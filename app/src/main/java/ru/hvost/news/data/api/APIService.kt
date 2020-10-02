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
import ru.hvost.news.data.api.response.LoginResponse
import java.util.concurrent.TimeUnit

interface APIService {

    @GET("/rest")
    fun loginAsync(
        @Query("login") login: String?,
        @Query("password") password: String?
    ): Deferred<LoginResponse>

    companion object{
        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        private const val baseUrl = "https://" //TODO: Add base URL.

        val API : APIService by lazy {
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

    class HTTPAuthenticator : Authenticator{
        override fun authenticate(route: Route?, response: Response): Request? {
            return response.request.newBuilder()
                .header(
                    "Authorization",
                    Credentials.basic("dev", "qOuYXDwtOsyv0UMkCD6a"))
                .build()
        }
    }

}