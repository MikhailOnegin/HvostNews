package ru.hvost.news.presentation.fragments.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.RegInterest
import ru.hvost.news.models.Species
import ru.hvost.news.models.toRegistrationInterests
import ru.hvost.news.models.toSpecies
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.Event
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.petBirthdayDateFormat
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class RegistrationVM : ViewModel() {

    enum class RegStep { USER, PET, INTERESTS }

    //user registration data
    var userSurname: String? = null
    var userName: String? = null
    var userPatronymic: String? = null
    var userPhone: String? = null
    var userEmail: String? = null
    var userCity: String? = null
    //pet registration data
    val petSex = MutableLiveData<Int>()
    val petBirthday = MutableLiveData<Date>()
    var petSpeciesId: Int = -1
    var voucher: String? = null //sergeev: Обязательно ли наличие ваучера при регистрации?
    var petName: String? = null
    //interests
    var interestsIds: String? = null

    private val _interests = MutableLiveData<List<RegInterest>>()
    val interests: LiveData<List<RegInterest>> = _interests

    private val _thirdStageFinished = MutableLiveData<Boolean>()
    val thirdStageFinished: LiveData<Boolean> = _thirdStageFinished

    init {
        petSex.value = SEX_MALE
        petBirthday.value = Date(System.currentTimeMillis())
        loadSpecies()
        loadInterests()
        setThirdStageFinished()
    }

    private val _stage = MutableLiveData<Event<Int>>()
    val stage: LiveData<Event<Int>> = _stage

    private val _step = MutableLiveData<RegStep>()
    val step: LiveData<RegStep> = _step

    fun setStage(step: RegStep) {
        _step.value = step
        when(step) {
            RegStep.USER -> _stage.value = Event(33)
            RegStep.PET -> _stage.value = Event(66)
            RegStep.INTERESTS -> _stage.value = Event(100)
        }
    }

    private val _species = MutableLiveData<List<Species>>().apply { value = null }
    val species: LiveData<List<Species>> = _species

    fun loadSpecies() {
        viewModelScope.launch {
            try {
                val response = APIService.API.getSpeciesAsync().await()
                if(response.result == "success") _species.value = response.species?.toSpecies()
                else _species.value = null
            }catch (exc: Exception) {
                _species.value = null
            }
        }
    }

    private fun loadInterests() {
        viewModelScope.launch {
            try {
                val response = APIService.API.getInterestsAsync().await()
                if(response.result == "success") _interests.value = response.interests.toRegistrationInterests()
                else _interests.value = null
            }catch (exc: Exception) {
                _interests.value = null
            }
        }
    }

    fun setThirdStageFinished() {
        _thirdStageFinished.value = interests.value?.firstOrNull { it.isSelected } != null
    }

    private val _registrationState = MutableLiveData<NetworkEvent<State>>()
    val registrationState: LiveData<NetworkEvent<State>> = _registrationState

    fun registerUser() {
        _registrationState.value = NetworkEvent(State.LOADING)
        createInterestsIds(_interests)
        if(hasFieldsProblems()) {
            _registrationState.value = NetworkEvent(
                State.ERROR,
                App.getInstance().getString(R.string.regErrorHasFieldsProblems)
            )
            return
        }
        viewModelScope.launch {
            try {
                val result = APIService.API.registerUserAsync(
                    name = userName ?: "",
                    surname = userSurname ?: "",
                    patronymic = userPatronymic ?: "",
                    email = userEmail ?: "",
                    phone = userPhone ?: "",
                    city = userCity ?: "",
                    petName = petName ?: "",
                    //voucher = voucher ?: "", //sergeev: Купоны не работают при регистрации.
                    interests = interestsIds ?: "",
                    petSpecies = petSpeciesId.toString(),
                    petBirthday = petBirthdayDateFormat.format(petBirthday.value ?: Date()),
                    petSex = petSex.value.toString()
                ).await()
                if(result.result == "success") _registrationState.value = NetworkEvent(State.SUCCESS)
                else _registrationState.value = NetworkEvent(State.ERROR, result.error)
            }catch (exc: Exception) {
                _registrationState.value = NetworkEvent(
                    State.FAILURE,
                    exc.message
                )
            }
        }

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun createInterestsIds(interestsLiveData: LiveData<List<RegInterest>>? = null) {
        interestsLiveData?.value?.run {
            interestsIds = if(isEmpty()) null
            else {
                val builder = StringBuilder()
                for((index, interest) in this.withIndex()){
                    builder.append(interest.interestId)
                    if(index != size-1) builder.append(",")
                }
                builder.toString()
            }
        } ?: run {
            interestsIds = null
        }
    }

    private fun hasFieldsProblems(): Boolean {
        if(userName.isNullOrBlank()
            || userSurname.isNullOrBlank()
            || userPatronymic.isNullOrBlank()
            || userPhone.isNullOrBlank()
            || userEmail.isNullOrBlank()
            || userCity.isNullOrBlank()
            || voucher.isNullOrBlank()
            || petName.isNullOrBlank()
            || interestsIds.isNullOrBlank()) return true
        if(petSex.value == null || petBirthday.value == null) return true
        if(petSpeciesId <= 0) return true
        return false
    }

    companion object {
        const val SEX_MALE = 8
        const val SEX_FEMALE = 9
        const val SEX_UNKNOWN = 0
    }

}