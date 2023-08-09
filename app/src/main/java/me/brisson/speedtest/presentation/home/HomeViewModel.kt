package me.brisson.speedtest.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import me.brisson.speedtest.network.INetworkManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkManager: INetworkManager
) : ViewModel()  {
    private val isConnected = networkManager.isConnected()

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = combine(isConnected, _uiState) { connected, state ->
        val ipAddress = networkManager.getPublicIpAddress()
        state.copy(isConnected = connected, ipAddress = ipAddress)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _uiState.value)

    fun handleEvents(event: HomeEvent) {
        when(event){
            HomeEvent.FetchNetworkDetails -> updateNetworkState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkManager.clear()
    }

    private fun updateNetworkState() {
        val ping = networkManager.calculateLatency()
        networkManager.calculateSpeed { downSpeed, upSpeed ->
            _uiState.update {
                it.copy(
                    ping = ping,
                    uploadSpeed = upSpeed,
                    downloadSpeed = downSpeed
                )
            }
        }
    }
}
