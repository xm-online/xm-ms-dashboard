package com.icthh.xm.ms.dashboard.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;


@UtilityClass
public class FileUtils {

    public static String readAsString(final String path) {
        return new String(readAsByteArray(path), UTF_8);
    }

    @SneakyThrows
    public static byte[] readAsByteArray(final String path) {
        final InputStream resource = new ClassPathResource(path).getInputStream();
        final byte[] byteArray = new byte[resource.available()];

        resource.read(byteArray);

        return byteArray;
    }

}
