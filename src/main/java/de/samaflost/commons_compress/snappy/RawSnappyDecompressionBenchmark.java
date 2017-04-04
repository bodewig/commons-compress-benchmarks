package de.samaflost.commons_compress.snappy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.samaflost.commons_compress.TestFixture;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class RawSnappyDecompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = compress(TestFixture.SMALL_FILE);
        BIGGER_FILE = compress(TestFixture.BIGGER_FILE);
    }

    @Benchmark
    public byte[] decompressSmallFile() throws Exception {
        return decompress(SMALL_FILE);
    }

    @Benchmark
    public byte[] decompressBiggerFile() throws Exception {
        return decompress(BIGGER_FILE);
    }

    private byte[] compress(byte[] data) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             SnappyCompressorOutputStream bout = new SnappyCompressorOutputStream(baos, data.length);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            bout.close();
            return baos.toByteArray();
        }
    }

    private byte[] decompress(byte[] data) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             SnappyCompressorInputStream bin = new SnappyCompressorInputStream(in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(bin, baos);
            return baos.toByteArray();
        }
    }
}
