package ru.iate.myfinances.util

import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.data.TransactionType
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

const val CURRENCY = "₽"

private const val MINUS = "\u2212"

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun formatAmount(value: Double): String {
    val format = NumberFormat.getNumberInstance(Locale.getDefault())
    format.minimumFractionDigits = 0
    format.maximumFractionDigits = 2
    return format.format(value)
}

fun formatMoney(value: Double): String = "${formatAmount(value)} $CURRENCY"

fun formatSignedMoney(operation: Operation): String {
    val sign = if (operation.type == TransactionType.INCOME) "+" else MINUS
    return "$sign${formatMoney(operation.amount)}"
}

fun formatDate(date: LocalDate): String = date.format(dateFormatter)

fun amountToInput(value: Double): String =
    BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()

fun parseAmount(text: String): Double? =
    text.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }
