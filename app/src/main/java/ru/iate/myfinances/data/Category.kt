package ru.iate.myfinances.data

import androidx.annotation.StringRes
import ru.iate.myfinances.R

enum class Category(
    val type: TransactionType,
    @StringRes val titleRes: Int
) {
    SALARY(TransactionType.INCOME, R.string.category_salary),
    GIFT(TransactionType.INCOME, R.string.category_gift),
    INCOME_OTHER(TransactionType.INCOME, R.string.category_other),

    FOOD(TransactionType.EXPENSE, R.string.category_food),
    TRANSPORT(TransactionType.EXPENSE, R.string.category_transport),
    ENTERTAINMENT(TransactionType.EXPENSE, R.string.category_entertainment),
    SHOPPING(TransactionType.EXPENSE, R.string.category_shopping),
    EXPENSE_OTHER(TransactionType.EXPENSE, R.string.category_other);

    companion object {
        fun forType(type: TransactionType): List<Category> =
            entries.filter { it.type == type }
    }
}
