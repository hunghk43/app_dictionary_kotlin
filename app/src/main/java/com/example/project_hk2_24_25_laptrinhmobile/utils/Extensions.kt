package com.example.project_hk2_24_25_laptrinhmobile.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


fun String?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


@Composable
fun hideKeyboardOnDispose(softwareKeyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current) {
    DisposableEffect(Unit) {
        onDispose {
            softwareKeyboardController?.hide()
        }
    }
}

fun FocusManager.clearFocusAndHideKeyboard(softwareKeyboardController: SoftwareKeyboardController?) {
    this.clearFocus()
    softwareKeyboardController?.hide()
}

fun <T> Flow<T>.logFlow(tag: String = "FlowLog", messagePrefix: String = ""): Flow<T> = this
    .onStart { Log.d(tag, "$messagePrefix Started") }
    .onCompletion { cause ->
        if (cause == null) Log.d(tag, "$messagePrefix Completed")
        else Log.e(tag, "$messagePrefix Failed with $cause")
    }
    .catch { throwable -> Log.e(tag, "$messagePrefix Exception: $throwable", throwable) }


fun Long.toFormattedDateString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}


fun Modifier.debouncedClickable(
    debounceInterval: Long = 500L,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableStateOf(0L) }
    this.clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceInterval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}


fun CoroutineScope.launchWithDelay(delayMillis: Long, block: suspend CoroutineScope.() -> Unit) {
    this.launch {
        delay(delayMillis)
        block()
    }
}