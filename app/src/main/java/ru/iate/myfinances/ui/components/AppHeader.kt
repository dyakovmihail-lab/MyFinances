package ru.iate.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            HeaderTab(
                text = stringResource(R.string.tab_home),
                selected = selected == MainTab.HOME,
                onClick = onHomeClick,
                modifier = Modifier.weight(1f)
            )
            HeaderTab(
                text = stringResource(R.string.tab_operations),
                selected = selected == MainTab.OPERATIONS,
                onClick = onOperationsClick,
                modifier = Modifier.weight(1f)
            )
        }
        AppDivider()
    }
}

@Composable
private fun HeaderTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            )
        }
    }
}

@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}
