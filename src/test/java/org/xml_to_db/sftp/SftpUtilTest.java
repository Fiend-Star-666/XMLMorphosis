package org.xml_to_db.sftp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SftpUtilTest {

    @Test
    void testHumanReadableByteCount() {
        assertEquals("0 B", SftpUtil.humanReadableByteCount(0));
        assertEquals("1 B", SftpUtil.humanReadableByteCount(1));
        assertEquals("1 KB", SftpUtil.humanReadableByteCount(1024));
        assertEquals("1 MB", SftpUtil.humanReadableByteCount(1024 * 1024));
        assertEquals("1 GB", SftpUtil.humanReadableByteCount(1024 * 1024 * 1024));
        assertEquals("1 TB", SftpUtil.humanReadableByteCount(1024L * 1024 * 1024 * 1024));
    }

    @Test
    void testHumanReadableByteCountNegative() {
        assertEquals("-1 B", SftpUtil.humanReadableByteCount(-1));
        assertEquals("-1 KB", SftpUtil.humanReadableByteCount(-1024));
    }

    @Test
    void testHumanReadableByteCountEdgeCases() {
        assertEquals("8 EB", SftpUtil.humanReadableByteCount(Long.MAX_VALUE));
        assertEquals("-8 EB", SftpUtil.humanReadableByteCount(Long.MIN_VALUE));
    }
}
