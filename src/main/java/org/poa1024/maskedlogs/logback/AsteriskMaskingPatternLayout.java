package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.FieldMasker;
import org.poa1024.maskedlogs.masker.AsteriskMasker;

import java.util.Arrays;
import java.util.List;

/**
 * Logback pattern layout masking sensitive data.
 */
public class AsteriskMaskingPatternLayout extends PatternLayout {

    private volatile double maskPercentage;
    private volatile List<String> fieldsToMask;

    private volatile FieldMasker fieldMasker;

    public synchronized void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = Double.parseDouble(maskPercentage);
    }

    public synchronized void setFieldsToMask(String fieldsToMask) {
        this.fieldsToMask = Arrays.asList(fieldsToMask.split(" *, *"));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (fieldMasker == null) {
            synchronized (this) {
                if (fieldMasker == null) {
                    fieldMasker = new FieldMasker(fieldsToMask, new AsteriskMasker(maskPercentage));
                }
            }
        }
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        return fieldMasker.mask(message);
    }
}
