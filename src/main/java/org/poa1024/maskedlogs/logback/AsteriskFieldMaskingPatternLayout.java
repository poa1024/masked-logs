package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.masker.AsteriskMasker;
import org.poa1024.maskedlogs.pattern.FieldsPatternSupplier;
import org.poa1024.maskedlogs.processor.LogProcessor;
import org.poa1024.maskedlogs.processor.MaskingLogProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsteriskFieldMaskingPatternLayout extends PatternLayout {

    private volatile double maskPercentage = 100.0;
    private final List<String> fieldsToMask = new ArrayList<>();

    private volatile LogProcessor logProcessor;

    public synchronized void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = Double.parseDouble(maskPercentage);
    }

    public synchronized void setFields(String fields) {
        this.fieldsToMask.addAll(Arrays.asList(fields.split(" *, *")));
    }

    public synchronized void setField(String field) {
        this.fieldsToMask.add(field);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (logProcessor == null) {
            synchronized (this) {
                if (logProcessor == null) {
                    logProcessor = new MaskingLogProcessor(
                            new FieldsPatternSupplier(fieldsToMask),
                            new AsteriskMasker(maskPercentage)
                    );
                }
            }
        }
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        return logProcessor.process(message);
    }
}
