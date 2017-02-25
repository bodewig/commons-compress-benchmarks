package de.samaflost.commons_compress;

import java.util.zip.Checksum;

import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ChecksumBenchmark {

    private byte[] SMALL_FILE;
    private byte[] BIGGER_FILE;

    @Param({"org.apache.commons.compress.compressors.lz4.XXHash32"})
    public String impl;

    private Checksum checksum;

    @Setup
    public void readData() throws Exception {
        SMALL_FILE = TestFixture.SMALL_FILE;
        BIGGER_FILE = TestFixture.BIGGER_FILE;
        checksum = (Checksum) Class.forName(impl).newInstance();
    }

    @Benchmark
    public long checksumSmallFile() throws Exception {
        return checksum(SMALL_FILE);
    }

    @Benchmark
    public long checksumBiggerFile() throws Exception {
        return checksum(BIGGER_FILE);
    }

    private long checksum(byte[] data) throws Exception {
        checksum.reset();
        checksum.update(data, 0, data.length);
        return checksum.getValue();
    }
}
