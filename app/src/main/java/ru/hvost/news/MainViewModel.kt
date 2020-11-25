package ru.hvost.news

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.*
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent

class MainViewModel : ViewModel() {

    val articlesState = MutableLiveData<State>()
    val articles = MutableLiveData<List<Article>>()
    val allArticles = MutableLiveData<List<Article>>()
    val allArticlesState = MutableLiveData<State>()

    val userDataState = MutableLiveData<State>()
    val userDataResponse = MutableLiveData<UserDataResponse>()
    val changeUserDataState = MutableLiveData<State>()

    val userPetsState = MutableLiveData<State>()
    val userPetsResponse = MutableLiveData<List<Pets>>()

    val petsSpeciesState = MutableLiveData<State>()
    val petsSpeciesResponse = MutableLiveData<List<Species>>()

    val petsBreedsState = MutableLiveData<State>()
    val petsBreedsResponse = MutableLiveData<List<Breeds>>()

    val petDeleteState = MutableLiveData<State>()
    val petDeleteResponse = MutableLiveData<DeletePetResponse>()

    val inviteLinkState = MutableLiveData<State>()
    val inviteLinkResponse = MutableLiveData<InviteLinkResponse>()

    val sendInviteState = MutableLiveData<State>()
    val sendInviteResponse = MutableLiveData<SendToEmailResponse>()

    val bonusBalanceState = MutableLiveData<State>()
    val bonusBalanceResponse = MutableLiveData<BonusBalanceResponse>()

    val prizeCategoriesState = MutableLiveData<State>()
    val prizeCategoriesResponse = MutableLiveData<List<PrizeCategory>>()

    val prizeToCartState = MutableLiveData<State>()
    val prizeToCartResponse = MutableLiveData<PrizeToCartResponse>()

    val prizesState = MutableLiveData<State>()
    val prizesResponse = MutableLiveData<PrizesResponse>()
    val prizes = MutableLiveData<List<Prize>>()

    var categories: List<Categories>? = null
    var domains: List<Domain>? = null

    //Событие, сообщающее о необходимости закрытия инструкций.
    val closeInstructionsEvent = MutableLiveData<OneTimeEvent>()

    private val _loadingVouchersEvent = MutableLiveData<NetworkEvent<State>>()

    init {
        loadArticles()
        loadAllArticles()
        loadUserData()
        loadPetsData()
        loadSpecies()
        getBonusBalance()
        getInviteLink()
        getPrizeCategories()
        updateVouchers(App.getInstance().userToken)
    }

