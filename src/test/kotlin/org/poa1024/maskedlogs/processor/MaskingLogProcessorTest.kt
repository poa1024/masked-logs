package org.poa1024.maskedlogs.processor

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class MaskingLogProcessorTest {

    private val maskingLogProcessor = MaskingLogProcessor({
        listOf(
            Pattern.compile("order/(?<value>.*?)\\?"),
            Pattern.compile("apikey=(?<value>.*?)(&|$)"),
            Pattern.compile("APIKEY=(?<value>.*?)&"),
        )
    }) { "<MASKED>" }

    @Test
    fun testMaskUrl() {
        val log = "https://poa1024.com/order/12?apikey=someApiKey&num=8787&APIKEY=someApiKey&apikey=someApiKey"
        val expectedMaskedLOg = "https://poa1024.com/order/<MASKED>?apikey=<MASKED>&num=8787&APIKEY=<MASKED>&apikey=<MASKED>"
        val maskedLog = maskingLogProcessor.process(log)
        assertThat(maskedLog).isEqualTo(expectedMaskedLOg)
    }
}