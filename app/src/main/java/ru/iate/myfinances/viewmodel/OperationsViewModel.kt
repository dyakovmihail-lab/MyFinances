package ru.iate.myfinances.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.iate.myfinances.data.AppDatabase
import ru.iate.myfinances.data.Category
import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.data.TransactionType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class Period {
    ALL,
    TODAY,
    WEEK,
    MONTH,
    CUSTOM
}

data class OperationFilter(
    val type: TransactionType? = null,
    val category: Category? = null,
    val period: Period = Period.ALL,
    val customFrom: LocalDate = LocalDate.now(),
    val customTo: LocalDate = LocalDate.now()
)

class OperationsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).operationDao()

    private val _filter = MutableStateFlow(OperationFilter())
    val filter: StateFlow<OperationFilter> = _filter

    val operations: StateFlow<List<Operation>> =
        combine(dao.observeAll(), _filter) { all, filter ->
            all.filter { matches(it, filter) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setType(type: TransactionType?) {
        _filter.update { current ->
            val category = current.category?.takeIf { type == null || it.type == type }
            current.copy(type = type, category = category)
        }
    }

    fun setCategory(category: Category?) {
        _filter.update { it.copy(category = category) }
    }

    fun setPeriod(period: Period) {
        _filter.update { it.copy(period = period) }
    }

    fun setCustomPeriod(from: LocalDate, to: LocalDate) {
        _filter.update { it.copy(period = Period.CUSTOM, customFrom = from, customTo = to) }
    }

    private fun matches(operation: Operation, filter: OperationFilter): Boolean {
        if (filter.type != null && operation.type != filter.type) return false
        if (filter.category != null && operation.category != filter.category) return false

        val today = LocalDate.now()
        return when (filter.period) {
            Period.ALL -> true
            Period.TODAY -> operation.date == today
            Period.WEEK -> {
                val start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                !operation.date.isBefore(start) && !operation.date.isAfter(start.plusDays(6))
            }
            Period.MONTH ->
                operation.date.year == today.year && operation.date.month == today.month
            Period.CUSTOM ->
                !operation.date.isBefore(filter.customFrom) &&
                    !operation.date.isAfter(filter.customTo)
        }
    }
}
