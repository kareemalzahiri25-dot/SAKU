package com.saku.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saku.app.data.DashboardSummary
import com.saku.app.data.DataRepository
import com.saku.app.data.DefaultDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(dataRepository: DataRepository = DefaultDataRepository.getInstance()) : ViewModel() {
    val uiState: StateFlow<MainScreenUiState> =
        dataRepository.dashboardData
            .map<DashboardSummary, MainScreenUiState>(MainScreenUiState::Success)
            .catch { emit(MainScreenUiState.Error(it)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MainScreenUiState.Loading
            )
}

sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState
    data class Error(val throwable: Throwable) : MainScreenUiState
    data class Success(val data: DashboardSummary) : MainScreenUiState
}
