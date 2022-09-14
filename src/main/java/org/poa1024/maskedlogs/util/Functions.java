package org.poa1024.maskedlogs.util;

import com.google.common.base.CaseFormat;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Functions {

    private final static Pattern LOWER_UNDERSCORE_PATTERN = Pattern.compile("^[a-z0-9_]*$");

    public static Stream<String> lowerUnderscoreToAllCaseFormats(String lowerUnderscoreText) {

        var mather = LOWER_UNDERSCORE_PATTERN.matcher(lowerUnderscoreText);

        if (!mather.find()) {
            throw new IllegalArgumentException(lowerUnderscoreText + " is not in lower underscore case. But should be!");
        }

        return Arrays
                .stream(CaseFormat.values())
                .map(f -> CaseFormat.LOWER_UNDERSCORE.to(f, lowerUnderscoreText));
    }

}
