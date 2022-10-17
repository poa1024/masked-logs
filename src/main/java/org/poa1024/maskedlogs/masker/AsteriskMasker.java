package org.poa1024.maskedlogs.masker;

/**
 * Uses asterisks for masking.
 * Example: card_number=3454*****123.
 */
public class AsteriskMasker implements Masker {

    private final double maskPercentage;

    /**
     * @param maskPercentage Value from 0.00 to 100.00, defining the mask size (card_number=3454*****123 or
     *                       card_number=34*********3 or card_number=************).
     */
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

        var maskedLength = Math.floor(s.length() * maskPercentage / 100.0);
        var maskedIdx = (int) (Math.ceil(s.length() - maskedLength) / 2.0);

        StringBuilder res = new StringBuilder(s);
        for (int i = 0; i < maskedLength; i++) {
            res.setCharAt(maskedIdx + i, '*');
        }

        return res.toString();

    }
}
