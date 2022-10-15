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

/**
 * Pattern masking layout configurable by regex patterns.
 */
public class AsteriskMaskingPatternLayout extends PatternLayout {

    private String maskPercentage = "100.0";
    private final List<String> patterns = new ArrayList<>();

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
     * Adds regex pattern for masking.
     * Regex group `value` will be used for masking.
     * For example: order_id=value_that_we_want_to_mask, {@code .*order_id=(?<value>[0-9a-z]*)}
     */
    public void setRegexPattern(String pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public void start() {
        try {
            this.logProcessor = new MaskingLogProcessor(
                    () -> patterns.stream().map(Pattern::compile),
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
