package org.poa1024.maskedlogs.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.poa1024.maskedlogs.masker.AsteriskMasker;
import org.poa1024.maskedlogs.processor.LogProcessor;
import org.poa1024.maskedlogs.processor.LogProcessorWithInitializationException;
import org.poa1024.maskedlogs.processor.MaskingLogProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AsteriskMaskingPatternLayout extends PatternLayout {

    private volatile String maskPercentage = "100.0";
    private final List<String> patterns = new ArrayList<>();

    private volatile LogProcessor logProcessor;

    public synchronized void setMaskPercentage(String maskPercentage) {
        this.maskPercentage = maskPercentage;
    }

    public synchronized void setRegexPattern(String pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        if (logProcessor == null) {
            synchronized (this) {
                if (logProcessor == null) {
                    try {
                        logProcessor = new MaskingLogProcessor(
                                () -> patterns.stream().map(Pattern::compile).toList(),
                                new AsteriskMasker(Double.parseDouble(maskPercentage))
                        );
                    } catch (Exception e) {
                        logProcessor = new LogProcessorWithInitializationException(getClass(), e);
                    }
                }
            }
        }
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        return logProcessor.process(message);
    }
}
