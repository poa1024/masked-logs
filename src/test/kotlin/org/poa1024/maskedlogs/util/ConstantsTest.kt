package org.poa1024.maskedlogs.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class ConstantsTest {

    @Test
    fun testValuePattern() {
        val pattern = Pattern.compile("""^${Constants.VALUE_PATTERN}*$""")
        assertThat(pattern.matches("abv1232312+345_12-34ABC_1")).isTrue
        assertThat(pattern.matches("abv1232312+345_12-[34ABC_1")).isFalse
        assertThat(pattern.matches("abv1232312+345_12-]34ABC_1")).isFalse
        assertThat(pattern.matches("abv1232312+345_12-*34ABC_1")).isFalse
        assertThat(pattern.matches("abv1232312+345_12-&34ABC_1")).isFalse
    }

    private fun Pattern.matches(s: String) = this.matcher(s).find()

}