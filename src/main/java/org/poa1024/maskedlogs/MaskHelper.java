package org.poa1024.maskedlogs;

import com.google.common.base.CaseFormat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaskHelper {

    private final double maskPercentage;
    private final List<Pattern> patternsToMask;

    public MaskHelper(double maskPercentage, List<String> fieldsToMask) {
        this.maskPercentage = maskPercentage;
        this.patternsToMask = fieldsToMask
                .stream()
                .flatMap(MaskHelper::getAllCases)
                .distinct()
                .flatMap(MaskHelper::getPatterns)
                .collect(Collectors.toList());
    }

    public String maskText(String text) {
        StringBuilder res = new StringBuilder(text);
        for (Pattern pattern : patternsToMask) {
            var matcher = pattern.matcher(text);
            while (matcher.find() && matcher.groupCount() > 0) {
                var foundText = matcher.group(0);
                var value = matcher.group(1);
                var maskedValue = maskValue(value);
                var replacement = foundText.replace(value, maskedValue);
                var idx = matcher.start();
                for (int j = 0; j < foundText.length(); j++) {
                    res.setCharAt(idx + j, replacement.charAt(j));
                }
            }
        }
        return res.toString();
    }

    public String maskValue(String text) {
        if (text == null || text.length() < 3) {
            return "***";
        }
        if (text.length() == 3) {
            return String.format("%s*%s", text.charAt(0), text.charAt(2));
        }

        var maskedLength = Math.floor(text.length() * maskPercentage / 100.0);
        var maskedIdx = (int) (Math.ceil(text.length() - maskedLength) / 2.0);

        StringBuilder res = new StringBuilder(text);
        for (int i = 0; i < maskedLength; i++) {
            res.setCharAt(maskedIdx + i, '*');
        }

        return res.toString();

    }

    private static Stream<Pattern> getPatterns(String field) {
        return Stream.of(
                //json
                Pattern.compile(" *\"" + field + "\" *: *\"(.*?)\" *"),
                Pattern.compile(" *\"" + field + "\" *: *(.*?) *,"),
                //map, url etc
                Pattern.compile(" *[^_-]" + field + " *= *(.*?) *(,|}|&| |\\)|$|\n|\r)"),
                Pattern.compile("/" + field + "/*(.*?)([/?])")
        );
    }

    private static Stream<String> getAllCases(String lowerUnderscoreText) {
        return Arrays
                .stream(CaseFormat.values())
                .map(f -> CaseFormat.LOWER_UNDERSCORE.to(f, lowerUnderscoreText));
    }

}
