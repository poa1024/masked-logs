package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.masker.AsteriskMasker;
import org.poa1024.maskedlogs.processor.LogProcessor;
import org.poa1024.maskedlogs.processor.MaskingLogProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AsteriskMaskingPatternLayout extends PatternLayout {

    private volatile double maskPercentage = 100.0;
    private final List<Pattern> patterns = new ArrayList<>();

    private volatile LogProcessor logProcessor;

    public synchronized void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = Double.parseDouble(maskPercentage);
    }

    public synchronized void setRegexPattern(String pattern) {
        this.patterns.add(Pattern.compile(pattern));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (logProcessor == null) {
            synchronized (this) {
                if (logProcessor == null) {
                    logProcessor = new MaskingLogProcessor(
                            () -> patterns,
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
