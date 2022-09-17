package org.poa1024.maskedlogs.pattern;

import org.poa1024.maskedlogs.util.Constants;
import org.poa1024.maskedlogs.util.Functions;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FieldsPatternSupplier implements PatternSupplier {

    private final List<String> fields;
    private boolean jsonPatternsEnabled = true;
    private boolean urlPathPatternsEnabled = true;
    private boolean equalSignPatternsEnabled = true;

    public FieldsPatternSupplier(List<String> fields) {
        this.fields = fields;
    }

    public void setJsonPatternsEnabled(boolean jsonPatternsEnabled) {
        this.jsonPatternsEnabled = jsonPatternsEnabled;
    }

    public void setUrlPathPatternsEnabled(boolean urlPathPatternsEnabled) {
        this.urlPathPatternsEnabled = urlPathPatternsEnabled;
    }

    public void setEqualSignPatternsEnabled(boolean equalSignPatternsEnabled) {
        this.equalSignPatternsEnabled = equalSignPatternsEnabled;
    }

    @Override
    public Stream<Pattern> get() {
        return fields
                .stream()
                .flatMap(Functions::lowerUnderscoreToAllCaseFormats)
                .distinct()
                .map(Pattern::quote)
                .flatMap(this::getPatterns);
    }

    private Stream<Pattern> getPatterns(String field) {
        return Stream.of(
                jsonPatternsEnabled ? getJsonPatterns(field) : Stream.<Pattern>empty(),
                urlPathPatternsEnabled ? getUrlPathPatterns(field) : Stream.<Pattern>empty(),
                equalSignPatternsEnabled ? getEqualSignPatterns(field) : Stream.<Pattern>empty()
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
