package com.example.project_hk2_24_25_laptrinhmobile.utils



import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

/**
 * Enum để biểu diễn các trạng thái kết nối mạng.
 */
enum class NetworkStatus {
    Available, Unavailable, Losing, Lost
}

/**
 * Lớp tiện ích để theo dõi trạng thái kết nối mạng sử dụng Flow.
 */
class NetworkConnectivityObserver(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<NetworkStatus> {
        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkStatus.Available)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    trySend(NetworkStatus.Losing)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(NetworkStatus.Lost)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(NetworkStatus.Unavailable)
                }
            }


            if (context.isNetworkAvailable()) {
                trySend(NetworkStatus.Available)
            } else {
                trySend(NetworkStatus.Unavailable)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val networkRequest = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            }

            awaitClose {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }.distinctUntilChanged()
    }
}



object InternetAvailability {
    // Kiểm tra xem có thể thực sự kết nối tới internet không (không chỉ là network available)
    // Bằng cách thử kết nối tới một server DNS của Google.
    // Chạy trên một CoroutineScope riêng để không block UI.
    fun check(scope: CoroutineScope, resultCallback: (Boolean) -> Unit): Job {
        return scope.launch(Dispatchers.IO) {
            try {
                val socket = SocketFactory.getDefault().createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500) // Timeout 1.5 giây
                socket.close()
                resultCallback(true)
            } catch (e: IOException) {
                resultCallback(false)
            }
        }
    }

    // LiveData version
    fun getLiveStatus(context: Context): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                liveData.postValue(true)
            }
            override fun onLost(network: Network) {
                liveData.postValue(false)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
        // Initial check
        liveData.postValue(context.isNetworkAvailable())
        return liveData
    }
}


@Suppress("unused")
fun NetworkConnectivityObserver.observeAsStateFlow(
    scope: CoroutineScope,
    initialValue: NetworkStatus = NetworkStatus.Unavailable
): StateFlow<NetworkStatus> {
    return this.observe().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialValue
    )
}