package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ArchivingBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;
    private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

    @Param({"zip"})
    public String format;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = TestFixture.SMALL_FILE;
        BIGGER_FILE = TestFixture.BIGGER_FILE;
    }

    @Benchmark
    public byte[] archiveSmallFile() throws Exception {
        return archive(SMALL_FILE);
    }

    @Benchmark
    public byte[] archiveManySmallFiles() throws Exception {
        return archive(SMALL_FILE, format, 10000);
    }

    @Benchmark
    public byte[] archiveSomeSmallFiles() throws Exception {
        return archive(SMALL_FILE, format, 100);
    }

    @Benchmark
    public byte[] archiveBiggerFile() throws Exception {
        return archive(BIGGER_FILE);
    }

    private byte[] archive(byte[] data) throws Exception {
        return archive(data, format, 1);
    }

    static byte[] archive(byte[] data, String format, int numberOfEntries) throws Exception {
        switch(format) {
        case ArchiveStreamFactory.SEVEN_Z:
            return archive7z(data, format, numberOfEntries);
        default:
            return archiveStream(data, format, numberOfEntries);
        }
    }

    private static byte[] archiveStream(byte[] data, String format, int numberOfEntries) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ArchiveOutputStream bout = factory.createArchiveOutputStream(format, baos);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            for (int i = 0; i < numberOfEntries; i++) {
                bout.putArchiveEntry(createEntry(format, i, data.length));
                IOUtils.copy(in, bout);
                bout.closeArchiveEntry();
            }
            bout.finish();
            bout.close();
            return baos.toByteArray();
        }
    }

    private static byte[] archive7z(byte[] data, String format, int numberOfEntries) throws Exception {
        try (SeekableInMemoryByteChannel ch = new SeekableInMemoryByteChannel();
             SevenZOutputFile bout = new SevenZOutputFile(ch);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            final byte[] buffer = new byte[8192];
            for (int i = 0; i < numberOfEntries; i++) {
                bout.putArchiveEntry(createEntry(format, i, data.length));
                int n = 0;
                while (-1 != (n = in.read(buffer))) {
                    bout.write(buffer, 0, n);
                }
                bout.closeArchiveEntry();
            }
            bout.finish();
            bout.close();
            return Arrays.copyOf(ch.array(), (int) ch.size());
        }
    }

    private static ArchiveEntry createEntry(String format, int entryNumber, long length) {
        String entryName = format + "." + entryNumber;
        switch(format) {
        case ArchiveStreamFactory.AR:
            return new ArArchiveEntry(entryName, length);
        case ArchiveStreamFactory.CPIO:
            return new CpioArchiveEntry(entryName, length);
        case ArchiveStreamFactory.JAR:
        case ArchiveStreamFactory.ZIP:
            return new ZipArchiveEntry(entryName);
        case ArchiveStreamFactory.TAR:
            return new TarArchiveEntry(entryName);
        case ArchiveStreamFactory.SEVEN_Z:
            SevenZArchiveEntry e = new SevenZArchiveEntry();
            e.setName(entryName);
            return e;
        default:
            throw new RuntimeException("unsupported format " + format);
        }
    }
}
