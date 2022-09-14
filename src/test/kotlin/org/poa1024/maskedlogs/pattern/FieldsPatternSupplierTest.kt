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
            .forEach { field ->
                textExamplesFor(field).forEach {
                    assertThat(patterns.oneIsMatched(it)).isTrue
                }
            }
    }

    @Test
    fun testThatPartialPatternsDoNotWork() {
        val patternSupplier = FieldsPatternSupplier(listOf("order_id"))

        val patterns = patternSupplier.get()

        listOf("ext_order_id", "ext-order-id", "extOrderId", "ExtOrderId", "EXT_ORDER_ID")
            .forEach { field ->
                textExamplesFor(field).forEach {
                    assertThat(patterns.noneMatched(it)).isTrue
                }
            }
    }

    private fun textExamplesFor(field: String) = listOf(
        "$field=1",
        ",$field=1",
        " $field=1"
    )

    private fun List<Pattern>.oneIsMatched(s: String): Boolean {
        return this.map { it.matcher(s).matches() }.filter { it }.size == 1
    }

    private fun List<Pattern>.noneMatched(s: String): Boolean {
        return this.map { it.matcher(s).matches() }.none { it }
    }

}