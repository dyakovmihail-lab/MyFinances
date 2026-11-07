package ru.iate.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import ru.iate.myfinances.ui.screens.MainScreen
import ru.iate.myfinances.ui.theme.MyFinancesTheme
import ru.iate.myfinances.util.openAddOperation
import ru.iate.myfinances.util.openOperationDetails
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
                    onOperationClick = { id -> openOperationDetails(this, id) }
                )
            }
        }
    }
}
