package ru.hvost.news.data.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.hvost.news.data.api.response.*
import java.util.concurrent.TimeUnit

interface APIService {

    @GET("/rest/Authorization/login/")
    fun loginAsync(
        @Query("login") login: String?,
        @Query("password") password: String?
    ): Deferred<LoginResponse>

    @GET("/rest/Authorization/restorePassword/")
    fun restorePassAsync(
        @Query("email") email: String?
    ): Deferred<PassRestoreResponse>

    @GET("/rest/UserProfile/getUserProfile/")
    fun getUserDataAsync(
        @Query("userToken") userToken: String? = null
    ): Deferred<UserDataResponse>

    @GET("/rest/Articles/getArticles/")
    fun getArticlesAsync(
        @Query("userToken") userToken: String? = null
    ): Deferred<ArticlesResponse>

    // Coupons
    @GET("/rest/Coupons/getCoupons/")
    fun getCouponsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<CouponsResponse>

    @GET()
    fun getCouponsInfoAsync(): Deferred<CouponInfoResponse>

    // School
    @GET("/rest/School/getOfflineLessons/")
    fun getOfflineLessonsAsync(
        @Query("city") city: String?
    ): Deferred<OfflineLessonsResponse>

    @GET("/rest/School/getOnlineLessons/")
    fun getOnlineLessonsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<OnlineLessonsResponse>

    @GET("/rest/School/getOnlineSchools/")
    fun getOnlineSchoolsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<OnlineSchoolsResponse>

    @GET()
    fun setLessonTestesPassedAsync(
        userToken: String,
        lessonId: Long
    ): Deferred<LessonTestesPassedResponse>

    // Map
    @GET()
    fun getShopsAsync(userToken: String): Deferred<ShopsResponse>

    companion object {
        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        const val baseUrl = "http://hvost-news.testfact3.ru"

        val API: APIService by lazy {
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.callTimeout(90, TimeUnit.SECONDS)
            okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
            okHttpClient.authenticator(HTTPAuthenticator())
            val retrofit = Retrofit.Builder()
                .client(okHttpClient.build())
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
            retrofit.create(APIService::class.java)
        }
    }

    class HTTPAuthenticator : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            return response.request.newBuilder()
                .header(
                    "Authorization",
                    Credentials.basic("dev", "qOuYXDwtOsyv0UMkCD6a")
                )
                .build()
        }
    }

}