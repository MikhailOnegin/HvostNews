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

    val petDeleteState = MutableLiveData<State>()
    val petDeleteResponse = MutableLiveData<DeletePetResponse>()

    val inviteLinkState = MutableLiveData<State>()
    val inviteLinkResponse = MutableLiveData<InviteLinkResponse>()

    val sendInviteState = MutableLiveData<State>()
    val sendInviteResponse = MutableLiveData<SendToEmailResponse>()

    val prizeCategoriesState = MutableLiveData<State>()
    val prizeCategoriesResponse = MutableLiveData<List<PrizeCategory>>()

    val prizeToCartState = MutableLiveData<State>()
    val prizeToCartResponse = MutableLiveData<PrizeToCartResponse>()

    val prizesState = MutableLiveData<State>()
    val prizesResponse = MutableLiveData<PrizesResponse>()
    val prizes = MutableLiveData<List<Prize>>()

    var categories: List<Categories>? = null
    var domains: List<Domain>? = null

    private val _interestsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val interestsLoadingEvent: LiveData<NetworkEvent<State>> = _interestsLoadingEvent
    private val _interests = MutableLiveData<List<CategoryItem>>()
    val interests: LiveData<List<CategoryItem>> = _interests

    private val _articleViewedLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val articleViewedLoadingEvent: LiveData<NetworkEvent<State>> = _articleViewedLoadingEvent

    private val _articleLikedLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val articleLikedLoadingEvent: LiveData<NetworkEvent<State>> = _articleLikedLoadingEvent
    private val _articleIsLiked = MutableLiveData<AddLikedByUserResponse>()
    val articleIsLiked: LiveData<AddLikedByUserResponse> = _articleIsLiked

    private val _petToysLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petToysLoadingEvent: LiveData<NetworkEvent<State>> = _petToysLoadingEvent
    private val _petToys = MutableLiveData<List<Toys>>()
    val petToys: LiveData<List<Toys>> = _petToys

    private val _petEducationLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petEducationLoadingEvent: LiveData<NetworkEvent<State>> = _petEducationLoadingEvent
    private val _petEducation = MutableLiveData<List<PetEducation>>()
    val petEducation: LiveData<List<PetEducation>> = _petEducation

    private val _userPetsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val userPetsLoadingEvent: LiveData<NetworkEvent<State>> = _userPetsLoadingEvent
    private val _userPets = MutableLiveData<List<Pets>>()
    val userPets: LiveData<List<Pets>> = _userPets

    private val _petsSpeciesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petsSpeciesLoadingEvent: LiveData<NetworkEvent<State>> = _petsSpeciesLoadingEvent
    private val _petsSpecies = MutableLiveData<List<Species>>()
    val petsSpecies: LiveData<List<Species>> = _petsSpecies

    private val _userDataLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val userDataLoadingEvent: LiveData<NetworkEvent<State>> = _userDataLoadingEvent
    private val _changeUserDataLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val changeUserDataLoadingEvent: LiveData<NetworkEvent<State>> = _changeUserDataLoadingEvent
    private val _userData = MutableLiveData<UserDataResponse>()
    val userData: LiveData<UserDataResponse> = _userData

    private val _petsBreedsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petsBreedsLoadingEvent: LiveData<NetworkEvent<State>> = _petsBreedsLoadingEvent
    private val _petsBreeds = MutableLiveData<List<Breeds>>()
    val petsBreeds: LiveData<List<Breeds>> = _petsBreeds

    private val _bonusBalanceLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val bonusBalanceLoadingEvent: LiveData<NetworkEvent<State>> = _bonusBalanceLoadingEvent
    private val _bonusBalance = MutableLiveData<BonusBalanceResponse>()
    val bonusBalance: LiveData<BonusBalanceResponse> = _bonusBalance

    //Событие, сообщающее о необходимости закрытия инструкций.
    val closeInstructionsEvent = MutableLiveData<OneTimeEvent>()

    private val _loadingVouchersEvent = MutableLiveData<NetworkEvent<State>>()

    //событие, сообщающее о закрытии ArticlesFilterCustomDialog
    val closeArticlesFilterCustomDialog = MutableLiveData<OneTimeEvent>()

    //событие, сообщающее об изменении интересов пользователя
    val updateArticlesWithNewInterests = MutableLiveData<OneTimeEvent>()

