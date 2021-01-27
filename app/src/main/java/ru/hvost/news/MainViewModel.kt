package ru.hvost.news

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.http.Query
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.*
import ru.hvost.news.models.*
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.events.OneTimeEvent

class MainViewModel : ViewModel() {

    val petDeleteState = MutableLiveData<State>()
    val petDeleteResponse = MutableLiveData<DeletePetResponse>()

    val prizeCategoriesState = MutableLiveData<State>()
    val prizeCategoriesResponse = MutableLiveData<List<PrizeCategory>>()

    var categories: List<Categories>? = null
    var domains: List<Domain>? = null

    private val _inviteLinkLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val inviteLinkLoadingEvent: LiveData<NetworkEvent<State>> = _inviteLinkLoadingEvent
    private val _inviteLink = MutableLiveData<InviteLinkResponse>()
    val inviteLink: LiveData<InviteLinkResponse> = _inviteLink

    private val _sendInviteLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val sendInviteLoadingEvent: LiveData<NetworkEvent<State>> = _sendInviteLoadingEvent
    private val _sendInviteResponse = MutableLiveData<SendToEmailResponse>()
    val sendInviteResponse: LiveData<SendToEmailResponse> = _sendInviteResponse

    private val _prizesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val prizesLoadingEvent: LiveData<NetworkEvent<State>> = _prizesLoadingEvent
    private val _prizes = MutableLiveData<List<Prize>>()
    val prizes: LiveData<List<Prize>> = _prizes

    private val _vaccinesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val vaccinesLoadingEvent: LiveData<NetworkEvent<State>> = _vaccinesLoadingEvent
    private val _vaccines = MutableLiveData<List<Preps>>()
    val vaccines: LiveData<List<Preps>> = _vaccines

    private val _dewormingLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val dewormingLoadingEvent: LiveData<NetworkEvent<State>> = _dewormingLoadingEvent
    private val _deworming = MutableLiveData<List<Preps>>()
    val deworming: LiveData<List<Preps>> = _deworming

    private val _exoparazitesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val exoparazitesLoadingEvent: LiveData<NetworkEvent<State>> = _exoparazitesLoadingEvent
    private val _exoparazites = MutableLiveData<List<Preps>>()
    val exoparazites: LiveData<List<Preps>> = _exoparazites

    private val _petFoodLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petFoodLoadingEvent: LiveData<NetworkEvent<State>> = _petFoodLoadingEvent
    private val _petFood = MutableLiveData<List<PetFood>>()
    val petFood: LiveData<List<PetFood>> = _petFood

    private val _changeCartLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val changeCartLoadingEvent: LiveData<NetworkEvent<State>> = _changeCartLoadingEvent
    private val _cartChange = MutableLiveData<PrizeToCartResponse>()
    val cartChange: LiveData<PrizeToCartResponse> = _cartChange

    private val _interestsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val interestsLoadingEvent: LiveData<NetworkEvent<State>> = _interestsLoadingEvent
    private val _interests = MutableLiveData<List<CategoryItem>>()
    val interests: LiveData<List<CategoryItem>> = _interests

    private val _articlesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val articlesLoadingEvent: LiveData<NetworkEvent<State>> = _articlesLoadingEvent
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles
    val likedArticleList = MutableLiveData<List<Article>>()
    private val _allArticlesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val allArticlesLoadingEvent: LiveData<NetworkEvent<State>> = _allArticlesLoadingEvent
    private val _allArticles = MutableLiveData<List<Article>>()
    val allArticles: LiveData<List<Article>> = _allArticles
    val likedAllArticles = MutableLiveData<List<Article>>()
    val updateAllArticlesViewsCount = MutableLiveData<OneTimeEvent>()
    val updateArticlesViewsCount = MutableLiveData<OneTimeEvent>()

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

    private val _updatePetPassportLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val updatePetPassportLoadingEvent: LiveData<NetworkEvent<State>> =
        _updatePetPassportLoadingEvent

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
    val dismissArticlesFilterCustomDialog = MutableLiveData<OneTimeEvent>()

    //событие, сообщающее об изменении интересов пользователя
    val updateArticlesWithNewInterests = MutableLiveData<OneTimeEvent>()

//    val enableFilterButton = MutableLiveData<Boolean>()

    var feedTabState: Enum<ButtonSelected> = ButtonSelected.FEED

    var prizeDomainId: String? = null

    init {
        initializeData()
    }

