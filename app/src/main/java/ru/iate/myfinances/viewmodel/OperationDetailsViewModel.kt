package ru.iate.myfinances.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.iate.myfinances.data.AppDatabase
import ru.iate.myfinances.data.Operation

class OperationDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).operationDao()

    fun observe(id: Long): Flow<Operation?> = dao.observeById(id)

    fun delete(operation: Operation, onDeleted: () -> Unit) {
        viewModelScope.launch {
            dao.delete(operation)
            onDeleted()
        }
    }
}
