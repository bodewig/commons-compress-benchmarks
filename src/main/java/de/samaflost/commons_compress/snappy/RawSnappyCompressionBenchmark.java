package de.samaflost.commons_compress.snappy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.samaflost.commons_compress.TestFixture;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class RawSnappyCompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;

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
             SnappyCompressorOutputStream bout = new SnappyCompressorOutputStream(baos, data.length);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            return baos.toByteArray();
        }
    }
}
