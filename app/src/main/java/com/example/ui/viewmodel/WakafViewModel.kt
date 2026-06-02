package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import com.example.data.repository.WakafRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WakafViewModel(application: Application, private val repository: WakafRepository) : AndroidViewModel(application) {

    val wakafLands: StateFlow<List<WakafLand>> = repository.allWakafLands
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allNazhirs: StateFlow<List<Nazhir>> = repository.allNazhirs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedKecamatanFilter = MutableStateFlow("Kendal")
    val selectedKecamatanFilter = _selectedKecamatanFilter.asStateFlow()

    val filteredNazhirs: StateFlow<List<Nazhir>> = _selectedKecamatanFilter
        .flatMapLatest { kecamatan ->
            repository.getNazhirsByKecamatan(kecamatan)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Initialize default Nazhirs if the database is newly created
        viewModelScope.launch {
            repository.initializeDefaultNazhirsIfNeeded()
        }
    }

    fun setKecamatanFilter(kecamatan: String) {
        _selectedKecamatanFilter.value = kecamatan
    }

    fun saveWakafLand(wakafLand: WakafLand, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val insertedId = repository.insertWakafLand(wakafLand)
            onComplete(insertedId)
        }
    }

    fun deleteWakafLand(id: Long) {
        viewModelScope.launch {
            repository.deleteWakafLandById(id)
        }
    }

    fun registerNewNazhir(nazhir: Nazhir) {
        viewModelScope.launch {
            repository.insertNazhir(nazhir)
        }
    }

    fun deleteNazhir(id: Long) {
        viewModelScope.launch {
            repository.deleteNazhirById(id)
        }
    }

    // Load custom specific lists asynchronously
    fun fetchNazhirsByIds(ids: List<Long>, onResult: (List<Nazhir>) -> Unit) {
        viewModelScope.launch {
            val result = repository.getNazhirsByIds(ids)
            onResult(result)
        }
    }
}

class WakafViewModelFactory(
    private val application: Application,
    private val repository: WakafRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WakafViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WakafViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
