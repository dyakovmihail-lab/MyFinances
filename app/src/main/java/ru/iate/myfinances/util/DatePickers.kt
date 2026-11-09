package ru.iate.myfinances.util

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate

fun showDatePicker(context: Context, initial: LocalDate, onPicked: (LocalDate) -> Unit) {
    DatePickerDialog(
        context,
        { _, year, month, day -> onPicked(LocalDate.of(year, month + 1, day)) },
        initial.year,
        initial.monthValue - 1,
        initial.dayOfMonth
    ).show()
}
