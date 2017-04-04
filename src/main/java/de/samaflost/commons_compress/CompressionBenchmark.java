package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class CompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;
    private final CompressorStreamFactory factory = new CompressorStreamFactory();

    @Param({"lz4-framed"})
    public String format;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = TestFixture.SMALL_FILE;
        BIGGER_FILE = TestFixture.BIGGER_FILE;
    }

    @Benchmark
    public byte[] compressSmallFile() throws Exception {
        return compress(SMALL_FILE);
    }

    @Benchmark
    public byte[] compressBiggerFile() throws Exception {
        return compress(BIGGER_FILE);
    }

    private byte[] compress(byte[] data) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CompressorOutputStream bout = factory.createCompressorOutputStream(format, baos);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            bout.close();
            return baos.toByteArray();
        }
    }
}
