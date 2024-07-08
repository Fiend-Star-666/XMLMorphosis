package org.xml_to_db.sftp;

import java.text.StringCharacterIterator;

public class SftpUtil {
    private static final int KILOBYTE = 1024;
    private static final String SIZE_UNITS = "KMGTPE";
    private static final long MAX_COMPARABLE = 0xfffccccccccccccL;

    private SftpUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Converts a byte size into a human-readable format using binary units.
     *
     * @param bytes the size of the file in bytes
     * @return a human-readable string representation of the file size
     */
    public static String humanReadableByteCount(long bytes) {
        long absBytes = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absBytes < KILOBYTE) {
            return bytes + " B";
        }
        return formatSize(bytes, absBytes);
    }

    private static String formatSize(long originalBytes, long absBytes) {
        long value = absBytes;
        var charIterator = new StringCharacterIterator(SIZE_UNITS);
        for (int i = 40; i >= 0 && absBytes > MAX_COMPARABLE >> i; i -= 10) {
            value >>= 10;
            charIterator.next();
        }
        double scaledValue = value / (double) KILOBYTE;
        return formatFinalValue(originalBytes, scaledValue, charIterator.current());
    }

    private static String formatFinalValue(long originalBytes, double scaledValue, char sizeUnit) {
        return "%.1f %cB".formatted(scaledValue * Long.signum(originalBytes), sizeUnit)
                .replace(".0", ""); // Remove decimal point for whole numbers
    }
}
