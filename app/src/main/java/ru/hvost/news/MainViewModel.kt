package ru.hvost.news

import androidx.lifecycle.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.http.GET
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

    private val _prizeCategoriesState = MutableLiveData<NetworkEvent<State>>()
    val prizeCategoriesState: LiveData<NetworkEvent<State>> = _prizeCategoriesState
    private val _prizeCategoriesResponse = MutableLiveData<List<PrizeCategory>>()
    val prizeCategoriesResponse: LiveData<List<PrizeCategory>> = _prizeCategoriesResponse

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

    private val _articleLikedLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    private val _articleIsLiked = MutableLiveData<AddLikedByUserResponse>()

    private val _petToysLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petToysLoadingEvent: LiveData<NetworkEvent<State>> = _petToysLoadingEvent
    private val _petToys = MutableLiveData<List<PetToys>>()
    val petToys: LiveData<List<PetToys>> = _petToys

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

    private val _petSportsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petSportsLoadingEvent: LiveData<NetworkEvent<State>> = _petSportsLoadingEvent
    private val _petSports = MutableLiveData<List<PetSports>>()
    val petSports: LiveData<List<PetSports>> = _petSports

    private val _petBadHabitsLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petBadHabitsLoadingEvent: LiveData<NetworkEvent<State>> = _petBadHabitsLoadingEvent
    private val _petBadHabits = MutableLiveData<List<PetBadHabbits>>()
    val petBadHabits: LiveData<List<PetBadHabbits>> = _petBadHabits

    private val _petFeedLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petFeedLoadingEvent: LiveData<NetworkEvent<State>> = _petFeedLoadingEvent
    private val _petFeed = MutableLiveData<List<PetFeed>>()
    val petFeed: LiveData<List<PetFeed>> = _petFeed

    private val _petDeliciesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petDeliciesLoadingEvent: LiveData<NetworkEvent<State>> = _petDeliciesLoadingEvent
    private val _petDelicies = MutableLiveData<List<PetDelicies>>()
    val petDelicies: LiveData<List<PetDelicies>> = _petDelicies

    private val _notificationsPeriodLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val notificationsPeriodLoadingEvent: LiveData<NetworkEvent<State>> =
        _notificationsPeriodLoadingEvent
    private val _notificationsPeriod = MutableLiveData<List<NotificationPeriod>>()
    val notificationsPeriod: LiveData<List<NotificationPeriod>> = _notificationsPeriod

    private val _petDiseasesLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val petDiseasesLoadingEvent: LiveData<NetworkEvent<State>> = _petDiseasesLoadingEvent
    private val _petDiseases = MutableLiveData<List<PetDiseases>>()
    val petDiseases: LiveData<List<PetDiseases>> = _petDiseases

    private val _bonusBalanceLoadingEvent = MutableLiveData<NetworkEvent<State>>()
    val bonusBalanceLoadingEvent: LiveData<NetworkEvent<State>> = _bonusBalanceLoadingEvent
    private val _bonusBalance = MutableLiveData<BonusBalanceResponse>()
    val bonusBalance: LiveData<BonusBalanceResponse> = _bonusBalance

    //Событие, сообщающее о необходимости закрытия инструкций.
    val closeInstructionsEvent = MutableLiveData<OneTimeEvent>()

    private val _loadingVouchersEvent = MutableLiveData<NetworkEvent<State>>()

    //событие, сообщающее о закрытии ArticlesFilterCustomDialog
    val dismissArticlesFilterCustomDialog = MutableLiveData<OneTimeEvent>()

    //событие, сообщающее об изменении интересов пользователя
    val updateArticlesWithNewInterests = MutableLiveData<OneTimeEvent>()

    //событие, сообщающее об изменении в списке фильтров
    val filterRvChangedEvent = MutableLiveData<OneTimeEvent>()

