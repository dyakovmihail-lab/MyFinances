package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.ui.components.AppHeader
import ru.iate.myfinances.ui.components.MainTab
import ru.iate.myfinances.ui.components.OperationItem
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.formatMoney
import ru.iate.myfinances.util.openAddOperation
import ru.iate.myfinances.util.openEditOperation
import ru.iate.myfinances.util.openOperations
import ru.iate.myfinances.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinancesTheme {
                MainScreen(
                    viewModel = viewModel,
                    onOperationsClick = { openOperations(this) },
                    onAddClick = { openAddOperation(this) },
                    onOperationClick = { id -> openEditOperation(this, id) }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: MainViewModel,
    onOperationsClick: () -> Unit,
    onAddClick: () -> Unit,
    onOperationClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                selected = MainTab.HOME,
                onHomeClick = {},
                onOperationsClick = onOperationsClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp)
            ) {
                Text(
                    text = stringResource(R.string.balance),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatMoney(state.balance),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(text = stringResource(R.string.for_all_time))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stringResource(R.string.income)}: ${formatMoney(state.monthIncome)} " +
                        stringResource(R.string.for_month)
                )
                Text(
                    text = "${stringResource(R.string.expense)}: ${formatMoney(state.monthExpense)} " +
                        stringResource(R.string.for_month)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.recent_operations),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (state.recent.isEmpty()) {
                    Text(text = stringResource(R.string.no_operations))
                } else {
                    state.recent.forEach { operation ->
                        OperationItem(
                            operation = operation,
                            onClick = { onOperationClick(operation.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_operation)
            )
        }
    }
}
