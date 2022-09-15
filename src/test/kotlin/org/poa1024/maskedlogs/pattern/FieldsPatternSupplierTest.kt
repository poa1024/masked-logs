package org.poa1024.maskedlogs.pattern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class FieldsPatternSupplierTest {

    private val testValue = "abv1232312+345_12-34ABC_1"

    @Test
    fun testThatExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEachFieldOccurrenceExample { fieldOccurrenceExample ->
                assertThat(patterns.matches(fieldOccurrenceExample))
                    .describedAs(fieldOccurrenceExample)
                    .isOne
                assertThat(patterns.retrieveValue(fieldOccurrenceExample))
                    .describedAs(fieldOccurrenceExample)
                    .containsExactly(testValue)
            }
    }

    @Test
    fun testThatPartialPatternsDoNotWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get()

        listOf("ext_order_id", "ext-order-id", "extOrderId", "ExtOrderId", "EXT_ORDER_ID")
            .forEachFieldOccurrenceExample {
                assertThat(patterns.noneMatched(it))
                    .describedAs(it)
                    .isTrue
            }
    }

    private fun List<String>.forEachFieldOccurrenceExample(action: (String) -> Unit) {
        return this.flatMap { field ->
            listOf(
                generateMapAndUrlParameterExamples(field),
                generateJsonExamples(field),
                generateJsonExamples(field, keyQuote = false),
                generateJsonExamples(field, valueQuote = false),
                generateJsonExamples(field, keyQuote = false, valueQuote = false)
            ).flatten()
        }.forEach(action)
    }

    private fun generateMapAndUrlParameterExamples(field: String) = listOf(
        "$field=$testValue",
        "\r$field=$testValue",
        "\n$field=$testValue",
        "{$field=$testValue",
        "($field=$testValue",
        "?$field=$testValue",
        "&$field=$testValue",
        ",$field=$testValue",
        " $field=$testValue",
        ", $field=$testValue",
        ", $field=$testValue,",
        ", $field=$testValue}",
        ", $field=$testValue)",
        ", $field=$testValue&",
        ", $field=$testValue ",
        ", $field=$testValue\r",
        ", $field=$testValue\n",
    )

    private fun generateJsonExamples(field: String, keyQuote: Boolean = true, valueQuote: Boolean = true): List<String> {
        val kq = if (keyQuote) "\"" else ""
        val vq = if (valueQuote) "\"" else ""
        return listOf(
            """{$kq$field$kq:${vq}$testValue${vq}}""",
            """{$kq$field$kq : ${vq}$testValue${vq}}""",
            """{   $kq$field$kq   :    ${vq}$testValue${vq}   }""",
            """{$kq$field$kq:${vq}$testValue${vq},}""",
            """{$kq$field$kq:${vq}$testValue${vq}   ,}""",
            """{$kq$field$kq:${vq}$testValue${vq}${System.lineSeparator()}}""",
        )
    }


    private fun List<Pattern>.matches(s: String): Int {
        return this.map { it.matcher(s).find() }.filter { it }.size
    }

    private fun List<Pattern>.retrieveValue(s: String): List<String> {
        return this.mapNotNull {
            val matcher = it.matcher(s)
            if (matcher.find()) matcher.group("value") else null
        }.distinct()
    }

    private fun List<Pattern>.noneMatched(s: String): Boolean {
        return this.map { it.matcher(s).find() }.none { it }
    }

}