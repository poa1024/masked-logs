package org.poa1024.maskedlogs.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.poa1024.maskedlogs.util.Functions.lowerUnderscoreToAllCaseFormats

class FunctionsTest {

    @Test
    fun testLowerUnderscoreToAllCaseFormatsErrors() {
        assertThatThrownBy { lowerUnderscoreToAllCaseFormats("PERSON_ID_123") }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("PERSON_ID_123 is not in lower underscore case. But should be!")

        assertThatThrownBy { lowerUnderscoreToAllCaseFormats("PersonId123") }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("PersonId123 is not in lower underscore case. But should be!")
    }


    @Test
    fun testLowerUnderscoreToAllCaseFormats() {
        assertThat(lowerUnderscoreToAllCaseFormats("person_id_123"))
            .containsOnly(
                "person_id_123",
                "person-id-123",
                "personId123",
                "PersonId123",
                "PERSON_ID_123"
            )
    }

}