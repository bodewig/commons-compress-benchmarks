package de.samaflost.commons_compress;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TestFixture {

    public static final byte[] SMALL_FILE;
    public static final byte[] BIGGER_FILE;

    static {
        try {
            try (InputStream in = TestFixture.class.getResourceAsStream("/bla.tar")) {
                SMALL_FILE = IOUtils.toByteArray(in);
            }
            try (InputStream fromUrl = new URL("http://archive.apache.org/dist/commons/compress/source/commons-compress-1.13-src.tar.gz")
                     .openStream();
                 GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fromUrl)) {
                BIGGER_FILE = IOUtils.toByteArray(gzIn);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
