// di/AppModule.kt
package com.example.project_hk2_24_25_laptrinhmobile.di // Đảm bảo package name chính xác

import android.content.Context // Cần cho NetworkConnectivityObserver
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.EnglishDictionaryApiService // Import service mới
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.RetrofitClient
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.TranslationApiService // Import service mới
import com.example.project_hk2_24_25_laptrinhmobile.data.repository.DictionaryRepository // Import Repository nếu bạn muốn provide nó ở đây (nhưng không cần nếu có @Inject constructor)
import com.example.project_hk2_24_25_laptrinhmobile.utils.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Các dependency sẽ có scope là Singleton (sống suốt đời ứng dụng)
object AppModule {

    /**
     * Cung cấp instance của EnglishDictionaryApiService.
     * Hilt sẽ gọi hàm này khi cần một instance của EnglishDictionaryApiService.
     * Instance này sẽ là singleton.
     */
    @Provides
    @Singleton
    fun provideEnglishDictionaryApiService(): EnglishDictionaryApiService {
        return RetrofitClient.englishDictionaryService // Lấy từ RetrofitClient đã cập nhật
    }

    /**
     * Cung cấp instance của TranslationApiService.
     * Hilt sẽ gọi hàm này khi cần một instance của TranslationApiService.
     * Instance này sẽ là singleton.
     */
    @Provides
    @Singleton
    fun provideTranslationApiService(): TranslationApiService {
        return RetrofitClient.translationService // Lấy từ RetrofitClient đã cập nhật
    }

    /**
     * Cung cấp instance của NetworkConnectivityObserver.
     * @param context ApplicationContext được Hilt tự động inject.
     * Instance này sẽ là singleton.
     */
    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

    /**
     * Cung cấp instance của DictionaryRepository.
     * QUAN TRỌNG: Nếu DictionaryRepository của bạn đã có annotation @Inject constructor(...)
     * và @Singleton, thì bạn KHÔNG CẦN hàm @Provides này nữa. Hilt sẽ tự động biết
     * cách tạo DictionaryRepository và inject các dependency (EnglishDictionaryApiService,
     * TranslationApiService) vào đó (miễn là các dependency đó cũng đã được Hilt biết cách provide).
     *
     * Giữ lại hàm này nếu DictionaryRepository không có @Inject constructor
     * hoặc bạn muốn tùy chỉnh cách nó được tạo ra.
     *
     * Trong trường hợp chúng ta đang làm (DictionaryRepository sẽ inject 2 services),
     * và DictionaryRepository có @Inject constructor, thì hàm này là THỪA.
     */
    /* // BỎ COMMENT NẾU DictionaryRepository KHÔNG CÓ @Inject constructor
    @Provides
    @Singleton
    fun provideDictionaryRepository(
        englishApi: EnglishDictionaryApiService,
        translationApi: TranslationApiService
        // Thêm các dependency khác của Repository nếu có
    ): DictionaryRepository {
        return DictionaryRepository(englishApi, translationApi)
    }
    */
}