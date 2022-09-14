package org.poa1024.maskedlogs.masker;

/**
 * From value to v***e
 */
public class AsteriskMasker implements Masker {

    private final double maskPercentage;

    public AsteriskMasker(double maskPercentage) {
        if (maskPercentage <= 0 || maskPercentage > 100) {
            throw new IllegalArgumentException("maskPercentage should in a range 0 - 100");
        }
        this.maskPercentage = maskPercentage;
    }

    @Override
    public String mask(String s) {
        if (s == null || s.length() < 3) {
            return "***";
        }
        if (s.length() == 3) {
            return String.format("%s*%s", s.charAt(0), s.charAt(2));
        }

        var maskedLength = Math.floor(s.length() * maskPercentage / 100.0);
        var maskedIdx = (int) (Math.ceil(s.length() - maskedLength) / 2.0);

        StringBuilder res = new StringBuilder(s);
        for (int i = 0; i < maskedLength; i++) {
            res.setCharAt(maskedIdx + i, '*');
        }

        return res.toString();

    }
}
