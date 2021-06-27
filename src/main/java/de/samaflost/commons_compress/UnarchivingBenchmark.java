package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class UnarchivingBenchmark {

    private byte[] SMALL_FILE;
    private byte[] MANY_SMALL_FILES;
    private byte[] BIGGER_FILE;
    private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

    @Param({"zip"})
    public String format;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = ArchivingBenchmark.archive(TestFixture.SMALL_FILE, format, 1);
        BIGGER_FILE = ArchivingBenchmark.archive(TestFixture.BIGGER_FILE, format, 1);
        MANY_SMALL_FILES = ArchivingBenchmark.archive(TestFixture.SMALL_FILE, format, 10000);
    }

    @Benchmark
    public byte[] unarchiveSmallFile() throws Exception {
        return unarchive(SMALL_FILE);
    }

    @Benchmark
    public byte[] unarchiveManySmallFiles() throws Exception {
        return unarchive(MANY_SMALL_FILES);
    }

    @Benchmark
    public byte[] archiveBiggerFile() throws Exception {
        return unarchive(BIGGER_FILE);
    }

    @Benchmark
    public byte[] unarchiveManySmallFilesMetaDataOnly() throws Exception {
        return unarchiveMetadataOnly(MANY_SMALL_FILES);
    }

    private byte[] unarchive(byte[] data) throws Exception {
        switch(format) {
        case ArchiveStreamFactory.SEVEN_Z:
            return unarchive7z(data);
        default:
            return unarchiveStream(data);
        }
    }

    private byte[] unarchiveStream(byte[] data) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             ArchiveInputStream ain = factory.createArchiveInputStream(format, in);
             ) {
            byte[] lastEntryData = null;
            while (ain.getNextEntry() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    IOUtils.copy(ain, out);
                    out.close();
                    lastEntryData = out.toByteArray();
                }
            }
            return lastEntryData;
        }
    }

    private byte[] unarchive7z(byte[] data) throws Exception {
        try (SeekableInMemoryByteChannel ch = new SeekableInMemoryByteChannel(data);
             SevenZFile ain = new SevenZFile(ch);
             ) {
            final byte[] buffer = new byte[8192];
            byte[] lastEntryData = null;
            while (ain.getNextEntry() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    int n = 0;
                    while (-1 != (n = ain.read(buffer))) {
                        out.write(buffer, 0, n);
                    }
                    out.close();
                    lastEntryData = out.toByteArray();
                }
            }
            return lastEntryData;
        }
    }

    private byte[] unarchiveMetadataOnly(byte[] data) throws Exception {
        switch(format) {
        case ArchiveStreamFactory.SEVEN_Z:
            return unarchive7zMetadataOnly(data);
        default:
            return unarchiveStreamMetadataOnly(data);
        }
    }

    private byte[] unarchiveStreamMetadataOnly(byte[] data) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             ArchiveInputStream ain = factory.createArchiveInputStream(format, in);
             ) {
            while (ain.getNextEntry() != null) {
            }
            return new byte[0];
        }
    }

    private byte[] unarchive7zMetadataOnly(byte[] data) throws Exception {
        try (SeekableInMemoryByteChannel ch = new SeekableInMemoryByteChannel(data);
             SevenZFile ain = new SevenZFile(ch);
             ) {
            while (ain.getNextEntry() != null) {
            }
            return new byte[0];
        }
    }
}
