package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.masker.AsteriskMasker;
import org.poa1024.maskedlogs.pattern.FieldsPatternSupplier;
import org.poa1024.maskedlogs.processor.LogProcessor;
import org.poa1024.maskedlogs.processor.LogProcessorWithInitializationException;
import org.poa1024.maskedlogs.processor.MaskingLogProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pattern masking layout configurable by field names.
 */
public class AsteriskFieldMaskingPatternLayout extends PatternLayout {

    private String maskPercentage = "100.0";
    private final List<String> fieldsToMask = new ArrayList<>();

    private boolean jsonPatternsEnabled = true;
    private boolean equalSignPatternsEnabled = true;
    private boolean urlPathPatternsEnabled = false;

    private LogProcessor logProcessor;

    /**
     * Value from 0.00 to 100.00, defining the mask size (card_number=3454*****123 or
     * card_number=34*********3 or card_number=************).
     * Default value is 100.00 (full value is masked).
     */
    public void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = maskPercentage;
    }

    /**
     * Adds fields, divided by comma, that will be masked.
     */
    public void setFields(String fields) {
        this.fieldsToMask.addAll(Arrays.asList(fields.split(" *, *")));
    }

    /**
     * Adds field that will be masked.
     */
    public void setField(String field) {
        this.fieldsToMask.add(field);
    }

    /**
     * Enables masking json fields.
     * Default value: true.
     */
    public void setJsonPatternsEnabled(boolean jsonPatternsEnabled) {
        this.jsonPatternsEnabled = jsonPatternsEnabled;
    }

    /**
     * Enables masking url path parameters (/field/value).
     * Default value: false.
     */
    public void setUrlPathPatternsEnabled(boolean urlPathPatternsEnabled) {
        this.urlPathPatternsEnabled = urlPathPatternsEnabled;
    }

    /**
     * Enables equals sign parameters (field=value).
     * Default value: true.
     */
    public void setEqualSignPatternsEnabled(boolean equalSignPatternsEnabled) {
        this.equalSignPatternsEnabled = equalSignPatternsEnabled;
    }

    @Override
    public void start() {
        try {
            var patternSupplier = new FieldsPatternSupplier(fieldsToMask);
            patternSupplier.setJsonPatternsEnabled(jsonPatternsEnabled);
            patternSupplier.setEqualSignPatternsEnabled(equalSignPatternsEnabled);
            patternSupplier.setUrlPathPatternsEnabled(urlPathPatternsEnabled);
            this.logProcessor = new MaskingLogProcessor(
                    patternSupplier,
                    new AsteriskMasker(Double.parseDouble(maskPercentage))
            );
        } catch (Exception e) {
            this.logProcessor = new LogProcessorWithInitializationException(getClass(), e);
        }
        super.start();
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return logProcessor.process((super.doLayout(event)));
    }

}
