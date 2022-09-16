package org.poa1024.maskedlogs.pattern;

import org.poa1024.maskedlogs.util.Constants;
import org.poa1024.maskedlogs.util.Functions;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldsPatternSupplier implements PatternSupplier {

    private final List<String> fields;

    public FieldsPatternSupplier(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public List<Pattern> get() {
        return fields
                .stream()
                .flatMap(Functions::lowerUnderscoreToAllCaseFormats)
                .distinct()
                .map(Pattern::quote)
                .flatMap(FieldsPatternSupplier::getPatterns)
                .collect(Collectors.toList());
    }

    private static Stream<Pattern> getPatterns(String field) {
        return Stream.of(
                getJsonPatterns(field),
                getUrlPathPatterns(field),
                getEqualSignPatterns(field)
        ).flatMap(s -> s);
    }

    private static Stream<Pattern> getJsonPatterns(String field) {
        return Stream.of(
                // { "order_id" : "123432" }
                Pattern.compile("\"" + field + "\" *: *\"(?<value>" + Constants.VALUE_PATTERN + "*?)\""),
                // { "order_id" : 123432 }
                Pattern.compile("\"" + field + "\" *: *(?<value>[^(\"| )]" + Constants.VALUE_PATTERN + "*?)([, }\n\r])"),
                // { order_id : "123432" }
                Pattern.compile("([,{ \n\r])" + field + " *: *\"(?<value>" + Constants.VALUE_PATTERN + "*?)\""),
                // { order_id : 123432 }
                Pattern.compile("([,{ \n\r])" + field + " *: *(?<value>[^(\"| )]" + Constants.VALUE_PATTERN + "*?)([, }\n\r])")
        );
    }

    private static Stream<Pattern> getUrlPathPatterns(String field) {
        return Stream.of(
                Pattern.compile("/" + field + "/*(?<value>.*?)(/|\\?|$|\n|\r)")
        );
    }

    //maps and url request parameters
    private static Stream<Pattern> getEqualSignPatterns(String field) {
        return Stream.of(
                Pattern.compile("(,|\\{|&|\\?| |\\(|^|\n|\r)+" + field + " *= *(?<value>.*?)(,|}|&| |\\)|$|\n|\r)+")
        );
    }

}
