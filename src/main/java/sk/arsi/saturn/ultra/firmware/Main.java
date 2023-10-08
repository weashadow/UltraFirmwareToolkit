/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.arsi.saturn.ultra.firmware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author arsi
 */
public class Main {

    private static final long max_size_rootfs = 0x2C00000; //44 MB
    private static final long max_size_miservice = 0xA00000;//10 MB
    private static final long max_size_customer = 0x2800000;//40 MB
    private static final long max_size_appconfigs = 0x400000;//4 MB
    private static final long max_size_parameter = 0x400000;//4 MB
    private static final String unit_size = "unit_size";
    private static final String block_size = "block_size";
    private static final String max_size = "max_size";
    private static final String dir_name = "dir_name";
    private static final String buildLines = "sudo mkfs.ubifs -m unit_size -e block_size -c max_size -x lzo -o dir_name.out.es -r dir_name --space-fixup\n"
            + "crc32 dir_name.out.es | tr -d \"\\n\" > dir_name.out.es.crc\n"
            + "sudo chmod 666 dir_name.out.es\n";

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            usage();
            return;
        }
        switch (args[0]) {
            case "-extract":
                if (args.length != 2) {
                    System.out.println("Missing file name!");
                    System.out.println();
                    usage();
                    return;
                }
                File file = new File(args[1]);
                if (!file.exists()) {
                    System.out.println("The file does not exist!");
                    System.out.println();
                    usage();
                    return;
                }
                extract(file);
                break;
            case "-build":
                if (args.length != 2) {
                    System.out.println("Missing file name!");
                    System.out.println();
                    usage();
                    return;
                }
                break;
            case "-validate":
                break;
        }
    }

    public static void usage() {
        System.out.println("Elegoo firmware utils");
        System.out.println("Usage:");
        System.out.println("-extract <filename>");
        System.out.println("-build <filename>");
        System.out.println("-validate");
    }

    public static void extract(File src) throws FileNotFoundException, IOException {
        List<FileRecord> records = new ArrayList<>();
        String build = "";
        parseHeader(src, records);
        ByteBuffer buffer = bufferFile(src);
        for (int i = 0; i < records.size(); i++) {
            FileRecord record = records.get(i);
            buffer.position(record.position);
            byte[] data = new byte[record.size];
            byte[] dataCrc = new byte[8];//crc32 as hex string -> 8byte
            buffer.get(data, 0, record.size);
            buffer.get(dataCrc, 0, 8);
            File tmp = new File(src.getParentFile(), record.getName());
            File tmpCrc = new File(src.getParentFile(), record.getName() + ".crc");
            FileOutputStream fos = new FileOutputStream(tmp);
            FileOutputStream fosCrc = new FileOutputStream(tmpCrc);
            fos.write(data);
            fosCrc.write(dataCrc);
            fos.close();
            fosCrc.close();
            switch (record.getName()) {
                case "rootfs.es":
                case "miservice.es":
                case "customer.es":
                case "appconfigs.es":
                case "parameter.es": {
                    FlasfInfo flasfInfo = execCmd("binwalk -B " + tmp.getAbsolutePath());
                    if (flasfInfo.isEmpty()) {
                        System.out.println("Unable to analyse binwalk output!");
                        System.exit(10);
                    }
                    build += buildLines.replace(unit_size, flasfInfo.unitSize)
                            .replace(block_size, flasfInfo.blockSize)
                            .replace(max_size, flasfInfo.maxBlocks)
                            .replace(dir_name, record.getName().replace(".es", ""));
                }
                break;
                default:
                    break;
            }
        }
        File tmp = new File(src.getParentFile(), "build.sh");
        tmp.setExecutable(true, false);
        FileOutputStream fos = new FileOutputStream(tmp);
        fos.write(build.getBytes());
        fos.close();
    }

    public static FlasfInfo execCmd(String cmd) {
        FlasfInfo tmp = new FlasfInfo();
        try {
            java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
            String out = s.hasNext() ? s.next() : "";
            tmp.setMaxBlocks(extractPattern(out, "max erase blocks: [0-9]+"));
            tmp.setUnitSize(extractPattern(out, "min I/O unit size: [0-9]+"));
            tmp.setBlockSize(extractPattern(out, "erase block size: [0-9]+"));
        } catch (Exception exception) {
            System.out.println("Unable to execute binwalk! Please check if it's installed...");
            System.exit(5);
        }
        return tmp;
    }

    public static String extractPattern(String out, String patternTxt) {
        Pattern pattern = Pattern.compile(patternTxt, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(out);
        if (matcher.find()) {
            String group = matcher.group(0);
            return (group.replace(patternTxt.split(":")[0] + ": ", ""));
        }
        return "";
    }

    public static void parseHeader(File src, List<FileRecord> records) throws FileNotFoundException, IOException {
        long position = 0;
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String line = reader.readLine();
        long lineStart = position;
        long lineLength = line.length();
        position += lineLength;
        while (line != null && !line.startsWith("%")) {
            if (line.startsWith("# File Partition:")) {
                String fileName = line.replace("# File Partition:", "").trim();

                if (!"set_config".equals(fileName)) {
                    System.out.println("Name:" + fileName);
                    line = reader.readLine();
                    lineStart = position;
                    lineLength = line.length();
                    position += lineLength;
                    while (line != null && !line.startsWith("fatload")) {
                        line = reader.readLine();
                        lineStart = position;
                        lineLength = line.length();
                        position += lineLength;
                    }
                    String[] split = line.split(" ");
                    String start = split[6];
                    String endx = split[5];
                    if (!"set_partition.es".equalsIgnoreCase(fileName)) {
                        records.add(new FileRecord(fileName, start, endx, lineStart, lineLength, line));
                    }
                }
            }
            // read next line
            line = reader.readLine();
            lineStart = position;
            lineLength = line.length();
            position += lineLength;
        }

        reader.close();
    }

    public static ByteBuffer bufferFile(File file) throws IOException {
        long size = file.length();

        ByteBuffer buf = ByteBuffer.allocate((int) (size & 0x7FFFFFFF));
        FileChannel chan = new FileInputStream(file).getChannel();
        while (buf.remaining() > 0) {
            int n = chan.read(buf);
            if (n <= 0) {
                throw new IOException("Read operation failed.");
            }
        }

        chan.close();
        buf.flip();
        return buf;
    }

    private static class FlasfInfo {

        String unitSize = null;
        String blockSize = null;
        String maxBlocks = null;

        public FlasfInfo(String unitSize, String blockSize, String maxBlocks) {
            this.unitSize = unitSize;
            this.blockSize = blockSize;
            this.maxBlocks = maxBlocks;
        }

        public boolean isEmpty() {
            return unitSize == null || blockSize == null || maxBlocks == null;
        }

        public FlasfInfo() {
        }

        public String getUnitSize() {
            return unitSize;
        }

        public void setUnitSize(String unitSize) {
            this.unitSize = unitSize;
        }

        public String getBlockSize() {
            return blockSize;
        }

        public void setBlockSize(String blockSize) {
            this.blockSize = blockSize;
        }

        public String getMaxBlocks() {
            return maxBlocks;
        }

        public void setMaxBlocks(String maxBlocks) {
            this.maxBlocks = maxBlocks;
        }

    }

    private static class FileRecord {

        String name;
        int position;
        int size;
        long lineStart;
        long lineLength;
        String line;

        public FileRecord(String name, String position, String size, long lineStart, long lineLength, String line) {
            this.name = name;
            this.position = (int) Long.parseLong(position.replace("0x", ""), 16);
            this.size = (int) Long.parseLong(size.replace("0x", ""), 16);
            this.size = this.size - 8;
            this.lineStart = lineStart;
            this.lineLength = lineLength;
            this.line = line;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getPosition() {
            return position;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "FileRecord{" + "name=" + name + ", position=" + position + ", size=" + size + ", lineStart=" + lineStart + ", lineLength=" + lineLength + '}';
        }

        public long getLineStart() {
            return lineStart;
        }

        public void setLineStart(long lineStart) {
            this.lineStart = lineStart;
        }

        public long getLineLength() {
            return lineLength;
        }

        public void setLineLength(long lineLength) {
            this.lineLength = lineLength;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

    }

}
