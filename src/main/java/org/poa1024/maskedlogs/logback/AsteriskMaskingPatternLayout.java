package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.MaskingTextProcessor;
import org.poa1024.maskedlogs.masker.AsteriskMasker;
import org.poa1024.maskedlogs.pattern.FieldsPatternSupplier;

import java.util.Arrays;
import java.util.List;

/**
 * Logback pattern layout masking sensitive data.
 */
public class AsteriskMaskingPatternLayout extends PatternLayout {

    private volatile double maskPercentage = 60.0;
    private volatile List<String> fieldsToMask;

    private volatile MaskingTextProcessor maskingTextProcessor;

    public synchronized void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = Double.parseDouble(maskPercentage);
    }

    public synchronized void setFieldsToMask(String fieldsToMask) {
        this.fieldsToMask = Arrays.asList(fieldsToMask.split(" *, *"));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (maskingTextProcessor == null) {
            synchronized (this) {
                if (maskingTextProcessor == null) {
                    maskingTextProcessor = new MaskingTextProcessor(
                            new FieldsPatternSupplier(fieldsToMask),
                            new AsteriskMasker(maskPercentage)
                    );
                }
            }
        }
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        return maskingTextProcessor.mask(message);
    }
}
