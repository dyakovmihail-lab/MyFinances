package ru.iate.myfinances.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iate.myfinances.data.AppDatabase
import ru.iate.myfinances.data.Operation

class EditOperationViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).operationDao()

    suspend fun load(id: Long): Operation? = dao.getById(id)

    fun save(operation: Operation, onSaved: () -> Unit) {
        viewModelScope.launch {
            if (operation.id == 0L) {
                dao.insert(operation)
            } else {
                dao.update(operation)
            }
            onSaved()
        }
    }
}