    fun initializeData() {
        loadArticles()
        loadAllArticles()
        loadUserData()
        getBonusBalance()
        getInviteLink()
        getPrizeCategories()
        updateVouchers(App.getInstance().userToken)
        loadInterests()
        loadSpecies()
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
                    _bonusBalanceLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _bonusBalanceLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun updatePetPassport(
        petId: String,
        isSterilised: Boolean,
        vacineId: String,
        vacinationDate: String,
        dewormingId: String,
        dewormingDate: String,
        exoparasiteId: String,
        exoparasitesDate: String,
        feedingTypeId: String,
        diseases: String,
        favouriteVetName: String,
        favouriteVetAdress: String
    ) {
        viewModelScope.launch {
            _updatePetPassportLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.updatePetPassportAsync(
                        App.getInstance().userToken,
                        petId,
                        isSterilised,
                        vacineId,
                        vacinationDate,
                        dewormingId,
                        dewormingDate,
                        exoparasiteId,
                        exoparasitesDate,
                        feedingTypeId,
                        diseases,
                        favouriteVetName,
                        favouriteVetAdress
                    ).await()
                if (response.result == "success") {
                    _updatePetPassportLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _updatePetPassportLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _updatePetPassportLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun getPetFood() {
        viewModelScope.launch {
            _petFoodLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetFoodAsync().await()
                if (response.result == "success") {
                    _petFood.value = response.petFood.toFood()
                    _petFoodLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petFoodLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petFood.value = listOf()
                }
            } catch (exc: Exception) {
                _petFoodLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petFood.value = listOf()
            }
        }
    }

    fun getVaccines() {
        viewModelScope.launch {
            _vaccinesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPreparationsAsync(VACCINE_ID).await()
                if (response.result == "success") {
                    _vaccines.value = response.preparations.toPreps()
                    _vaccinesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _vaccinesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _vaccines.value = listOf()
                }
            } catch (exc: Exception) {
                _vaccinesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _vaccines.value = listOf()
            }
        }
    }

    fun getDeworming() {
        viewModelScope.launch {
            _dewormingLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPreparationsAsync(DEWORMING_ID).await()
                if (response.result == "success") {
                    _deworming.value = response.preparations.toPreps()
                    _dewormingLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _dewormingLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _deworming.value = listOf()
                }
            } catch (exc: Exception) {
                _dewormingLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _deworming.value = listOf()
            }
        }
    }

    fun getExoparazites() {
        viewModelScope.launch {
            _exoparazitesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPreparationsAsync(EXOPARAZITES_ID).await()
                if (response.result == "success") {
                    _exoparazites.value = response.preparations.toPreps()
                    _exoparazitesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _exoparazitesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _exoparazites.value = listOf()
                }
            } catch (exc: Exception) {
                _exoparazitesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _exoparazites.value = listOf()
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
            _changeCartLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.addPrizeToCartAsync(App.getInstance().userToken, id).await()
                if (response.result == "success") {
                    _cartChange.value = response
                    _changeCartLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _changeCartLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _changeCartLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun getInviteLink() {
        viewModelScope.launch {
            _inviteLinkLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getInviteLinkAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _inviteLink.value = response
                    _inviteLinkLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else _inviteLinkLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _inviteLinkLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun SendInviteToMail(
        email: String?
    ) {
        viewModelScope.launch {
            _sendInviteLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.sendInviteToEmailAsync(App.getInstance().userToken, email)
                        .await()
                if (response.result == "success") {
                    _sendInviteResponse.value = response
                    _sendInviteLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else _sendInviteLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
            } catch (exc: Exception) {
                _sendInviteLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun loadArticles() {
        viewModelScope.launch {
            _articlesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getArticlesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _articles.value = response.articles?.toArticles()
                    _articlesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _articlesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _articles.value = listOf()
                }
            } catch (exc: Exception) {
                _articlesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _articles.value = listOf()
            }
        }
    }

    fun loadPrizes(
        prizeCategoryId: String
    ) {
        viewModelScope.launch {
            _prizesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPrizesAsync(App.getInstance().userToken, prizeCategoryId)
                        .await()
                if (response.result == "success") {
                    _prizes.value = response.prizes?.toPrize()
                    _prizesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _prizes.value = listOf()
                    _prizesLoadingEvent.value = NetworkEvent(State.ERROR)
                }
            } catch (exc: Exception) {
                _prizes.value = listOf()
                _prizesLoadingEvent.value = NetworkEvent(State.FAILURE)
            }
        }
    }

    private fun loadAllArticles() {
        viewModelScope.launch {
            _allArticlesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getArticlesAsync().await()
                if (response.result == "success") {
                    _allArticles.value = response.articles?.toArticles()
                    categories = allArticles.value?.toCategory()
                    domains = allArticles.value?.toOfflineLessons()
                    _allArticlesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _allArticlesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _allArticles.value = listOf()
                    categories = listOf()
                    domains = listOf()
                }
            } catch (exc: Exception) {
                _allArticlesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _allArticles.value = listOf()
                categories = listOf()
                domains = listOf()
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
                    _userDataLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _userDataLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
                    _userPetsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _userPets.value = listOf()
                }
            } catch (exc: Exception) {
                _userPetsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
                    _petsSpeciesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petsSpecies.value = listOf()
                }
            } catch (exc: Exception) {
                _petsSpeciesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
                    _petsBreedsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petsBreeds.value = listOf()
                }
            } catch (exc: Exception) {
                _petsBreedsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
        interests: String? = null,
        mailNotifications: Boolean? = null,
        sendPushes: Boolean? = null
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
                    interests = interests,
                    mailNotifications = mailNotifications,
                    sendPushes = sendPushes
                ).await()
                if (response.result == "success") {
                    _changeUserDataLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _changeUserDataLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _changeUserDataLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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
        Transformations.map(orders) { list -> list.count { it.orderStatus == "DT" || it.orderStatus == "P" } }
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

    private val _deleteOrderEvent = MutableLiveData<NetworkEvent<State>>()
    val deleteOrderEvent: LiveData<NetworkEvent<State>> = _deleteOrderEvent

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            _deleteOrderEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.deleteOrderAsync(
                    userToken = App.getInstance().userToken,
                    orderId = orderId
                ).await()
                if (response.result == "success") {
                    _deleteOrderEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _deleteOrderEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _deleteOrderEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun setArticleViewed(
        articleId: String?
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
        articleId: String,
        liked: Boolean
    ) {
        viewModelScope.launch {
            _articleLikedLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.setArticleLikedByUserAsync(
                    App.getInstance().userToken,
                    articleId,
                    liked
                ).await()
                if (result.result == "success") {
                    _articleIsLiked.value = result
                    _articleLikedLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _articleLikedLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _articleLikedLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun showOrder(order: Order) {
        _orderSelectedEvent.value = Event(order)
    }

    val loadingVouchersEvent: LiveData<NetworkEvent<State>> = _loadingVouchersEvent
    private val _vouchers = MutableLiveData<List<VoucherItem>>()
    val vouchers: LiveData<List<VoucherItem>> = _vouchers
    var vouchersCount: Int? = null
    val vouchersFooterClickEvent = MutableLiveData<OneTimeEvent>()
    val vouchersFilter = MutableLiveData<String>()

    fun updateVouchers(userToken: String?) {
        viewModelScope.launch {
            _loadingVouchersEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getVouchersAsync(userToken).await()
                if (result.result == "success") {
                    _vouchers.value = result.toVouchers()
                    vouchersCount = result.vouchers?.size
                    _loadingVouchersEvent.value = NetworkEvent(State.SUCCESS)
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
        petName: String,
        petSpecies: String,
        petSex: String?,
        petBirthday: String,
    ) {
        viewModelScope.launch {
            _addPetEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.addPetAsync(
                    userToken = App.getInstance().userToken,
                    petName = petName,
                    petSpecies = petSpecies,
                    petSex = petSex,
                    petBirthday = petBirthday,
                ).await()
                if (result.result == "success") {
                    _addPetResponse.value = result
                    _addPetEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _addPetEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _addPetEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _petPassportLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petPassportLoadingEvent: LiveData<NetworkEvent<State>> = _petPassportLoadingEvent
    private val _petPassportResponse = MutableLiveData<PetPassportResponse>()
    val petPassportResponse: LiveData<PetPassportResponse> = _petPassportResponse

    fun getPetPassport(petId: String) {
        viewModelScope.launch {
            _petPassportLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getPetPassportAsync(
                    userToken = App.getInstance().userToken,
                    petId = petId
                ).await()
                if (result.result == "success") {
                    _petPassportResponse.value = result
                    _petPassportLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petPassportLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _petPassportLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    private val _updatePetLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val updatePetLoadingEvent: LiveData<NetworkEvent<State>> = _updatePetLoadingEvent
    private val _updatePetResponse = MutableLiveData<UpdatePetResponse>()
    val updatePetResponse: LiveData<UpdatePetResponse> = _updatePetResponse

    fun updatePet(
        petId: String,
        petName: String,
        petSpecies: String,
        petSex: String,
        petBreed: String? = null,
        petBirthday: String,
        petDelicies: String,
        petToy: String,
        petBadHabbit: String,
        petChip: String,
        isPetForShows: Boolean,
        hasTitles: Boolean,
        isSportsPet: Boolean,
        visitsSaloons: Boolean,
        petEducation: String
    ) {
        viewModelScope.launch {
            _updatePetLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.updatePetAsync(
                    userToken = App.getInstance().userToken,
                    petId = petId,
                    petName = petName,
                    petSpecies = petSpecies,
                    petSex = petSex,
                    petBreed = petBreed ?: "",
                    petBirthday = petBirthday,
                    petDelicies = petDelicies,
                    petToy = petToy,
                    petBadHabbit = petBadHabbit,
                    petChip = petChip,
                    isPetForShows = isPetForShows,
                    hasTitles = hasTitles,
                    isSportsPet = isSportsPet,
                    visitsSaloons = visitsSaloons,
                    petEducation = petEducation
                ).await()
                if (result.result == "success") {
                    _updatePetResponse.value = result
                    _updatePetLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _updatePetLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _updatePetLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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

    companion object {
        enum class ButtonSelected { FEED, DOMAINS, NEWS }

        private const val VACCINE_ID = "1"
        private const val DEWORMING_ID = "2"
        private const val EXOPARAZITES_ID = "3"
    }
}