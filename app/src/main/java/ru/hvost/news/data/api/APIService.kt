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

    @GET("/rest/Registration/getSpecies/")
    fun getSpeciesAsync(): Deferred<SpeciesResponse>

    @GET("/rest/Registration/getBreeds/")
    fun getBreedsAsync(
        @Query("specId") specId: Int?
    ): Deferred<BreedsResponse>

    @GET("/rest/PetProfile/deletePet/")
    fun deletePetAsync(
        @Query("userToken") userToken: String?,
        @Query("petId") petId: String?
    ): Deferred<DeletePetResponse>

    @GET("/rest/InviteFriend/getBonusBalance/")
    fun getBonusBalanceAsync(
        @Query("userToken") userToken: String?,
    ): Deferred<BonusBalanceResponse>

    @GET("/rest/UserProfile/updateUserProfile/")
    fun getUpdateUserProfileAsync(
        @Query("userToken") userToken: String?,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("patronymic") patronymic: String?,
        @Query("phone") phone: String?,
        @Query("email") email: String?,
        @Query("city") city: String?,
        @Query("birthday") birthday: String?,
        @Query("interests") interests: List<String>?
    ): Deferred<UserDataResponse>

    // Coupons
    @GET("/rest/Coupons/getCoupons/")
    fun getCouponsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<CouponsResponse>

    @GET("/rest/School/getOnlineLessons/")
    fun getCouponsInfoAsync(
        @Query("userToken") userToken: String?
    ): Deferred<CouponInfoResponse>

    @GET("/rest/PetProfile/getPets/")
    fun getPetsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<PetsResponse>

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

    @GET("/rest/School/setLessonTestPassed/")
    fun setLessonTestesPassedAsync(
        @Query("userToken") userToken: String?,
        @Query("lessonId") lessonId: Long?
    ): Deferred<LessonTestesPassedResponse>

    // Map
    @GET("/rest/Maps/getShops/")
    fun getShopsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<ShopsResponse>

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