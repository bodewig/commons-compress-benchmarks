package de.samaflost.commons_compress.lz4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.samaflost.commons_compress.TestFixture;

import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BlockLZ4DecompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = compress(TestFixture.SMALL_FILE);
        BIGGER_FILE = compress(TestFixture.BIGGER_FILE);
    }

    @Benchmark
    public byte[] decompressSmallFile() throws Exception {
        return decompress(compress(SMALL_FILE));
    }

    @Benchmark
    public byte[] decompressBiggerFile() throws Exception {
        return decompress(compress(BIGGER_FILE));
    }

    private byte[] compress(byte[] data) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BlockLZ4CompressorOutputStream bout = new BlockLZ4CompressorOutputStream(baos);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            return baos.toByteArray();
        }
    }

    private byte[] decompress(byte[] data) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data);
             BlockLZ4CompressorInputStream bin = new BlockLZ4CompressorInputStream(in);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(bin, baos);
            return baos.toByteArray();
        }
    }
}
