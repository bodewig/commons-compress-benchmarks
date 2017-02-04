package de.samaflost.commons_compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;

public class BZip2CompressionBenchmark {

    private static final byte[] SMALL_FILE;

    static {
        try {
            try (InputStream in = BZip2CompressionBenchmark.class.getResourceAsStream("/bla.tar")) {
                SMALL_FILE = IOUtils.toByteArray(in);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Benchmark
    public void compressSmallFile() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BZip2CompressorOutputStream bout = new BZip2CompressorOutputStream(baos);
             ByteArrayInputStream in = new ByteArrayInputStream(SMALL_FILE)) {
            IOUtils.copy(in, bout);
        }
    }

}
