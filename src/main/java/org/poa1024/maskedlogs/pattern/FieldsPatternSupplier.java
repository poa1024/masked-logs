package org.poa1024.maskedlogs.pattern;

import org.poa1024.maskedlogs.util.Constants;
import org.poa1024.maskedlogs.util.Functions;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Supplies regex patterns that matches fields names.
 * <p>
 * Fields should be provided only in the lower underscore case.
 * Other cases will be calculated automatically.
 */
public class FieldsPatternSupplier implements Supplier<Stream<Pattern>> {

    private final List<String> fields;
    private boolean jsonPatternsEnabled = true;
    private boolean urlPathPatternsEnabled = true;
    private boolean equalSignPatternsEnabled = true;

    /**
     * @param fields Fields in the lower underscore format.
     */
    public FieldsPatternSupplier(List<String> fields) {
        this.fields = fields;
    }

    /**
     * Enables masking json fields.
     */
    public void setJsonPatternsEnabled(boolean jsonPatternsEnabled) {
        this.jsonPatternsEnabled = jsonPatternsEnabled;
    }

    /**
     * Enables masking url path parameters (/field/value).
     */
    public void setUrlPathPatternsEnabled(boolean urlPathPatternsEnabled) {
        this.urlPathPatternsEnabled = urlPathPatternsEnabled;
    }

    /**
     * Enables equals sign parameters (field=value).
     */
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
                .flatMap(this::flatMapPatterns);
    }

    private Stream<Pattern> flatMapPatterns(String field) {
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
