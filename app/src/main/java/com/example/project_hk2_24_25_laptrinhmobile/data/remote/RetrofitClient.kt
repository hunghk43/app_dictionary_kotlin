// File: app/src/main/java/com/example/project_hk2_24_25_laptrinhmobile/data/remote/RetrofitClient.kt
package com.example.project_hk2_24_25_laptrinhmobile.data.remote

// Import BuildConfig từ package ứng dụng của bạn
import com.example.project_hk2_24_25_laptrinhmobile.BuildConfig

// Imports cho Gson và Retrofit/OkHttp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException // Cho hàm getErrorMessage
import retrofit2.Response     // Cho hàm safeApiCall (nếu bạn định nghĩa nó ở đây)
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object để quản lý và cung cấp các instance của Retrofit services.
 */
object RetrofitClient {

    /**
     * HttpLoggingInterceptor để log các request và response mạng.
     * Level được đặt dựa trên BuildConfig.DEBUG (BODY cho debug, NONE cho release).
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * OkHttpClient được cấu hình với logging interceptor và timeouts.
     * Instance này có thể được dùng chung cho nhiều Retrofit instance.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // Bạn có thể thêm các interceptors khác ở đây nếu cần
            // ví dụ: .addInterceptor(AuthInterceptor()) cho việc thêm token tự động
            .build()
    }

    /**
     * Gson instance được cấu hình (có thể tùy chỉnh thêm nếu cần).
     * Instance này có thể được dùng chung.
     */
    private val gson: Gson by lazy {
        GsonBuilder()
            // .setLenient() // Bỏ comment nếu API trả về JSON không quá chặt chẽ
            // .serializeNulls() // Bỏ comment nếu muốn serialize các giá trị null
            .create()
    }

    /**
     * Lazy-initialized instance của EnglishDictionaryApiService.
     * Sử dụng BASE_URL từ EnglishApiConstants.
     */
    val englishDictionaryService: EnglishDictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EnglishApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(EnglishDictionaryApiService::class.java)
    }

    /**
     * Lazy-initialized instance của TranslationApiService.
     * Sử dụng BASE_URL từ MyMemoryApiConstants.
     */
    val translationService: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MyMemoryApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TranslationApiService::class.java)
    }

    /**
     * Hàm tiện ích để chuyển đổi Throwable thành một thông điệp lỗi thân thiện với người dùng.
     * @param throwable Lỗi cần được xử lý.
     * @return Chuỗi thông điệp lỗi.
     */
    fun getErrorMessage(throwable: Throwable): String {
        // Log lỗi gốc để debug
        // Log.e("RetrofitClientError", "Error: ${throwable.javaClass.simpleName} - ${throwable.message}", throwable)

        return when (throwable) {
            is java.net.UnknownHostException -> "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng của bạn."
            is java.net.SocketTimeoutException -> "Yêu cầu quá thời gian chờ. Vui lòng thử lại."
            is java.net.ConnectException -> "Kết nối thất bại. Vui lòng kiểm tra mạng và thử lại."
            is HttpException -> { // Lỗi HTTP từ Retrofit (ví dụ: 404, 500)
                // Bạn có thể thử đọc errorBody để lấy thông điệp lỗi cụ thể từ server nếu có
                // val errorBody = throwable.response()?.errorBody()?.string()
                // if (!errorBody.isNullOrBlank()) { return errorBody } // Cẩn thận, errorBody có thể là HTML hoặc JSON
                when (throwable.code()) {
                    400 -> "Yêu cầu không hợp lệ."
                    401 -> "Yêu cầu cần xác thực." // (Không áp dụng cho API hiện tại)
                    403 -> "Bạn không có quyền truy cập tài nguyên này."
                    404 -> "Không tìm thấy nội dung yêu cầu." // Có thể là "Từ không tồn tại"
                    429 -> "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau ít phút." // Rate limiting
                    in 500..599 -> "Lỗi từ phía máy chủ. Vui lòng thử lại sau."
                    else -> "Lỗi HTTP: ${throwable.code()} (${throwable.message()})"
                }
            }
            is java.io.IOException -> "Đã xảy ra lỗi mạng hoặc đọc/ghi dữ liệu. (${throwable.message})" // Các lỗi IO khác
            else -> throwable.message ?: "Đã xảy ra lỗi không xác định."
        }
    }
}

// Các sealed class và extension function cho ApiResult và safeApiCall có thể đặt ở đây
// hoặc trong một file tiện ích riêng (ví dụ: utils/ApiUtils.kt) để tránh làm file này quá dài.
// Nếu bạn đã đặt chúng ở đây, chúng có thể trông như sau:

/**
 * Sealed class để biểu diễn các trạng thái kết quả của một lời gọi API.
 * @param T Kiểu dữ liệu của kết quả thành công.
 */
sealed class ApiResult<out T> {
    /**
     * Trạng thái thành công, chứa dữ liệu [data].
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * Trạng thái lỗi, chứa thông điệp lỗi [message].
     */
    data class Error(val message: String) : ApiResult<Nothing>()

    /**
     * Trạng thái đang tải dữ liệu.
     */
    object Loading : ApiResult<Nothing>()
}

/**
 * Extension function để thực hiện một lời gọi API Retrofit một cách an toàn và
 * chuyển đổi kết quả thành [ApiResult].
 *
 * @param T Kiểu dữ liệu của body response khi thành công.
 * @param call Lambda suspend thực hiện lời gọi Retrofit và trả về `retrofit2.Response<T>`.
 * @return [ApiResult<T>] tương ứng với kết quả của lời gọi API.
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                // Trường hợp isSuccessful = true nhưng body = null
                ApiResult.Error("Nội dung phản hồi rỗng dù thành công (Mã: ${response.code()})")
            }
        } else {
            // Lỗi HTTP (4xx, 5xx)
            ApiResult.Error(RetrofitClient.getErrorMessage(HttpException(response)))
        }
    } catch (e: Exception) {
        // Các lỗi khác (mạng, parse JSON, etc.)
        ApiResult.Error(RetrofitClient.getErrorMessage(e))
    }
}

/**
 * Extension function để chuyển đổi dữ liệu bên trong [ApiResult.Success]
 * từ kiểu [T] sang kiểu [R] sử dụng hàm [transform] được cung cấp.
 * Trạng thái [ApiResult.Error] và [ApiResult.Loading] được giữ nguyên.
 */
inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> ApiResult.Error(message) // Giữ nguyên lỗi
        is ApiResult.Loading -> ApiResult.Loading   // Giữ nguyên trạng thái loading
    }
}