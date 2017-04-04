package de.samaflost.commons_compress.lz4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.samaflost.commons_compress.TestFixture;

import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BlockLZ4CompressionBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = TestFixture.SMALL_FILE;
        BIGGER_FILE = TestFixture.BIGGER_FILE;
    }

    @Param({"default"})
    public String config;

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
             BlockLZ4CompressorOutputStream bout = new BlockLZ4CompressorOutputStream(baos, getParameters());
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
            return baos.toByteArray();
        }
    }

    private Parameters getParameters() {
        switch (config) {
        case "speed":
            return BlockLZ4CompressorOutputStream.createParameterBuilder().tunedForSpeed().build();
        case "compression":
            return BlockLZ4CompressorOutputStream.createParameterBuilder().tunedForCompressionRatio().build();
        case "default":
        default:
            return BlockLZ4CompressorOutputStream.createParameterBuilder().build();
        }
    }
}
