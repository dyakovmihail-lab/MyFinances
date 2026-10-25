package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.data.TransactionType
import ru.iate.myfinances.ui.components.DeleteConfirmDialog
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.EXTRA_OPERATION_ID
import ru.iate.myfinances.util.NEW_OPERATION_ID
import ru.iate.myfinances.util.formatDate
import ru.iate.myfinances.util.formatSignedMoney
import ru.iate.myfinances.util.openEditOperation
import ru.iate.myfinances.viewmodel.OperationDetailsViewModel

class OperationDetailsActivity : ComponentActivity() {

    private val viewModel: OperationDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val operationId = intent.getLongExtra(EXTRA_OPERATION_ID, NEW_OPERATION_ID)

        setContent {
            MyFinancesTheme {
                OperationDetailsScreen(
                    viewModel = viewModel,
                    operationId = operationId,
                    onBack = { finish() },
                    onEdit = { openEditOperation(this, operationId) }
                )
            }
        }
    }
}

@Composable
private fun OperationDetailsScreen(
    viewModel: OperationDetailsViewModel,
    operationId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val operationFlow = remember { viewModel.observe(operationId) }
    val operation by operationFlow.collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.details_title),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        operation?.let { current ->
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(
                        if (current.type == TransactionType.INCOME) R.string.income_type
                        else R.string.expense_type
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                DetailsTable(operation = current)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.edit))
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }

            if (showDeleteDialog) {
                DeleteConfirmDialog(
                    onConfirm = {
                        showDeleteDialog = false
                        viewModel.delete(current) { onBack() }
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}

@Composable
private fun DetailsTable(operation: Operation) {
    Column {
        DetailsRow(
            label = stringResource(R.string.category),
            value = stringResource(operation.category.titleRes)
        )
        HorizontalDivider()
        DetailsRow(
            label = stringResource(R.string.amount),
            value = formatSignedMoney(operation)
        )
        HorizontalDivider()
        DetailsRow(
            label = stringResource(R.string.date),
            value = formatDate(operation.date)
        )
        HorizontalDivider()
        DetailsRow(
            label = stringResource(R.string.comment),
            value = operation.comment.ifBlank { "—" }
        )
    }
}

@Composable
private fun DetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
