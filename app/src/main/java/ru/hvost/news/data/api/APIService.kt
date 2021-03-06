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
import ru.hvost.news.BuildConfig
import ru.hvost.news.data.api.response.*
import java.util.concurrent.TimeUnit

interface APIService {

    @GET("/rest/Authorization/updateFirebaseToken/")
    fun updateFirebaseTokenAsync(
        @Query("userToken") userToken: String?,
        @Query("firebaseToken") firebaseToken: String?
    ): Deferred<SimpleResponse>

    @GET("/rest/Authorization/login/")
    fun loginAsync(
        @Query("login") login: String?,
        @Query("password") password: String?,
        @Query("firebaseToken") firebaseToken: String?
    ): Deferred<LoginResponse>

    @GET("/rest/Authorization/requestSMS/")
    fun requestSMSAsync(
        @Query("userToken") userToken: String?,
        @Query("phone") phone: String?
    ): Deferred<SimpleResponse>

    @GET("/rest/Authorization/sendSecretCode/")
    fun sendSecretCodeAsync(
        @Query("userToken") userToken: String?,
        @Query("phone") phone: String?,
        @Query("secretCode") secretCode: String?
    ): Deferred<SimpleResponse>

    @GET("/rest/Authorization/restorePassword/")
    fun restorePassAsync(
        @Query("email") email: String?
    ): Deferred<PassRestoreResponse>

    @GET("/rest/PetVeterinaryProfile/getAllPreparationsByTypeId/")
    fun getPreparationsAsync(
        @Query("typeId") typeId: String?
    ): Deferred<PreparationsResponse>

