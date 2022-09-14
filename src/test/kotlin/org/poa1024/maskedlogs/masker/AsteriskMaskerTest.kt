package org.poa1024.maskedlogs.masker

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class AsteriskMaskerTest {

    @Test
    fun testMaskPercentageRange() {
        assertThatThrownBy { AsteriskMasker(0.0) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("maskPercentage should in a range 0 - 100")
        assertThatThrownBy { AsteriskMasker(101.0) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("maskPercentage should in a range 0 - 100")
        AsteriskMasker(50.0)
    }

    @Test
    fun testMask60() {
        val masker = AsteriskMasker(60.0)
        assertThat(masker.mask("1")).isEqualTo("***")
        assertThat(masker.mask("12")).isEqualTo("***")
        assertThat(masker.mask("123")).isEqualTo("1*3")
        assertThat(masker.mask("1234")).isEqualTo("1**4")
        assertThat(masker.mask("12345")).isEqualTo("1***5")
        assertThat(masker.mask("123456")).isEqualTo("1***56")
        assertThat(masker.mask("1234567")).isEqualTo("1****67")
        assertThat(masker.mask("12345678")).isEqualTo("12****78")
        assertThat(masker.mask("123456789")).isEqualTo("12*****89")
        assertThat(masker.mask("1234567890")).isEqualTo("12******90")
        assertThat(masker.mask("12345678901")).isEqualTo("12******901")
        assertThat(masker.mask("123456789012")).isEqualTo("12*******012")
        assertThat(masker.mask("1234567890123")).isEqualTo("123*******123")
        assertThat(masker.mask("12345678901234")).isEqualTo("123********234")
        assertThat(masker.mask("123456789012345")).isEqualTo("123*********345")
        assertThat(masker.mask("1234567890123456")).isEqualTo("123*********3456")
        assertThat(masker.mask("12345678901234567")).isEqualTo("123**********4567")
        assertThat(masker.mask("123456789012345678")).isEqualTo("1234**********5678")
        assertThat(masker.mask("1234567890123456789")).isEqualTo("1234***********6789")
        assertThat(masker.mask("12345678901234567890")).isEqualTo("1234************7890")
    }

    @Test
    fun testMask40() {
        val masker = AsteriskMasker(40.0)
        assertThat(masker.mask("1")).isEqualTo("***")
        assertThat(masker.mask("12")).isEqualTo("***")
        assertThat(masker.mask("123")).isEqualTo("1*3")
        assertThat(masker.mask("1234")).isEqualTo("1*34")
        assertThat(masker.mask("12345")).isEqualTo("1**45")
        assertThat(masker.mask("123456")).isEqualTo("12**56")
        assertThat(masker.mask("1234567")).isEqualTo("12**567")
        assertThat(masker.mask("12345678")).isEqualTo("12***678")
        assertThat(masker.mask("123456789")).isEqualTo("123***789")
        assertThat(masker.mask("1234567890")).isEqualTo("123****890")
        assertThat(masker.mask("12345678901")).isEqualTo("123****8901")
        assertThat(masker.mask("123456789012")).isEqualTo("1234****9012")
        assertThat(masker.mask("1234567890123")).isEqualTo("1234*****0123")
        assertThat(masker.mask("12345678901234")).isEqualTo("1234*****01234")
        assertThat(masker.mask("123456789012345")).isEqualTo("1234******12345")
        assertThat(masker.mask("1234567890123456")).isEqualTo("12345******23456")
        assertThat(masker.mask("12345678901234567")).isEqualTo("12345******234567")
        assertThat(masker.mask("123456789012345678")).isEqualTo("12345*******345678")
        assertThat(masker.mask("1234567890123456789")).isEqualTo("123456*******456789")
        assertThat(masker.mask("12345678901234567890")).isEqualTo("123456********567890")
    }

}