    fun getBonusBalance() {
        viewModelScope.launch {
            bonusBalanceState.value = State.LOADING
            try {
                val response =
                    APIService.API.getBonusBalanceAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    bonusBalanceResponse.value = response
                    bonusBalanceState.value = State.SUCCESS
                } else bonusBalanceState.value = State.ERROR
            } catch (exc: Exception) {
                bonusBalanceState.value = State.FAILURE
            }
        }
    }

    fun getPrizeCategories() {
        viewModelScope.launch {
            prizeCategoriesState.value = State.LOADING
            try {
                val response =
                    APIService.API.getPrizeCategoriesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    prizeCategoriesResponse.value = response.categories?.toPrizeCategories()
                    prizeCategoriesState.value = State.SUCCESS
                } else prizeCategoriesState.value = State.ERROR
            } catch (exc: Exception) {
                prizeCategoriesState.value = State.FAILURE
            }
        }
    }

    fun addPrizeToCart(
        id: String?
    ) {
        viewModelScope.launch {
            prizeToCartState.value = State.LOADING
            try {
                val response =
                    APIService.API.addPrizeToCartAsync(App.getInstance().userToken, id).await()
                if (response.result == "success") {
                    prizeToCartResponse.value = response
                    prizeToCartState.value = State.SUCCESS
                } else prizeToCartState.value = State.ERROR
            } catch (exc: Exception) {
                prizeToCartState.value = State.FAILURE
            }
        }
    }

    fun getInviteLink() {
        viewModelScope.launch {
            inviteLinkState.value = State.LOADING
            try {
                val response =
                    APIService.API.getInviteLinkAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    inviteLinkResponse.value = response
                    inviteLinkState.value = State.SUCCESS
                } else inviteLinkState.value = State.ERROR
            } catch (exc: Exception) {
                inviteLinkState.value = State.FAILURE
            }
        }
    }

    fun SendInviteToMail(
        email: String?
    ) {
        viewModelScope.launch {
            sendInviteState.value = State.LOADING
            try {
                val response =
                    APIService.API.sendInviteToEmailAsync(App.getInstance().userToken, email)
                        .await()
                if (response.result == "success") {
                    sendInviteResponse.value = response
                    sendInviteState.value = State.SUCCESS
                } else sendInviteState.value = State.ERROR
            } catch (exc: Exception) {
                sendInviteState.value = State.FAILURE
            }
        }
    }

    private fun loadArticles() {
        viewModelScope.launch {
            articlesState.value = State.LOADING
            try {
                val response = APIService.API.getArticlesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    articles.value = response.articles?.toArticles()
                    articlesState.value = State.SUCCESS
                } else articlesState.value = State.ERROR
            } catch (exc: Exception) {
                articlesState.value = State.FAILURE
            }
        }
    }

    fun loadPrizes(
        prizeCategoryId: String
    ) {
        viewModelScope.launch {
            prizesState.value = State.LOADING
            try {
                val response =
                    APIService.API.getPrizesAsync(App.getInstance().userToken, prizeCategoryId)
                        .await()
                if (response.result == "success") {
                    prizesResponse.value = response
                    prizes.value = response.prizes?.toPrize()
                    prizesState.value = State.SUCCESS
                } else {
                    prizes.value = listOf()
                    prizesState.value = State.ERROR
                }
            } catch (exc: Exception) {
                prizes.value = listOf()
                prizesState.value = State.FAILURE
            }
        }
    }

    private fun loadAllArticles() {
        viewModelScope.launch {
            allArticlesState.value = State.LOADING
            try {
                val response = APIService.API.getArticlesAsync().await()
                if (response.result == "success") {
                    allArticles.value = response.articles?.toArticles()
                    categories = allArticles.value?.toCategory()
                    domains = allArticles.value?.toOfflineLessons()
                    allArticlesState.value = State.SUCCESS
                } else allArticlesState.value = State.ERROR
            } catch (exc: Exception) {
                allArticlesState.value = State.FAILURE
            }
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            userDataState.value = State.LOADING
            try {
                val response = APIService.API.getUserDataAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    userDataResponse.value = response
                    userDataState.value = State.SUCCESS
                } else userDataState.value = State.ERROR
            } catch (exc: Exception) {
                userDataState.value = State.FAILURE
            }
        }
    }

    fun loadPetsData() {
        viewModelScope.launch {
            userPetsState.value = State.LOADING
            try {
                val response = APIService.API.getPetsAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    userPetsResponse.value = response.pets?.toPets()
                    userPetsState.value = State.SUCCESS
                } else userPetsState.value = State.ERROR
            } catch (exc: Exception) {
                userPetsState.value = State.FAILURE
            }
        }
    }

    fun loadSpecies() {
        viewModelScope.launch {
            petsSpeciesState.value = State.LOADING
            try {
                val response = APIService.API.getSpeciesAsync().await()
                if (response.result == "success") {
                    petsSpeciesResponse.value = response.species?.toSpecies()
                    petsSpeciesState.value = State.SUCCESS
                } else petsSpeciesState.value = State.ERROR
            } catch (exc: Exception) {
                petsSpeciesState.value = State.FAILURE
            }
        }
    }

    fun loadBreeds(
        specId: Int
    ) {
        viewModelScope.launch {
            petsBreedsState.value = State.LOADING
            try {
                val response = APIService.API.getBreedsAsync(specId).await()
                if (response.result == "success") {
                    petsBreedsResponse.value = response.breeds?.toBreeds()
                    petsBreedsState.value = State.SUCCESS
                } else petsBreedsState.value = State.ERROR
            } catch (exc: Exception) {
                petsBreedsState.value = State.FAILURE
            }
        }
    }

    fun deletePet(
        petId: String
    ) {
        viewModelScope.launch {
            petDeleteState.value = State.LOADING
            try {
                val response =
                    APIService.API.deletePetAsync(
                        userToken = App.getInstance().userToken,
                        petId = petId
                    ).await()
                if (response.result == "success") {
                    petDeleteResponse.value = response
                    petDeleteState.value = State.SUCCESS
                } else petDeleteState.value = State.ERROR
            } catch (exc: Exception) {
                petDeleteState.value = State.FAILURE
            }
        }
    }

    fun changeUserData(
        name: String?,
        surname: String?,
        patronymic: String?,
        phone: String?,
        email: String?,
        birthday: String?,
        city: String?,
        interests: List<String>? = null
    ) {
        viewModelScope.launch {
            changeUserDataState.value = State.LOADING
            try {
                val response = APIService.API.getUpdateUserProfileAsync(
                    userToken = App.getInstance().userToken,
                    name = name,
                    surname = surname,
                    patronymic = patronymic,
                    phone = phone,
                    email = email,
                    birthday = birthday,
                    city = city,
                    interests = interests
                ).await()
                if (response.result == "success") {
                    changeUserDataState.value = State.SUCCESS
                } else changeUserDataState.value = State.ERROR
            } catch (exc: Exception) {
                changeUserDataState.value = State.FAILURE
            }
        }
    }

    private val _loadingOrdersEvent = MutableLiveData<NetworkEvent<State>>()
    val loadingOrdersEvent: LiveData<NetworkEvent<State>> = _loadingOrdersEvent
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders
    var ordersFilter = MutableLiveData<String>()
    private val _orderSelectedEvent = MutableLiveData<Event<Order>>()
    val orderSelectedEvent: LiveData<Event<Order>> = _orderSelectedEvent

    val ordersInWork = Transformations.map(orders) { list -> list.count { it.orderStatus == "N" } }
    val ordersConstructed =
        Transformations.map(orders) { list -> list.count { it.orderStatus == "DT" } }
    val ordersFinished =
        Transformations.map(orders) { list -> list.count { it.orderStatus == "F" } }

    fun updateOrders(userToken: String?) {
        viewModelScope.launch {
            _loadingOrdersEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getOrdersAsync(userToken).await()
                if (result.result == "success") {
                    _orders.value = result.toOrders()
                    _loadingOrdersEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _loadingOrdersEvent.value = NetworkEvent(State.ERROR, result.error)
                    _orders.value = listOf()
                }
            } catch (exc: Exception) {
                _loadingOrdersEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _orders.value = listOf()
            }
        }
    }

    fun showOrder(order: Order) {
        _orderSelectedEvent.value = Event(order)
    }

    val loadingVouchersEvent: LiveData<NetworkEvent<State>> = _loadingVouchersEvent
    private val _vouchers = MutableLiveData<List<VoucherItem>>()
    val vouchers: LiveData<List<VoucherItem>> = _vouchers
    val vouchersFooterClickEvent = MutableLiveData<OneTimeEvent>()
    val vouchersFilter = MutableLiveData<String>()

    fun updateVouchers(userToken: String?) {
        viewModelScope.launch {
            _loadingVouchersEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getVouchersAsync(userToken).await()
                if(result.result == "success") {
                    _loadingVouchersEvent.value = NetworkEvent(State.SUCCESS)
                    _vouchers.value = result.toVouchers()
                } else {
                    _loadingVouchersEvent.value = NetworkEvent(State.ERROR, result.error)
                    _vouchers.value = listOf()
                }
            } catch (exc: Exception) {
                _loadingVouchersEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _vouchers.value = listOf()
            }
        }
    }

    companion object {
        const val SEX_MALE = 8
        const val SEX_FEMALE = 9
        const val UNKNOWN = 0
    }

}