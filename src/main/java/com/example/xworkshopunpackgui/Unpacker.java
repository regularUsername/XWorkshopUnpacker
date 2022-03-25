package com.example.xworkshopunpackgui;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class Unpacker {
    private static final int BUF_SIZE = 1024 * 512;
    private final File binfile;
    private long[] file_sizes;
    private String[] filenames;
    private long file_offset;
    private int file_count;

    public Unpacker(File binfile) {
        this.binfile = binfile;
    }

    public String[] getFilenames() {
        return filenames;
    }

    public void readHeader() throws IOException {
        try (var raf = new RandomAccessFile(binfile, "r")) {

            // read and check the header
            var sig = new byte[4];
            raf.read(sig);
            var h_version = raf.readInt();
            file_count = raf.readInt();
            var h_fname_len = raf.readInt();
            // convert to long because fsize is an unsigned integer
            var h_fsize = Integer.toUnsignedLong(raf.readInt());
            System.out.println("Signature: " + new String(sig));
            System.out.println("Version: " + h_version);
            System.out.println("File Count: " + file_count);
            System.out.println("File Size: " + h_fsize / 1024 + " KiB");

            if (!Arrays.equals(sig, (new byte[]{'X', 'R', 'W', 'S'}))) {
                throw new IOException("Invalid Header Signature");
            }
            if (h_version != 1) {
                throw new IOException("Invalid Header Version");
            }

            file_sizes = new long[file_count];
            for (int i = 0; i < file_count; i++) {
                file_sizes[i] = Integer.toUnsignedLong(raf.readInt());
            }
            // read and split the \0 seperated filenames
            var filenames_buf = new byte[h_fname_len];
            raf.read(filenames_buf);
            filenames = new String(filenames_buf).split("\0");
            file_offset = raf.getFilePointer();
        }
    }

    public void unpack(Path outDir) throws IOException {
        try (var raf = new RandomAccessFile(binfile, "r")) {
            raf.seek(file_offset);
            System.out.println("Creating Directory \"" + outDir + "\"");
            try {
                Files.createDirectory(outDir);
            } catch (final IOException e) {
                throw new IOException("Cant create directory. (maybe it exists already?)", e);
            }

            // copy files
            var buf = new byte[BUF_SIZE];
            for (int i = 0; i < file_count; i++) {
                var outfile = Paths.get(outDir.toString(), filenames[i]).toFile();
                System.out.println("Creating File: " + outfile + " (" + file_sizes[i] + " bytes)");
                try (var outputStream = new FileOutputStream(outfile)) {

                    for (var x = file_sizes[i]; x > 0; x -= buf.length) {
                        var byte_num = Math.toIntExact(Math.min(x, buf.length));
                        raf.read(buf, 0, byte_num);
                        outputStream.write(buf, 0, byte_num);
                    }
                }
            }

            //copy contents.xml
            var outfile = Paths.get(outDir.toString(), "contents.xml").toFile();
            System.out.println("Creating File: " + outfile);

            try(var outputStream = new FileOutputStream(outfile)) {
                int bytes_read;
                do {
                    bytes_read = raf.read(buf);
                    outputStream.write(buf, 0, bytes_read);
                } while (bytes_read >= buf.length);
            }
        }

    }

}
