package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BZip2CompressionBenchmark {

    private final byte[] SMALL_FILE;
    private final byte[] BIGGER_FILE;

    @Setup
    public void readData() throws Exception {
        System.err.println("init");
        try (InputStream in = BZip2CompressionBenchmark.class.getResourceAsStream("/bla.tar")) {
            SMALL_FILE = IOUtils.toByteArray(in);
        }
        try (InputStream fromUrl = new URL("http://archive.apache.org/dist/commons/compress/source/commons-compress-1.13-src.tar.gz")
                 .openStream();
             GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fromUrl)) {
            BIGGER_FILE = IOUtils.toByteArray(gzIn);
        }
    }

    @Benchmark
    public void compressSmallFile() throws IOException {
        compress(SMALL_FILE);
    }

    @Benchmark
    public void compressBiggerFile() throws IOException {
        compress(BIGGER_FILE);
    }

    private void compress(byte[] data) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BZip2CompressorOutputStream bout = new BZip2CompressorOutputStream(baos);
             ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            IOUtils.copy(in, bout);
        }
    }

}
