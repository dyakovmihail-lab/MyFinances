package ru.iate.myfinances.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.iate.myfinances.data.AppDatabase
import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.data.TransactionType
import java.time.LocalDate

data class MainState(
    val balance: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val recent: List<Operation> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).operationDao()

    val state: StateFlow<MainState> = dao.observeAll()
        .map { operations -> buildState(operations) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainState())

    private fun buildState(operations: List<Operation>): MainState {
        val today = LocalDate.now()
        val income = operations.filter { it.type == TransactionType.INCOME }
        val expense = operations.filter { it.type == TransactionType.EXPENSE }

        val monthIncome = income.filter { it.isInMonthOf(today) }.sumOf { it.amount }
        val monthExpense = expense.filter { it.isInMonthOf(today) }.sumOf { it.amount }

        return MainState(
            balance = income.sumOf { it.amount } - expense.sumOf { it.amount },
            monthIncome = monthIncome,
            monthExpense = monthExpense,
            recent = operations.take(RECENT_COUNT)
        )
    }

    private fun Operation.isInMonthOf(day: LocalDate): Boolean =
        date.year == day.year && date.month == day.month

    private companion object {
        const val RECENT_COUNT = 3
    }
}
