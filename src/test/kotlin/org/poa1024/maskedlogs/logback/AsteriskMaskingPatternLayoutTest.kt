package org.poa1024.maskedlogs.logback

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class AsteriskMaskingPatternLayoutTest {

    private val logger = LoggerFactory.getLogger("test")

    @Test
    fun testMaskingLogs() {
        val map = mapOf(
            "id" to "12",
            "person_id" to "321123",
            "external_order_id" to "ext_567765",
            "order_id" to "567765",
            "apiKey" to "someKey",
        )
        logger.info(map.toString())
        val lastLog = Files.lines(Path.of("build/test.log")).toList().last()

        assertThat(lastLog).isEqualTo("{id=12, person_id=3***23, external_order_id=ext_567765, order_id=5***65, apiKey=s****ey}")
    }
}