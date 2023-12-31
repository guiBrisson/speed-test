package me.brisson.speedtest.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class NetworkManager(context: Context) : INetworkManager {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val networkCallback = registerNetworkCallback()

    override fun isConnected() = _isConnected.asStateFlow()

    override fun calculateSpeed(callback: (downSpeed: Int, upSpeed: Int) -> Unit) {
        val network = connectivityManager.activeNetwork ?: return
        val networkCapabilities: NetworkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return

        val downSpeed = (networkCapabilities.linkDownstreamBandwidthKbps) / 1000
        val upSpeed = (networkCapabilities.linkUpstreamBandwidthKbps) / 1000

        Log.d(TAG, "Download speed: $downSpeed, Upload speed: $upSpeed")
        callback(downSpeed, upSpeed)
    }

    private fun registerNetworkCallback(): NetworkCallback {
        val networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "NetworkCallback: Available")
                _isConnected.update { true }
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "NetworkCallback: Lost")
                _isConnected.update { false }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        return networkCallback
    }

    override fun calculateLatency(): Double {
        val process = Runtime.getRuntime().exec("ping -c 1 8.8.8.8")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        for (line in reader.readLines()) {
            if (line.contains("time=")) {
                val startIndex = line.indexOf("time=") + 5
                val endIndex = line.indexOf(" ms", startIndex)
                val latencyTimeString = line.substring(startIndex, endIndex)

                Log.d(TAG, "CalculateLatency: $latencyTimeString ms")
                return latencyTimeString.toDouble()
            }
        }
        return -1.0
    }

    override suspend fun getPublicIpAddress(): String? = suspendCoroutine { continuation ->
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.ipify.org?format=json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    continuation.resume(parseIpAddressFromJson(it))
                    return
                }
                continuation.resume(null)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "GetIpAddress: ${e.message}", e)
                continuation.resumeWithException(e)
            }
        })
    }

    private fun parseIpAddressFromJson(json: String): String {
        try {
            val jsonObject = JSONObject(json)
            return jsonObject.optString("ip", "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun clear() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    companion object {
        val TAG = NetworkManager::class.simpleName
    }
}
