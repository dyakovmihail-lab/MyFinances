package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.data.Category
import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.data.TransactionType
import ru.iate.myfinances.ui.components.ChoiceDialog
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.CURRENCY
import ru.iate.myfinances.util.EXTRA_OPERATION_ID
import ru.iate.myfinances.util.NEW_OPERATION_ID
import ru.iate.myfinances.util.amountToInput
import ru.iate.myfinances.util.formatDate
import ru.iate.myfinances.util.parseAmount
import ru.iate.myfinances.util.showDatePicker
import ru.iate.myfinances.viewmodel.EditOperationViewModel
import java.time.LocalDate

private val AmountRegex = Regex("^\\d{0,9}([.,]\\d{0,2})?$")

class EditOperationActivity : ComponentActivity() {

    private val viewModel: EditOperationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val operationId = intent.getLongExtra(EXTRA_OPERATION_ID, NEW_OPERATION_ID)

        setContent {
            MyFinancesTheme {
                EditOperationScreen(
                    viewModel = viewModel,
                    operationId = operationId,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun EditOperationScreen(
    viewModel: EditOperationViewModel,
    operationId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isEdit = operationId != NEW_OPERATION_ID

    var typeName by rememberSaveable { mutableStateOf(TransactionType.EXPENSE.name) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var categoryName by rememberSaveable { mutableStateOf(Category.FOOD.name) }
    var epochDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    var comment by rememberSaveable { mutableStateOf("") }
    var loaded by rememberSaveable { mutableStateOf(!isEdit) }
    var showAmountError by rememberSaveable { mutableStateOf(false) }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val type = TransactionType.valueOf(typeName)
    val category = Category.valueOf(categoryName)
    val date = LocalDate.ofEpochDay(epochDay)

    LaunchedEffect(operationId) {
        if (!loaded) {
            val operation = viewModel.load(operationId)
            if (operation == null) {
                onBack()
            } else {
                typeName = operation.type.name
                amountText = amountToInput(operation.amount)
                categoryName = operation.category.name
                epochDay = operation.date.toEpochDay()
                comment = operation.comment
                loaded = true
            }
        }
    }

    fun changeType(newType: TransactionType) {
        typeName = newType.name
        if (category.type != newType) {
            categoryName = Category.forType(newType).first().name
        }
    }

    fun save() {
        val amount = parseAmount(amountText)
        if (amount == null) {
            showAmountError = true
            return
        }
        if (isSaving) return
        isSaving = true

        val operation = Operation(
            id = if (isEdit) operationId else 0,
            type = type,
            amount = amount,
            category = category,
            date = date,
            comment = comment.trim()
        )
        viewModel.save(operation) { onBack() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
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
                text = stringResource(if (isEdit) R.string.edit_title else R.string.add_title),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isEdit) {
                Text(
                    text = stringResource(
                        if (type == TransactionType.INCOME) R.string.income_type
                        else R.string.expense_type
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TypeButton(
                        text = stringResource(R.string.expense_type),
                        selected = type == TransactionType.EXPENSE,
                        onClick = { changeType(TransactionType.EXPENSE) },
                        modifier = Modifier.weight(1f)
                    )
                    TypeButton(
                        text = stringResource(R.string.income_type),
                        selected = type == TransactionType.INCOME,
                        onClick = { changeType(TransactionType.INCOME) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (AmountRegex.matches(input)) {
                        amountText = input
                        showAmountError = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.amount)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = { Text(text = CURRENCY) },
                isError = showAmountError,
                supportingText = if (showAmountError) {
                    { Text(text = stringResource(R.string.error_amount)) }
                } else {
                    null
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${stringResource(R.string.category)}: ${stringResource(category.titleRes)}")
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDatePicker(context, date) { epochDay = it.toEpochDay() } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${stringResource(R.string.date)}: ${formatDate(date)}")
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.comment)) },
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { save() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(if (isEdit) R.string.save_changes else R.string.save))
            }
        }
    }

    if (showCategoryDialog) {
        ChoiceDialog(
            title = stringResource(
                if (type == TransactionType.INCOME) R.string.choose_income_category
                else R.string.choose_expense_category
            ),
            options = Category.forType(type).map { it to stringResource(it.titleRes) },
            selected = category,
            onSelect = {
                categoryName = it.name
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

@Composable
private fun TypeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) { Text(text = text) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(text = text) }
    }
}
