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
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.simpleDateFormat
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
    var password: String? = null
    var userCity: String? = null
    //pet registration data
    val petSex = MutableLiveData<Int?>()
    val petBirthday = MutableLiveData<Date>()
    var petSpeciesId: Int = -1
    var voucher: String? = null
    var petName: String? = null
    //interests
    var interestsIds: String? = null

    val species = MutableLiveData<List<Species>?>()
    val interests = MutableLiveData<List<RegInterest>?>()

    val firstStageFinished = MutableLiveData<Boolean>()
    val secondStageFinished = MutableLiveData<Boolean>()
    private val _thirdStageFinished = MutableLiveData<Boolean>()
    val thirdStageFinished: LiveData<Boolean> = _thirdStageFinished

    private val progressMap = mutableMapOf(
        Pair(RegStep.USER, 0),
        Pair(RegStep.PET, 0),
        Pair(RegStep.INTERESTS, 0)
    )

    private val _progress = MutableLiveData<Pair<Int,Int>>()
    val progress: LiveData<Pair<Int,Int>> = _progress

    init {
        petSex.value = SEX_MALE
        petBirthday.value = Date(System.currentTimeMillis())
        interests.value = listOf()
        species.value = listOf()
        setThirdStageFinished()
    }

    fun reset() {
        userSurname = null
        userName = null
        userPatronymic = null
        userPhone = null
        userEmail = null
        userCity = null
        petSex.value = SEX_MALE
        petBirthday.value = Date(System.currentTimeMillis())
        petSpeciesId = -1
        voucher = null
        petName = null
        interestsIds = null
        interests.value?.run {
            for(interest in this) interest.isSelected = false
        }
        setThirdStageFinished()
    }

    private val _step = MutableLiveData<RegStep>()
    val step: LiveData<RegStep> = _step

    fun setProgressMap(step: RegStep, readyParts: Int) {
        progressMap[step] = readyParts
        updateProgress()
    }

    private fun updateProgress() {
        val userProgress = (progressMap[RegStep.USER] ?: 0) * 33 / 9f
        val petProgress = (progressMap[RegStep.PET] ?: 0) * 33 / 2f
        val interestsProgress = (progressMap[RegStep.INTERESTS] ?: 0) * 34
        val total = (userProgress + petProgress + interestsProgress).toInt()
        _progress.value = Pair(_progress.value?.second ?: 0, total)
    }

    fun setStage(step: RegStep) {
        _step.value = step
    }

    fun loadSpecies() {
        viewModelScope.launch {
            try {
                val response = APIService.API.getSpeciesAsync().await()
                if(response.result == "success") species.value = response.species?.toSpecies()
                else species.value = null
            }catch (exc: Exception) {
                species.value = null
            }
        }
    }

    fun loadInterests() {
        viewModelScope.launch {
            try {
                val response = APIService.API.getInterestsAsync().await()
                if(response.result == "success") interests.value = response.interests.toRegistrationInterests()
                else interests.value = null
            }catch (exc: Exception) {
                interests.value = null
            }
        }
    }

    fun setThirdStageFinished() {
        val firstSelected = interests.value?.firstOrNull { it.isSelected }
        _thirdStageFinished.value = firstSelected != null
        progressMap[RegStep.INTERESTS] = if (firstSelected != null) 1 else 0
        updateProgress()
    }

    private val _registrationState = MutableLiveData<NetworkEvent<State>>()
    val registrationState: LiveData<NetworkEvent<State>> = _registrationState

    fun registerUser() {
        _registrationState.value = NetworkEvent(State.LOADING)
        createInterestsIds(interests)
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
                    voucher = voucher ?: "",
                    interests = interestsIds ?: "",
                    petSpecies = petSpeciesId.toString(),
                    petBirthday = simpleDateFormat.format(petBirthday.value ?: Date()),
                    petSex = petSex.value.toString(),
                    password = password ?: ""
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
    fun createInterestsIds(interestsLiveData: LiveData<List<RegInterest>?>? = null) {
        interestsLiveData?.value?.run {
            interestsIds = if(isEmpty()) null
            else {
                val builder = StringBuilder()
                for(interest in this){
                    if (interest.isSelected) {
                        if (builder.isNotEmpty()) builder.append(",")
                        builder.append(interest.interestId)
                    }
                }
                builder.toString()
            }
        } ?: run {
            interestsIds = null
        }
    }

    private fun hasFieldsProblems(): Boolean {
        if(userName.isNullOrBlank()
            || userPhone.isNullOrBlank()
            || userEmail.isNullOrBlank()
            || petName.isNullOrBlank()
            || interestsIds.isNullOrBlank()
            || password.isNullOrBlank()) return true
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