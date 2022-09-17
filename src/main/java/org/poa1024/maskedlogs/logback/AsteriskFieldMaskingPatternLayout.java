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

public class AsteriskFieldMaskingPatternLayout extends PatternLayout {

    private String maskPercentage = "100.0";
    private final List<String> fieldsToMask = new ArrayList<>();

    private LogProcessor logProcessor;

    public void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = maskPercentage;
    }

    public void setFields(String fields) {
        this.fieldsToMask.addAll(Arrays.asList(fields.split(" *, *")));
    }

    public void setField(String field) {
        this.fieldsToMask.add(field);
    }

    @Override
    public void start() {
        try {
            this.logProcessor = new MaskingLogProcessor(
                    new FieldsPatternSupplier(fieldsToMask),
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
