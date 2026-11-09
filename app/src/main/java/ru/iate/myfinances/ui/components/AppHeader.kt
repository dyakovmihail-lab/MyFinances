package ru.iate.myfinances.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.iate.myfinances.R

enum class MainTab {
    HOME,
    OPERATIONS
}

@Composable
fun AppHeader(
    selected: MainTab,
    onHomeClick: () -> Unit,
    onOperationsClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge
        )
        Row {
            TextButton(onClick = onHomeClick) {
                Text(
                    text = stringResource(R.string.tab_home),
                    fontWeight = if (selected == MainTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            }
            TextButton(onClick = onOperationsClick) {
                Text(
                    text = stringResource(R.string.tab_operations),
                    fontWeight = if (selected == MainTab.OPERATIONS) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
