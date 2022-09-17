package org.poa1024.maskedlogs.processor;

import org.poa1024.maskedlogs.masker.Masker;
import org.poa1024.maskedlogs.pattern.PatternSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MaskingLogProcessor implements LogProcessor {

    private final Masker masker;
    private final List<Pattern> patternsToMask;

    public MaskingLogProcessor(PatternSupplier patternSupplier, Masker masker) {
        this.masker = masker;
        this.patternsToMask = patternSupplier.get().toList();
    }

    @Override
    public String process(String log) {
        StringBuilder res = new StringBuilder(log);
        for (Pattern pattern : patternsToMask) {
            var matcher = pattern.matcher(res);

            List<Runnable> replacements = new ArrayList<>();

            int idxCorrection = 0;

            while (matcher.find() && matcher.groupCount() > 0) {
                var foundText = matcher.group(0);
                var value = matcher.group("value");
                var maskedValue = masker.mask(value);
                var replacement = foundText.replace(value, maskedValue);
                var idx = matcher.start() + idxCorrection;
                //we run this later, because if string length changes it will break matcher
                replacements.add(() -> res.replace(idx, idx + foundText.length(), replacement));
                //if string length has changed, we need to remember the shift
                idxCorrection = idxCorrection + (replacement.length() - foundText.length());
            }

            for (Runnable replacement : replacements) {
                replacement.run();
            }

        }
        return res.toString();
    }
}