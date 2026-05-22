package com.phishguard.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phishguard.app.data.ThreatEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {
    private val dao = PhishGuardApp.database.threatDao()

    val threats: StateFlow<List<ThreatEntry>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}