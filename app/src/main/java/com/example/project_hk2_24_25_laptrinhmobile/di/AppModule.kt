
package com.example.project_hk2_24_25_laptrinhmobile.di

import android.content.Context // Cần cho NetworkConnectivityObserver
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.EnglishDictionaryApiService // Import service mới
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.RetrofitClient
import com.example.project_hk2_24_25_laptrinhmobile.data.remote.TranslationApiService // Import service mới
import com.example.project_hk2_24_25_laptrinhmobile.data.repository.DictionaryRepository
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


}