package net.geidea.paymentsdk.internal.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import net.geidea.paymentsdk.GeideaSdkInternal

@GeideaSdkInternal
internal class NetworkConnectivityDetector(context: Context) : NetworkConnectivity {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Solution 1: On API > Q only takes into account the available networks

    override val isConnected: Boolean
        get() {
            // For 29 api or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            // For below 29 api
            else {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                    return true
                }
            }
            return false
        }

    // Solution 2: HttpURLConnection ignores the connectTimeout resulting in slow responses sometimes

    /*override val isConnected: Boolean
        get() {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    val activeNetwork: Network? = connectivityManager.activeNetwork
                    if (activeNetwork != null) {
                        try {
                            val url = URL("https://google.net/")
                            with(url.openConnection() as HttpURLConnection) {
                                setRequestProperty("User-Agent", "Network connectivity check")
                                setRequestProperty("Connection", "close")
                                connectTimeout = 1_000
                                connect()
                                responseCode == 200
                            }
                        } catch (e: IOException) {
                            loge("Connection check failed: ${e.message}")
                            false
                        }
                    } else {
                        false
                    }
                }
            }
        }*/

    // Solution 3: Uses ICMP or TCP echo. ICMP might be blocked on some networks. It takes about 2x
    // more time than the actual timeout parameter

    /*override val isConnected: Boolean
        get() {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    try {
                        measureDuration {
                            InetAddress.getByName("google.com").isReachable(1500)
                        }
                    } catch (e: UnknownHostException) {
                        loge("Connection check failed: ${e.message}")
                        false
                    } catch (e: IOException) {
                        loge("Connection check failed: ${e.message}")
                        false
                    }
                }
            }
        }

    private fun <T> measureDuration(block: () -> T): T {
        val timeBeforeCall = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val timeAfterCall = System.currentTimeMillis()
            logi("Connection check took ${timeAfterCall - timeBeforeCall} ms")
        }
    }*/
}