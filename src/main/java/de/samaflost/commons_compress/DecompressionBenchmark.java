package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class DecompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;
    private final CompressorStreamFactory factory = new CompressorStreamFactory();

    @Param({"lz4-framed"})
    public String format;

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
        final String f = "deflate64".equals(format) ? "deflate" : format;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CompressorOutputStream bout = factory.createCompressorOutputStream(f, baos);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            bout.close();
            return baos.toByteArray();
        }
    }

    private byte[] decompress(byte[] data) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             CompressorInputStream bin = factory.createCompressorInputStream(format, in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(bin, baos);
            return baos.toByteArray();
        }
    }
}