//    val enableFilterButton = MutableLiveData<Boolean>()

    var feedTabState: Enum<ButtonSelected> = ButtonSelected.FEED

    var prizeDomainId: String? = null

    var inviteInstructionFirstTime: Boolean = true

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

    fun getPetSports(
        petSpecies: String? = null
    ) {
        viewModelScope.launch {
            _petSportsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetSports(petSpecies).await()
                if (response.result == "success") {
                    _petSports.value = response.sports?.toPetSports()
                    _petSportsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petSportsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petSports.value = listOf()
                }
            } catch (exc: Exception) {
                _petSportsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petSports.value = listOf()
            }
        }
    }

    fun getPetBadHabits(
        petSpecies: String? = null
    ) {
        viewModelScope.launch {
            _petBadHabitsLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetBadHabbits(petSpecies).await()
                if (response.result == "success") {
                    _petBadHabits.value = response.badHabbits?.toBadHabbits()
                    _petBadHabitsLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petBadHabitsLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petBadHabits.value = listOf()
                }
            } catch (exc: Exception) {
                _petBadHabitsLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petBadHabits.value = listOf()
            }
        }
    }

    fun getPetFeed() {
        viewModelScope.launch {
            _petFeedLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetFeed().await()
                if (response.result == "success") {
                    _petFeed.value = response.feeds?.toFeed()
                    _petFeedLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petFeedLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petFeed.value = listOf()
                }
            } catch (exc: Exception) {
                _petFeedLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petFeed.value = listOf()
            }
        }
    }

    fun getPetDelicies(
        petSpecies: String? = null
    ) {
        viewModelScope.launch {
            _petDeliciesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetDelicies(petSpecies).await()
                if (response.result == "success") {
                    _petDelicies.value = response.delicies?.toPetDelicies()
                    _petDeliciesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petDeliciesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petDelicies.value = listOf()
                }
            } catch (exc: Exception) {
                _petDeliciesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petDelicies.value = listOf()
            }
        }
    }

    fun getNotificationPeriod() {
        viewModelScope.launch {
            _notificationsPeriodLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getNotificationPeriod().await()
                if (response.result == "success") {
                    _notificationsPeriod.value = response.periods?.toNotificationPeriod()
                    _notificationsPeriodLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _notificationsPeriodLoadingEvent.value =
                        NetworkEvent(State.ERROR, response.error)
                    _notificationsPeriod.value = listOf()
                }
            } catch (exc: Exception) {
                _notificationsPeriodLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _notificationsPeriod.value = listOf()
            }
        }
    }

    fun getPetDiseases() {
        viewModelScope.launch {
            _petDiseasesLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPetDiseases().await()
                if (response.result == "success") {
                    _petDiseases.value = response.diseases?.toPetDiseases()
                    _petDiseasesLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petDiseasesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                    _petDiseases.value = listOf()
                }
            } catch (exc: Exception) {
                _petDiseasesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                _petDiseases.value = listOf()
            }
        }
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
        vacinationPeriodId: String,
        dewormingId: String,
        dewormingDate: String,
        dewormingPeriodId: String,
        exoparasiteId: String,
        exoparasitesDate: String,
        exoparasitePeriodId: String,
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
                        userToken = App.getInstance().userToken,
                        petId = petId,
                        isSterilised = isSterilised,
                        vacineId = vacineId,
                        vacinationDate = vacinationDate,
                        vacinationPeriodId = vacinationPeriodId,
                        dewormingId = dewormingId,
                        dewormingDate = dewormingDate,
                        dewormingPeriodId = dewormingPeriodId,
                        exoparasiteId = exoparasiteId,
                        exoparasitesDate = exoparasitesDate,
                        exoparasitesPeriodId = exoparasitePeriodId,
                        feeding = feedingTypeId,
                        diseases = diseases,
                        favouriteVetName = favouriteVetName,
                        favouriteVetAdress = favouriteVetAdress
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
            _prizeCategoriesState.value = NetworkEvent(State.LOADING)
            try {
                val response =
                    APIService.API.getPrizeCategoriesAsync(App.getInstance().userToken).await()
                if (response.result == "success") {
                    _prizeCategoriesResponse.value = response.categories?.toPrizeCategories()
                    _prizeCategoriesState.value = NetworkEvent(State.SUCCESS)
                } else {
                    _prizeCategoriesState.value = NetworkEvent(State.ERROR, response.error)
                    _prizeCategoriesResponse.value = listOf()
                }
            } catch (exc: Exception) {
                _prizeCategoriesState.value = NetworkEvent(State.FAILURE, exc.toString())
                _prizeCategoriesResponse.value = listOf()
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

    private fun getInviteLink() {
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

    fun sendInviteToMail(
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
                    _prizesLoadingEvent.value = NetworkEvent(State.ERROR, response.error)
                }
            } catch (exc: Exception) {
                _prizes.value = listOf()
                _prizesLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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

    fun loadPetEducation(
        petSpecies: String? = null
    ) {
        viewModelScope.launch {
            _petEducationLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getPetEducationAsync(petSpecies).await()
                if (result.result == "success") {
                    _petEducation.value = result.educations?.toEducation()
                    _petEducationLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petEducation.value = listOf()
                    _petEducationLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _petEducation.value = listOf()
                _petEducationLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

    fun loadPetToys(
        petSpecies: String? = null
    ) {
        viewModelScope.launch {
            _petToysLoadingEvent.value = NetworkEvent(State.LOADING)
            try {
                val result = APIService.API.getPetToysAsync(petSpecies).await()
                if (result.result == "success") {
                    _petToys.value = result.toys?.toToys()
                    _petToysLoadingEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _petToys.value = listOf()
                    _petToysLoadingEvent.value = NetworkEvent(State.ERROR, result.error)
                }
            } catch (exc: Exception) {
                _petToys.value = listOf()
                _petToysLoadingEvent.value = NetworkEvent(State.FAILURE, exc.toString())
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

    fun updatePet(
        petId: String,
        petName: String,
        petSpecies: String,
        petSex: String,
        petBreed: String? = null,
        petBirthday: String,
        petDelicies: String,
        petToy: String,
        petFeed: String,
        petBadHabbit: String,
        petChip: String,
        isPetForShows: Boolean,
        hasTitles: Boolean,
        petSports: String,
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
                    petFeed = petFeed,
                    petBadHabbit = petBadHabbit,
                    petChip = petChip,
                    isPetForShows = isPetForShows,
                    hasTitles = hasTitles,
                    sportsPet = petSports,
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

        public const val UNSELECTED_ID = -1L
        public const val UNSELECTED = "Не выбрано"
        public const val OTHER_ID = -2L
        public const val OTHER_NEW_ID = -3L
        public const val OTHER = "Другое"
        public const val SELECTED_ID = -3L
    }
}