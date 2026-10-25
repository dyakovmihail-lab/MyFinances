package ru.iate.myfinances.util

import android.content.Context
import android.content.Intent
import ru.iate.myfinances.EditOperationActivity
import ru.iate.myfinances.OperationDetailsActivity
import ru.iate.myfinances.OperationsActivity

const val EXTRA_OPERATION_ID = "operation_id"

const val NEW_OPERATION_ID = -1L

fun openAddOperation(context: Context) {
    context.startActivity(Intent(context, EditOperationActivity::class.java))
}

fun openEditOperation(context: Context, id: Long) {
    context.startActivity(
        Intent(context, EditOperationActivity::class.java).putExtra(EXTRA_OPERATION_ID, id)
    )
}

fun openOperations(context: Context) {
    context.startActivity(Intent(context, OperationsActivity::class.java))
}

fun openOperationDetails(context: Context, id: Long) {
    context.startActivity(
        Intent(context, OperationDetailsActivity::class.java).putExtra(EXTRA_OPERATION_ID, id)
    )
}
