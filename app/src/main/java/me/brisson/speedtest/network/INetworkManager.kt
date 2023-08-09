package me.brisson.speedtest.network

import kotlinx.coroutines.flow.StateFlow

interface INetworkManager {
    fun isConnected(): StateFlow<Boolean>
    fun calculateSpeed(callback: (downSpeed: Int, upSpeed: Int) -> Unit)
    fun calculateLatency(): Double
}