    @GET("/rest/PetVeterinaryProfile/getFood/")
    fun getPetFoodAsync(): Deferred<PetFoodResponse>

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
        @Query("voucher") voucher: String?,
        @Query("interests") interests: String,
        @Query("password") password: String
    ): Deferred<RegisterUserResponse>

    @GET("/rest/PetProfile/getPetToys/")
    fun getPetToysAsync(
        @Query("petSpecies") petSpecies: String?
    ): Deferred<PetToysResponse>

    @GET("/rest/PetProfile/getPetEducation/")
    fun getPetEducationAsync(
        @Query("petSpecies") petSpecies: String?
    ): Deferred<PetEducationResponse>

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
        @Query("productId") productId: String,
        @Query("count") count: Int
    ): Deferred<SimpleResponse>

    @GET("/rest/Basket/removeFromCart/")
    fun removeFromCartAsync(
        @Query("userToken") userToken: String?,
        @Query("productId") productId: String,
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

    @GET("/rest/Orders/deleteOrder/")
    fun deleteOrderAsync(
        @Query("userToken") userToken: String?,
        @Query("orderId") orderId: Long
    ): Deferred<SimpleResponse>

    @GET("/rest/Vouchers/getVouchers/")
    fun getVouchersAsync(
        @Query("userToken") userToken: String?
    ): Deferred<GetVouchersResponse>

    @GET("/rest/Vouchers/getVoucherProgram/")
    fun getVoucherProgramAsync(
        @Query("voucherCode") voucherCode: String?
    ): Deferred<GetVoucherProgramResponse>

    @GET("/rest/Vouchers/registerVoucher/")
    fun registerVoucherAsync(
        @Query("userToken") userToken: String?,
        @Query("voucherCode") voucherCode: String?,
        @Query("petId") petId: String?,
        @Query("forceRegister") forceRegister: Boolean = false
    ): Deferred<SimpleResponse>

    @GET("/rest/OnlineShop/getProducts/")
    fun getProductsAsync(
        @Query("userToken") userToken: String?,
        @Query("voucherCode") voucherCode: String?,
    ): Deferred<ProductsResponse>

    @GET("/rest/UserProfile/getUserProfile/")
    fun getUserDataAsync(
        @Query("userToken") userToken: String? = null
    ): Deferred<UserDataResponse>

    @GET("/rest/Articles/getArticles/")
    fun getArticlesAsync(
        @Query("userToken") userToken: String? = null
    ): Deferred<ArticlesResponse>

    @GET("/rest/Articles/getArticle/")
    fun getArticleAsync(
        @Query("userToken") userToken: String? = null,
        @Query("articleId") articleId: String?
    ): Deferred<ArticlesResponse>

    @GET("/rest/Registration/getSpecies/")
    fun getSpeciesAsync(): Deferred<SpeciesResponse>

    @GET("/rest/PetProfile/getPetSports/")
    fun getPetSports(
        @Query("petSpecies") petSpecies: String?
    ): Deferred<PetSportsResponse>

    @GET("/rest/PetProfile/getPetBadHabbits/")
    fun getPetBadHabbits(
        @Query("petSpecies") petSpecies: String?
    ): Deferred<PetBadHabbitsResponse>

    @GET("/rest/PetProfile/getPetFeed/")
    fun getPetFeed(): Deferred<PetFeedResponse>

    @GET("/rest/PetProfile/getPetDelicies/")
    fun getPetDelicies(
        @Query("petSpecies") petSpecies: String?
    ): Deferred<PetDeliciesResponse>

    @GET("/rest/PetVeterinaryProfile/getNotificationPeriod/")
    fun getNotificationPeriod(): Deferred<NotificationPeriodResponse>

    @GET("/rest/PetVeterinaryProfile/getPetDiseases/")
    fun getPetDiseases(): Deferred<PetDiseasesResponse>

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

    @GET("/rest/InviteFriend/deletePrize/")
    fun removePrizeFromCartAsync(
        @Query("userToken") userToken: String?,
        @Query("prizeId") prizeId: String?
    ): Deferred<SimpleResponse>

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
        @Query("interests") interests: String?,
        @Query("mailNotifications") mailNotifications: Boolean?,
        @Query("sendPushes") sendPushes: Boolean?
    ): Deferred<UserDataResponse>

    @GET("/rest/PetProfile/updatePet/")
    fun updatePetAsync(
        @Query("userToken") userToken: String?,
        @Query("petId") petId: String?,
        @Query("petName") petName: String?,
        @Query("petSpecies") petSpecies: String?,
        @Query("petSex") petSex: String?,
        @Query("petBreed") petBreed: String?,
        @Query("petBirthday") petBirthday: String?,
        @Query("petDelicies") petDelicies: String?,
        @Query("petToy") petToy: String?,
        @Query("petFeed") petFeed: String?,
        @Query("petBadHabbit") petBadHabbit: String?,
        @Query("petChip") petChip: String?,
        @Query("isPetForShows") isPetForShows: Boolean?,
        @Query("hasTitles") hasTitles: Boolean?,
        @Query("petSports") sportsPet: String?,
        @Query("visitsSaloons") visitsSaloons: Boolean?,
        @Query("petEducation") petEducation: String?
    ): Deferred<UpdatePetResponse>

    @GET("/rest/PetProfile/addPet/")
    fun addPetAsync(
        @Query("userToken") userToken: String?,
        @Query("petName") petName: String?,
        @Query("petSpecies") petSpecies: String?,
        @Query("petSex") petSex: String?,
        @Query("petBirthday") petBirthday: String?,
    ): Deferred<AddPetResponse>

    // Coupons
    @GET("/rest/Coupons/getCoupons/")
    fun getCouponsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<CouponsResponse>

    @GET("/rest/Coupons/getCouponsInfo/")
    fun getCouponsInfoAsync(): Deferred<CouponInfoResponse>

    @GET("/rest/PetProfile/getPets/")
    fun getPetsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<PetsResponse>

    // School
    @GET("/rest/School/getOfflineLessons/")
    fun getOfflineSeminarsAsync(
        @Query("cityId") cityId: String?,
        @Query("userToken") userToken: String?
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

    @GET("/rest/Articles/addArticleView/")
    fun setArticleViewedByUserAsync(
        @Query("userToken") userToken: String?,
        @Query("articleId") articleId: String?
    ): Deferred<AddViewedByUserResponse>

    @GET("/rest/PetVeterinaryProfile/getPetPassport/")
    fun getPetPassportAsync(
        @Query("userToken") userToken: String?,
        @Query("petId") petId: String?
    ): Deferred<PetPassportResponse>

    @GET("/rest/PetVeterinaryProfile/updatePassport/")
    fun updatePetPassportAsync(
        @Query("userToken") userToken: String?,
        @Query("petId") petId: String?,
        @Query("isSterilised") isSterilised: Boolean?,
        @Query("vacineId") vacineId: String?,
        @Query("vacinationDate") vacinationDate: String?,
        @Query("vacinationPeriodId") vacinationPeriodId: String?,
        @Query("dewormingId") dewormingId: String?,
        @Query("dewormingDate") dewormingDate: String?,
        @Query("dewormingPeriodId") dewormingPeriodId: String?,
        @Query("exoparasiteId") exoparasiteId: String?,
        @Query("exoparasiteDate") exoparasitesDate: String?,
        @Query("exoparasitePeriodId") exoparasitesPeriodId: String?,
        @Query("feedingTypeId") feeding: String?,
        @Query("diseases") diseases: String?,
        @Query("favouriteVetName") favouriteVetName: String?,
        @Query("favouriteVetAdress") favouriteVetAdress: String?
    ): Deferred<UpdatePetPassportResponse>

    @GET("/rest/Articles/setLiked/")
    fun setArticleLikedByUserAsync(
        @Query("userToken") userToken: String?,
        @Query("articleId") articleId: String?,
        @Query("liked") liked: Boolean?
    ): Deferred<AddLikedByUserResponse>

    @GET("/rest/School/getCities/")
    fun getOfflineCitiesAsync(): Deferred<OfflineCitiesResponse>

    @GET("/rest/School/setParticipate/")
    fun setParticipateAsync(
        @Query("userToken") userToken: String?,
        @Query("schoolId") schoolId: String?,
        @Query("petId") petId: String?
    ): Deferred<SetParticipateResponse>

    @GET("/rest/School/setSubscribe/")
    fun setSubscribeAsync(
        @Query("userToken") userToken: String?,
        @Query("schoolId") schoolId: String?,
    ): Deferred<SetSubscribeResponse>

    @GET("/rest/Maps/getShops/")
    fun getShopsAsync(
        @Query("userToken") userToken: String?
    ): Deferred<ShopsResponse>

    @GET("/rest/Maps/setIsShopFavourite/")
    fun setIsShopFavouriteAsync(
        @Query("userToken") userToken: String?,
        @Query("shopId") shopId: String?,
        @Query("isFavourite") isFavourite: String?
    ): Deferred<SimpleResponse>

    companion object {

        const val YANDEX_MAPKIT_KEY = "d5164e0f-9659-42eb-8e27-26b0bf835464"

        const val baseUrl = "https://hvost.news/"

        private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val API: APIService by lazy {
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.callTimeout(60, TimeUnit.SECONDS)
            okHttpClient.connectTimeout(20, TimeUnit.SECONDS)
            okHttpClient.readTimeout(20, TimeUnit.SECONDS)
            okHttpClient.writeTimeout(20, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) okHttpClient.authenticator(HTTPAuthenticator())
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
        override fun authenticate(route: Route?, response: Response): Request {
            return response.request.newBuilder()
                .header(
                    "Authorization",
                    Credentials.basic("dev", "qOuYXDwtOsyv0UMkCD6a")
                )
                .build()
        }
    }

}