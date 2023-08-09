package me.brisson.speedtest.presentation.home

data class HomeState(
    val isConnected: Boolean = true,
    val ipAddress: String? = null,
    val ping: Double = -1.0,
    val uploadSpeed: Int = -1,
    val downloadSpeed: Int = -1,
)

sealed interface HomeEvent {
    object FetchNetworkDetails : HomeEvent
}
