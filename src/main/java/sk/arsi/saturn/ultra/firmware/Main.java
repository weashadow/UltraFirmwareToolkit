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
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String line = reader.readLine();

        while (line != null && !line.startsWith("%")) {
            if (line.startsWith("# File Partition:")) {
                String fileName = line.replace("# File Partition:", "").trim();

                if (!"set_config".equals(fileName)) {
                    System.out.println("Name:" + fileName);
                    line = reader.readLine();
                    while (line != null && !line.startsWith("fatload")) {
                        line = reader.readLine();
                    }
                    String[] split = line.split(" ");
                    String start = split[6];
                    String endx = split[5];
                    if (!"set_partition.es".equalsIgnoreCase(fileName)) {
                        records.add(new FileRecord(fileName, start, endx));
                    }
                }
            }
            // read next line
            line = reader.readLine();
        }

        reader.close();
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
        }
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

    private static class FileRecord {

        String name;
        int position;
        int size;

        public FileRecord(String name, String position, String size) {
            this.name = name;
            this.position = (int) Long.parseLong(position.replace("0x", ""), 16);
            this.size = (int) Long.parseLong(size.replace("0x", ""), 16);
            this.size = this.size - 8;
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
            return "FileRecord{" + "name=" + name + ", position=" + position + ", size=" + size + '}';
        }

    }

}
