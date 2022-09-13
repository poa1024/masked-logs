package org.poa1024.maskedlogs;

import com.google.common.base.CaseFormat;
import org.poa1024.maskedlogs.masker.Masker;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldMasker {

    private final Masker masker;
    private final List<Pattern> patternsToMask;

    public FieldMasker(List<String> fieldsToMask, Masker masker) {
        this.masker = masker;
        this.patternsToMask = fieldsToMask
                .stream()
                .flatMap(FieldMasker::getAllCases)
                .distinct()
                .flatMap(FieldMasker::getPatterns)
                .collect(Collectors.toList());
    }

    public String mask(String text) {
        StringBuilder res = new StringBuilder(text);
        for (Pattern pattern : patternsToMask) {
            var matcher = pattern.matcher(text);
            while (matcher.find() && matcher.groupCount() > 0) {
                var foundText = matcher.group(0);
                var value = matcher.group(1);
                var maskedValue = masker.mask(value);
                var replacement = foundText.replace(value, maskedValue);
                var idx = matcher.start();
                for (int j = 0; j < foundText.length(); j++) {
                    res.setCharAt(idx + j, replacement.charAt(j));
                }
            }
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
