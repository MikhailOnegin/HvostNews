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

    @GET("/rest/Registration/getInterests/")
    fun getInterestsAsync(): Deferred<InterestsResponse>

    @GET("/rest/Registration/registerUser/")
    fun registerUserAsync(
        @Query("name") name: String,
        @Query("surname") surname: String,
        @Query("patronymic") patronymic: String,
        @Query("phone") phone: String,
        @Query("email") email: String,
        @Query("city") city: String,
        @Query("petName") petName: String,
        @Query("petSpecies") petSpecies: String,
        @Query("petSex") petSex: String,
        @Query("petBirthday") petBirthday: String,
        @Query("voucher") voucher: String? = null,
        @Query("interests") interests: String,
        @Query("password") password: String
    ): Deferred<RegisterUserResponse>

    @GET("/rest/Basket/getCart/")
    fun getCartAsync(
        @Query("userToken") userToken: String?
    ): Deferred<CartResponse>

    @GET("/rest/InviteFriend/getPrizeCategories/")
    fun getPrizeCategoriesAsync(
        @Query("userToken") userToken: String?
    ): Deferred<PrizeCategoriesResponse>

    @GET("/rest/Basket/addToCart/")
    fun addToCartAsync(
        @Query("userToken") userToken: String?,
        @Query("productId") productId: Long,
        @Query("count") count: Int
    ): Deferred<SimpleResponse>

    @GET("/rest/Basket/removeFromCart/")
    fun removeFromCartAsync(
        @Query("userToken") userToken: String?,
        @Query("productId") productId: Long,
        @Query("count") count: Int
    ): Deferred<SimpleResponse>

    @GET("/rest/Basket/makeOrder/")
    fun makeOrderAsync(
        @Query("userToken") userToken: String?,
        @Query("name") name: String?,
        @Query("phone") phone: String?,
        @Query("email") email: String?,
        @Query("city") city: String?,
        @Query("street") street: String?,
        @Query("house") house: String?,
        @Query("flat") flat: String?,
        @Query("saveDataForFuture") saveDataForFuture: String?
    ): Deferred<MakeOrderResponse>

    @GET("/rest/Orders/getOrders/")
    fun getOrdersAsync(
        @Query("userToken") userToken: String?
    ): Deferred<GetOrdersResponse>

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

    @GET("/rest/InviteFriend/getPrizes/")
    fun getPrizesAsync(
        @Query("userToken") userToken: String?,
        @Query("categoryId") categoryId: String?,
    ): Deferred<PrizesResponse>

    @GET("/rest/Registration/getBreeds/")
    fun getBreedsAsync(
        @Query("specId") specId: Int?
    ): Deferred<BreedsResponse>

    @GET("/rest/PetProfile/deletePet/")
    fun deletePetAsync(
        @Query("userToken") userToken: String?,
        @Query("petId") petId: String?
    ): Deferred<DeletePetResponse>

    @GET("/rest/InviteFriend/getInviteLink/")
    fun getInviteLinkAsync(
        @Query("userToken") userToken: String?,
    ): Deferred<InviteLinkResponse>

    @GET("/rest/InviteFriend/sendInviteToEmail/")
    fun sendInviteToEmailAsync(
        @Query("userToken") userToken: String?,
        @Query("email") email: String?,
    ): Deferred<SendToEmailResponse>

    @GET("/rest/InviteFriend/selectPrize/")
    fun addPrizeToCartAsync(
        @Query("userToken") userToken: String?,
        @Query("prizeId") id: String?,
    ): Deferred<PrizeToCartResponse>

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

    @GET("/rest/PetProfile/addPet/")
    fun addPetAsync(
        @Query("userToken") userToken: String?,
        @Query("petName") petName: String?,
        @Query("petSpecies") petSpecies: String?,
        @Query("petSex") petSex: String?,
        @Query("petBreed") petBreed: String?,
        @Query("petBirthday") petBirthday: String?,
        @Query("petDelicies") petDelicies: String?,
        @Query("petToy") petToy: String?,
        @Query("petBadHabbit") petBadHabbit: String?,
        @Query("petChip") petChip: String?,
        @Query("isPetForShows") isPetForShows: Boolean?,
        @Query("hasTitles") hasTitles: Boolean?,
        @Query("isSportsPet") isSportsPet: Boolean?,
        @Query("visitsSaloons") visitsSaloons: Boolean?,
        @Query("petEducation") petEducation: String?
    ): Deferred<AddPetResponse>

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
    fun getOfflineSeminarsAsync(
        @Query("cityId") cityId: String?
    ): Deferred<OfflineSeminarsResponse>

    @GET("/rest/School/getOnlineLessons/")
    fun getOnlineLessonsAsync(
        @Query("userToken") userToken: String?,
        @Query("schoolId") schoolId: String?
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

    @GET("/rest/School/getCities/")
    fun getOfflineCitiesAsync():Deferred<OfflineCitiesResponse>

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
            okHttpClient.callTimeout(60, TimeUnit.SECONDS)
            okHttpClient.connectTimeout(20, TimeUnit.SECONDS)
            okHttpClient.readTimeout(20, TimeUnit.SECONDS)
            okHttpClient.writeTimeout(20, TimeUnit.SECONDS)
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