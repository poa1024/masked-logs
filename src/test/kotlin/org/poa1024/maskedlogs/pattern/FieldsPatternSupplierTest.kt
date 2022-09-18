package org.poa1024.maskedlogs.pattern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class FieldsPatternSupplierTest {

    private val testValue = "abv1232312+345_12-34ABC_1"

    @Test
    fun testThatAllExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get().toList()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEach { field ->
                (equalSignParameterExamples(field) and urlPathParameterExamples(field) and jsonParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.matches(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isOne
                        assertThat(patterns.retrieveValue(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .containsExactly(testValue)
                    }
            }
    }

    @Test
    fun testThatEqualSignExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))
        patternSupplier.setJsonPatternsEnabled(false)
        patternSupplier.setEqualSignPatternsEnabled(true)
        patternSupplier.setUrlPathPatternsEnabled(false)

        val patterns = patternSupplier.get().toList()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEach { field ->
                (equalSignParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.matches(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isOne
                        assertThat(patterns.retrieveValue(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .containsExactly(testValue)
                    }
                (jsonParameterExamples(field) and urlPathParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.noneMatched(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isTrue
                    }
            }
    }

    @Test
    fun testThatUrlPathExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))
        patternSupplier.setJsonPatternsEnabled(false)
        patternSupplier.setEqualSignPatternsEnabled(false)
        patternSupplier.setUrlPathPatternsEnabled(true)

        val patterns = patternSupplier.get().toList()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEach { field ->
                (urlPathParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.matches(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isOne
                        assertThat(patterns.retrieveValue(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .containsExactly(testValue)
                    }
                (jsonParameterExamples(field) and equalSignParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.noneMatched(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isTrue
                    }
            }
    }

    @Test
    fun testThatJsonExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))
        patternSupplier.setJsonPatternsEnabled(true)
        patternSupplier.setEqualSignPatternsEnabled(false)
        patternSupplier.setUrlPathPatternsEnabled(false)

        val patterns = patternSupplier.get().toList()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEach { field ->
                (jsonParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.matches(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isOne
                        assertThat(patterns.retrieveValue(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .containsExactly(testValue)
                    }
                (equalSignParameterExamples(field) and urlPathParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.noneMatched(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isTrue
                    }
            }
    }

    @Test
    fun testThatPartialPatternsDoNotWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get().toList()

        listOf("ext_order_id", "ext-order-id", "extOrderId", "ExtOrderId", "EXT_ORDER_ID")
            .forEach { field ->
                (equalSignParameterExamples(field) and urlPathParameterExamples(field) and jsonParameterExamples(field))
                    .forEach { fieldOccurrenceExample ->
                        assertThat(patterns.noneMatched(fieldOccurrenceExample))
                            .describedAs(fieldOccurrenceExample)
                            .isTrue
                    }
            }
    }

    private fun urlPathParameterExamples(field: String) = listOf(
        "/$field/$testValue",
        "/$field/$testValue\r",
        "/$field/$testValue\n",
        "/$field/$testValue?",
    )

    private fun equalSignParameterExamples(field: String) = listOf(
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

    private fun jsonParameterExamples(field: String) = listOf(
        generateJsonExamples(field, keyQuote = true, valueQuote = true),
        generateJsonExamples(field, keyQuote = false, valueQuote = true),
        generateJsonExamples(field, keyQuote = true, valueQuote = false),
        generateJsonExamples(field, keyQuote = false, valueQuote = false)
    ).flatten()

    private fun generateJsonExamples(field: String, keyQuote: Boolean, valueQuote: Boolean): List<String> {
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

    infix fun <T> List<T>.and(other: List<T>): List<T> {
        return listOf(this, other).flatten()
    }

}