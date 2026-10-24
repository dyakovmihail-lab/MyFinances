package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.data.Category
import ru.iate.myfinances.data.TransactionType
import ru.iate.myfinances.ui.components.AppHeader
import ru.iate.myfinances.ui.components.ChoiceDialog
import ru.iate.myfinances.ui.components.CustomPeriodDialog
import ru.iate.myfinances.ui.components.MainTab
import ru.iate.myfinances.ui.components.OperationItem
import ru.iate.myfinances.ui.components.ToggleButton
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.formatDate
import ru.iate.myfinances.util.openEditOperation
import ru.iate.myfinances.viewmodel.OperationFilter
import ru.iate.myfinances.viewmodel.OperationsViewModel
import ru.iate.myfinances.viewmodel.Period

class OperationsActivity : ComponentActivity() {

    private val viewModel: OperationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinancesTheme {
                OperationsScreen(
                    viewModel = viewModel,
                    onHomeClick = { finish() },
                    onOperationClick = { id -> openEditOperation(this, id) }
                )
            }
        }
    }
}

@Composable
private fun OperationsScreen(
    viewModel: OperationsViewModel,
    onHomeClick: () -> Unit,
    onOperationClick: (Long) -> Unit
) {
    val filter by viewModel.filter.collectAsState()
    val operations by viewModel.operations.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showPeriodDialog by remember { mutableStateOf(false) }
    var showCustomPeriodDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppHeader(
            selected = MainTab.OPERATIONS,
            onHomeClick = onHomeClick,
            onOperationsClick = {}
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.tab_operations),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToggleButton(
                        text = stringResource(R.string.filter_all),
                        selected = filter.type == null,
                        onClick = { viewModel.setType(null) }
                    )
                    ToggleButton(
                        text = stringResource(R.string.filter_incomes),
                        selected = filter.type == TransactionType.INCOME,
                        onClick = { viewModel.setType(TransactionType.INCOME) }
                    )
                    ToggleButton(
                        text = stringResource(R.string.filter_expenses),
                        selected = filter.type == TransactionType.EXPENSE,
                        onClick = { viewModel.setType(TransactionType.EXPENSE) }
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categoryText = filter.category?.let { categoryFilterLabel(it, filter.type == null) }
                        ?: stringResource(R.string.all_categories)
                    Text(text = "${stringResource(R.string.category)}: $categoryText")
                }
            }

            item {
                OutlinedButton(
                    onClick = { showPeriodDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "${stringResource(R.string.period)}: ${periodFieldText(filter)}")
                }
            }

            if (operations.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.nothing_found),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
                items(operations, key = { it.id }) { operation ->
                    OperationItem(
                        operation = operation,
                        onClick = { onOperationClick(operation.id) }
                    )
                }
            }
        }
    }

    if (showCategoryDialog) {
        val categories = filter.type?.let { Category.forType(it) } ?: Category.entries
        val options: List<Pair<Category?, String>> =
            listOf<Pair<Category?, String>>(null to stringResource(R.string.all_categories)) +
                categories.map { it to categoryFilterLabel(it, filter.type == null) }

        ChoiceDialog(
            title = stringResource(R.string.choose_category),
            options = options,
            selected = filter.category,
            onSelect = {
                viewModel.setCategory(it)
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }

    if (showPeriodDialog) {
        val options = Period.entries.map { it to periodLabel(it) }

        ChoiceDialog(
            title = stringResource(R.string.choose_period),
            options = options,
            selected = filter.period,
            onSelect = { period ->
                showPeriodDialog = false
                if (period == Period.CUSTOM) {
                    showCustomPeriodDialog = true
                } else {
                    viewModel.setPeriod(period)
                }
            },
            onDismiss = { showPeriodDialog = false }
        )
    }

    if (showCustomPeriodDialog) {
        CustomPeriodDialog(
            initialFrom = filter.customFrom,
            initialTo = filter.customTo,
            onConfirm = { from, to ->
                viewModel.setCustomPeriod(from, to)
                showCustomPeriodDialog = false
            },
            onDismiss = { showCustomPeriodDialog = false }
        )
    }
}

@Composable
private fun categoryFilterLabel(category: Category, showType: Boolean): String {
    val name = stringResource(category.titleRes)
    val isOther = category == Category.INCOME_OTHER || category == Category.EXPENSE_OTHER
    if (!showType || !isOther) return name

    val typeName = stringResource(
        if (category.type == TransactionType.INCOME) R.string.type_income_lower
        else R.string.type_expense_lower
    )
    return stringResource(R.string.category_with_type, name, typeName)
}

@Composable
private fun periodLabel(period: Period): String = stringResource(
    when (period) {
        Period.ALL -> R.string.period_all
        Period.TODAY -> R.string.period_today
        Period.WEEK -> R.string.period_week
        Period.MONTH -> R.string.period_month
        Period.CUSTOM -> R.string.custom_period
    }
)

@Composable
private fun periodFieldText(filter: OperationFilter): String =
    if (filter.period == Period.CUSTOM) {
        stringResource(
            R.string.period_range,
            formatDate(filter.customFrom),
            formatDate(filter.customTo)
        )
    } else {
        periodLabel(filter.period)
    }
