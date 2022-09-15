package org.poa1024.maskedlogs.pattern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class FieldsPatternSupplierTest {

    @Test
    fun testThatExactPatternsWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get()

        listOf("order_id", "order-id", "orderId", "OrderId", "ORDER_ID")
            .forEachFieldOccurrenceExample {
                assertThat(patterns.matches(it))
                    .describedAs(it)
                    .isOne()
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
                generateMapAndUrlParameterExamples(field)
            ).flatten()
        }.forEach(action)
    }

    private fun generateMapAndUrlParameterExamples(field: String) = listOf(
        "$field=1",
        "\r$field=1",
        "\n$field=1",
        "{$field=1",
        "($field=1",
        "?$field=1",
        "&$field=1",
        ",$field=1",
        " $field=1",
        ", $field=1",
        ", $field=1,",
        ", $field=1}",
        ", $field=1)",
        ", $field=1&",
        ", $field=1 ",
        ", $field=1\r",
        ", $field=1\n",
    )


    private fun List<Pattern>.matches(s: String): Int {
        return this.map { it.matcher(s).matches() }.filter { it }.size
    }

    private fun List<Pattern>.noneMatched(s: String): Boolean {
        return this.map { it.matcher(s).matches() }.none { it }
    }

}