package ru.iate.myfinances.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.R
import ru.iate.myfinances.data.Operation
import ru.iate.myfinances.util.formatDate
import ru.iate.myfinances.util.formatSignedMoney
import java.time.LocalDate

@Composable
fun dateLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> stringResource(R.string.today)
        today.minusDays(1) -> stringResource(R.string.yesterday)
        else -> formatDate(date)
    }
}

@Composable
fun OperationItem(
    operation: Operation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = stringResource(operation.category.titleRes)
    val title = operation.comment.ifBlank { categoryName }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatSignedMoney(operation),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = dateLabel(operation.date),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        HorizontalDivider()
    }
}