//    val enableFilterButton = MutableLiveData<Boolean>()

    private val _loadingArticlesEvent = MutableLiveData<NetworkEvent<State>>()
    val loadingArticlesEvent: LiveData<NetworkEvent<State>> = _loadingArticlesEvent

    init {
        initializeData()
    }

    fun initializeData() {
        loadArticles()
        loadAllArticles()
        getBonusBalance()
        getInviteLink()
        getPrizeCategories()
        updateVouchers(App.getInstance().userToken)
        loadInterests()
    }

    fun getBonusBalance() {
        viewModelScope.launch {
            _bonusBalanceLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getBonusBalanceAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _bonusBalance.value = response
                    _bonusBalanceLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _bonusBalanceLoadingEvent.value = NetworkEvent(State.ERROR)
                    _bonusBalance.value = null
                }
            } catch (exc: Exception) {
                _bonusBalanceLoadingEvent.value = NetworkEvent(State.FAILURE)
                _bonusBalance.value = null
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

    fun loadArticles() {
        viewModelScope.launch {
            articlesState.value = State.LOADING
            _loadingArticlesEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getArticlesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    articles.value = response.articles?.toArticles()
                    articlesState.value = State.SUCCESS
                    _loadingArticlesEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    articlesState.value = State.ERROR
                    _loadingArticlesEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                articlesState.value = State.FAILURE
                _loadingArticlesEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
            _userDataLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getUserDataAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _userData.value = response
                    _userDataLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _userDataLoadingEvent.value = NetworkEvent(State.ERROR)
                    _userData.value = null
                }
            } catch (exc: Exception) {
                _userDataLoadingEvent.value = NetworkEvent(State.FAILURE)
                _userData.value = null
            }
        }
    }

    fun loadPetsData() {
        viewModelScope.launch {
            _userPetsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getPetsAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _userPets.value = response.pets?.toPets()
                    _userPetsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _userPetsLoadingEvent.value = NetworkEvent(State.ERROR)
                    _userPets.value = listOf()
                }
            } catch (exc: Exception) {
                _userPetsLoadingEvent.value = NetworkEvent(State.FAILURE)
                _userPets.value = listOf()
            }
        }
    }

    fun loadSpecies() {
        viewModelScope.launch {
            _petsSpeciesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getSpeciesAsync().await()
                if (response.result == "success") {
                    _petsSpecies.value = response.species?.toSpecies()
                    _petsSpeciesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petsSpeciesLoadingEvent.value = NetworkEvent(State.ERROR)
                    _petsSpecies.value = listOf()
                }
            } catch (exc: Exception) {
                _petsSpeciesLoadingEvent.value = NetworkEvent(State.FAILURE)
                _petsSpecies.value = listOf()
            }
        }
    }

    fun loadBreeds(
        specId: Int
    ) {
        viewModelScope.launch {
            _petsBreedsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getBreedsAsync(specId).await()
                if (response.result == "success") {
                    _petsBreeds.value = response.breeds?.toBreeds()
                    _petsBreedsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petsBreedsLoadingEvent.value = NetworkEvent(State.ERROR)
                    _petsBreeds.value = listOf()
                }
            } catch (exc: Exception) {
                _petsBreedsLoadingEvent.value = NetworkEvent(State.FAILURE)
                _petsBreeds.value = listOf()
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
        name: String? = null,
        surname: String? = null,
        patronymic: String? = null,
        phone: String? = null,
        email: String? = null,
        birthday: String? = null,
        city: String? = null,
        interests: String? = null
    ) {
        viewModelScope.launch {
            _changeUserDataLoadingEvent.value = NetworkEvent(State.LOADING)
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
                    _changeUserDataLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _changeUserDataLoadingEvent.value = NetworkEvent(State.ERROR)
                }
            } catch (exc: Exception) {
                _changeUserDataLoadingEvent.value = NetworkEvent(State.FAILURE)
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

    fun setArticleViewed(
        articleId: String
    ) {
        viewModelScope.launch {
            _articleViewedLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.setArticleViewedByUserAsync(
                    App.getInstance().userToken,
                    articleId
                ).await()
                if (result.result == "success") {
                    _articleViewedLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _articleViewedLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _articleViewedLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun loadPetEducation() {
        viewModelScope.launch {
            _petEducationLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getPetEducationAsync().await()
                if (result.result == "success") {
                    _petEducationLoadingEvent.value = NetworkEvent(State.SUCCESS)
                    _petEducation.value = result.educations?.toEducation()
                } else {
                    _petEducationLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                    _petEducation.value = listOf()
                }
            } catch (exc: Exception) {
                _petEducationLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petEducation.value = listOf()
            }
        }
    }

    fun loadPetToys() {
        viewModelScope.launch {
            _petToysLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getPetToysAsync().await()
                if (result.result == "success") {
                    _petToysLoadingEvent.value = NetworkEvent(State.SUCCESS)
                    _petToys.value = result.toys?.toToys()
                } else {
                    _petToysLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                    _petToys.value = listOf()
                }
            } catch (exc: Exception) {
                _petToysLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petToys.value = listOf()
            }
        }
    }

    fun setArticleLiked(
        articleId: String
    ) {
        viewModelScope.launch {
            _articleLikedLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.setArticleLikedByUserAsync(
                    App.getInstance().userToken,
                    articleId
                ).await()
                if (result.result == "success") {
                    _articleIsLiked.value = result
                    _articleLikedLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _articleLikedLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                    _articleIsLiked.value = null
                }
            } catch (exc: Exception) {
                _articleLikedLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _articleIsLiked.value = null
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
                if (result.result == "success") {
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

    private val _addPetEvent = MutableLiveData<NetworkEvent<State>>()
    val addPetEvent: LiveData<NetworkEvent<State>> = _addPetEvent
    private val _addPetResponse = MutableLiveData<AddPetResponse>()
    val addPetResponse: LiveData<AddPetResponse> = _addPetResponse

    fun addPet(
        //TODO: поправить отправляемые данные
        petName: String,
        petSpecies: String,
        petSex: String,
//        petBreed: String,
        petBirthday: String,
//        petDelicies: String,
//        petToy: String,
//        petBadHabbit: String,
//        petChip: String,
//        isPetForShows: Boolean,
//        hasTitles: Boolean,
//        isSportsPet: Boolean,
//        visitsSaloons: Boolean,
//        petEducation: String
    ) {
        viewModelScope.launch {
            _addPetEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.addPetAsync(
                    userToken = App.getInstance().userToken,
                    petName = petName,
                    petSpecies = petSpecies,
                    petSex = petSex,
                    petBreed = "502",
                    petBirthday = petBirthday,
                    petDelicies = "1",
                    petToy = "222",
                    petBadHabbit = "1",
                    petChip = "111",
                    isPetForShows = true,
                    hasTitles = true,
                    isSportsPet = true,
                    visitsSaloons = true,
                    petEducation = "111"
                ).await()
                if (result.result == "success") {
                    _addPetResponse.value = result
                    _addPetEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _addPetEvent.value = NetworkEvent(State.ERROR, result.error)
                    _addPetResponse.value = null
                }
            } catch (exc: Exception) {
                _addPetEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _addPetResponse.value = null
            }
        }
    }

    private fun loadInterests() {
        viewModelScope.launch {
            _interestsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getInterestsAsync().await()
                if (result.result == "success") {
                    _interestsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                    _interests.value = result.interests.toSortedInterests()
                } else {
                    _interestsLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                    _interests.value = listOf()
                }
            } catch (exc: Exception) {
                _interestsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _interests.value = listOf()
            }
        }
    }

}