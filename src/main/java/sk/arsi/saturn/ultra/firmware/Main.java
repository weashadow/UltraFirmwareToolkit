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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.arsi.saturn.ultra.firmware.elements.FirmwareRoot;

/**
 *
 * @author arsi
 */
public class Main {

    private static final int HEADER_SIZE = 0x4000;
    private static final int ADD_TO_SIZE = 1048576 * 10;//10MB
    private static final String unit_size = "unit_size";
    private static final String block_size = "block_size";
    private static final String max_size = "max_size";
    private static final String dir_name = "dir_name";
    private static final Map<String, Long> maxSizes = new HashMap<>();
    private static final String buildLines = "sudo mkfs.ubifs -m unit_size -e block_size -c max_size -x lzo -o dir_name.out.es -r dir_name --space-fixup\n"
            + "crc32 dir_name.out.es | tr -d \"\\n\" > dir_name.out.es.crc\n"
            + "sudo chmod 666 dir_name.out.es\n";
    private static File file;

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
                file = new File(args[1]);
                if (!file.exists()) {
                    System.out.println("The file does not exist!");
                    System.out.println();
                    usage();
                    return;
                }
                FirmwareRoot firmwareRoot = FimwareUtils.parseElements(file);
                System.exit(0);
                extract(file);
                break;
            case "-build":
                if (args.length != 2) {
                    System.out.println("Missing file name!");
                    System.out.println();
                    usage();
                    return;
                }
                file = new File(args[1]);
                if (!file.exists()) {
                    System.out.println("The file does not exist!");
                    System.out.println();
                    usage();
                    return;
                }
                build(file);
                break;
            case "-validate":
                break;
        }
    }

    public static void usage() {
        System.out.println("Elegoo firmware utils");
        System.out.println("Usage:");
        System.out.println("-extract filename>");
        System.out.println("-build <original-firmware-filename>");
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
        int subIndex = 0;
        String fileName = "";
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String line = reader.readLine();
        long lineStart = position;
        long lineLength = line.length();
        position += lineLength;
        while (line != null && !line.startsWith("%")) {
            if (line.startsWith("# File Partition:")) {
                fileName = line.replace("# File Partition:", "").trim();
                subIndex = 0;
                if (!"set_config".equals(fileName)) {
                    System.out.println("Name:" + fileName);
                    line = reader.readLine();
                    lineStart = position;
                    lineLength = line.length();
                    position += lineLength;
                    while (line != null && !line.startsWith("fatload")) {
                        if (line.trim().startsWith("ubi create")) {
                            String tmp = line.trim().replace("ubi create ", "");
                            String[] split = tmp.split(" ");
                            String name = split[0];
                            String maxSize = split[1];
                            long size = Long.parseUnsignedLong(maxSize.replace("0x", ""), 16);
                            maxSizes.put(name, size);
                            size = size / 1024;
                            size = size / 1024;
                            System.out.print("Partition: " + name);
                            System.out.println("     Max. Size: " + size + "MB");
                        }
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
            } else if (line.startsWith("fatload usb")) {
                subIndex++;
                String[] split = line.split(" ");
                String start = split[6];
                String endx = split[5];
                if (!"set_partition.es".equalsIgnoreCase(fileName)) {
                    records.add(new FileRecord(fileName + "." + subIndex, start, endx, lineStart, lineLength, line));
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

    private static void build(File src) throws IOException {
        List<FileRecord> records = new ArrayList<>();
        System.out.println("********************************************************************");
        File directory = file.getParentFile();
        String build = "";
        parseHeader(src, records);
        records.sort(new Comparator<FileRecord>() {
            @Override
            public int compare(FileRecord o1, FileRecord o2) {
                Integer pos1 = o1.position;
                Integer pos2 = o2.position;
                return pos1.compareTo(pos2);
            }
        });
        ByteBuffer buffer = ByteBuffer.allocate((int) src.length() + ADD_TO_SIZE);
        for (int i = 0; i < records.size(); i++) {
            FileRecord record = records.get(i);
            System.out.println("********************************************************************");
            System.out.println("Disk image file: " + record.getName());
            File original = new File(directory, record.getName());
            File custom = new File(directory, record.getName().replace(".es", ".out.es"));
            File originalCrc = new File(directory, record.getName() + ".crc");
            File customCrc = new File(directory, record.getName().replace(".es", ".out.es") + ".crc");
            boolean useCustom = false;
            if (custom.exists() && !sameFile(originalCrc, customCrc)) {
                if (original.length() == custom.length()) {
                    useCustom = keyboard("The " + custom.getName() + " has same size as original. Do you wish to use the " + custom.getName() + " file?");
                }
            }
            if (useCustom) {
                addFile(buffer, custom, customCrc, record);
            } else {
                addFile(buffer, original, originalCrc, record);
            }

        }
        System.out.println("********************************************************************");
        System.out.println("Original firmware size: " + src.length());
        long size = buffer.position() + HEADER_SIZE;
        System.out.println("New firmware size: " + size);
    }

    private static void addFile(ByteBuffer buffer, File image, File imageCrc, FileRecord record) throws IOException {
        ByteBuffer fileData = bufferFile(image);
        ByteBuffer fileCrc = bufferFile(imageCrc);
        int startPosition = buffer.position();
        buffer.put(fileData);
        buffer.put(fileCrc);
        record.setNewPosition(startPosition + HEADER_SIZE);
        record.setNewLength((int) image.length());
        System.out.println("Start: " + String.format("0x%08X", record.position) + " > " + String.format("0x%08X", record.newPosition));
        System.out.println("Length: " + String.format("0x%08X", record.size) + " > " + String.format("0x%08X", record.newLength));
        while ((buffer.position() & 0xfff) > 0) {
            buffer.put((byte) 0xff);
        }
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static boolean keyboard(String prompt) {
        System.out.println(prompt + " [" + "y" + "/" + "N" + "]: ");
        while (true) {
            String option = scanner.nextLine();
            if ("y".equalsIgnoreCase(option)) {
                return true;
            } else if ("n".equalsIgnoreCase(option)) {
                return false;
            } else if ("".equals(option)) {
                return false;
            };
        }
    }

    public static boolean sameFile(File a, File b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.getAbsolutePath().equals(b.getAbsolutePath())) {
            return true;
        }

        if (!a.exists() || !b.exists()) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        boolean eq = true;

        FileChannel channelA;
        FileChannel channelB;
        try {
            channelA = new RandomAccessFile(a, "r").getChannel();
            channelB = new RandomAccessFile(b, "r").getChannel();

            long channelsSize = channelA.size();
            ByteBuffer buff1 = channelA.map(FileChannel.MapMode.READ_ONLY, 0, channelsSize);
            ByteBuffer buff2 = channelB.map(FileChannel.MapMode.READ_ONLY, 0, channelsSize);
            for (int i = 0; i < channelsSize; i++) {
                if (buff1.get(i) != buff2.get(i)) {
                    eq = false;
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
        return eq;
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
        int newPosition;
        int newLength;

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

        public int getNewPosition() {
            return newPosition;
        }

        public void setNewPosition(int newPosition) {
            this.newPosition = newPosition;
        }

        public int getNewLength() {
            return newLength;
        }

        public void setNewLength(int newLength) {
            this.newLength = newLength;
        }

    }

}
