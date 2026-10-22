package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.formatDate
import ru.iate.myfinances.util.formatMoney
import ru.iate.myfinances.util.formatSignedMoney
import ru.iate.myfinances.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinancesTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

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
                Text(
                    text = "${stringResource(operation.category.titleRes)}  " +
                        "${formatSignedMoney(operation)}  ${formatDate(operation.date)}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
