package org.poa1024.maskedlogs;

import com.google.common.base.CaseFormat;
import org.poa1024.maskedlogs.masker.Masker;

import java.util.ArrayList;
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
            var matcher = pattern.matcher(res);

            List<Runnable> replacments = new ArrayList<>();

            int idxCorrection = 0;

            while (matcher.find() && matcher.groupCount() > 0) {
                var foundText = matcher.group(0);
                var value = matcher.group(1);
                var maskedValue = masker.mask(value);
                var replacement = foundText.replace(value, maskedValue);
                var idx = matcher.start() + idxCorrection;
                //we run this later, because if string length changes it will break matcher
                replacments.add(() -> res.replace(idx, idx + foundText.length(), replacement));
                //if string length has changed, we need to remember the shift
                idxCorrection = idxCorrection + (replacement.length() - foundText.length());
            }

            for (Runnable replacement : replacments) {
                replacement.run();
            }

        }
        return res.toString();
    }

    private static Stream<Pattern> getPatterns(String field) {
        var fieldRgx = Pattern.quote(field);
        return Stream.of(
                //json
                Pattern.compile(" *\"" + fieldRgx + "\" *: *\"(.*?)\""),
                Pattern.compile(" *\"" + fieldRgx + "\" *: *([^(\"| )].*?)([, }\n|\r])"),
                //map, url etc
                Pattern.compile(" *[^_-]" + fieldRgx + " *= *(.*?) *(,|}|&| |\\)|$|\n|\r)"),
                Pattern.compile("/" + fieldRgx + "/*(.*?)([/?$])")
        );
    }

    private static Stream<String> getAllCases(String lowerUnderscoreText) {
        return Arrays
                .stream(CaseFormat.values())
                .map(f -> CaseFormat.LOWER_UNDERSCORE.to(f, lowerUnderscoreText));
    }

}
