package com.icthh.xm.ms.dashboard.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@UtilityClass
public class FileUtils {

    public static String readAsString(final String path) {
        return new String(readAsByteArray(path), UTF_8);
    }

    @SneakyThrows
    @SuppressFBWarnings("RR_NOT_CHECKED")
    public static byte[] readAsByteArray(final String path) {
        final InputStream resource = new ClassPathResource(path).getInputStream();
        final byte[] byteArray = new byte[resource.available()];

        resource.read(byteArray);

        return byteArray;
    }

}
