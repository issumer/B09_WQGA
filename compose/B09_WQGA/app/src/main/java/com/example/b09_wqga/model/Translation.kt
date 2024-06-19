package com.example.b09_wqga.model

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Retrofit Service Interface
interface TranslationService {
    @FormUrlEncoded
    @POST("language/translate/v2")
    suspend fun translate(
        @Field("q") query: String,
        @Field("target") target: String,
        @Field("key") apiKey: String
    ): GTranslationResponse
}

// Retrofit Instance
object RetrofitInstance {
    private const val BASE_URL = "https://translation.googleapis.com/"

    val api: TranslationService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationService::class.java)
    }
}

data class GTranslationResponse(
    val data: GTranslationData
)

data class GTranslationData(
    val translations: List<GTranslation>
)

data class GTranslation(
    val translatedText: String
)


suspend fun translateText(text: String): List<GTranslation> {
    val apiKey = "API KEY" // 깃헙에 커밋 금지, 코드 제출할 때도 삭제 부탁
    val targetLanguage = "ko"
    return try {
        val response = RetrofitInstance.api.translate(text, targetLanguage, apiKey)
        response.data.translations
    } catch (e: Exception) {
        Log.i("translation", "Translation Failed")
        emptyList()
    }
}