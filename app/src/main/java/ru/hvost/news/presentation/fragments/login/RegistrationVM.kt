package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.RegistrationInterest
import ru.hvost.news.models.Species
import ru.hvost.news.models.toRegistrationInterests
import ru.hvost.news.models.toSpecies
import ru.hvost.news.utils.events.Event
import java.lang.Exception
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
    var petSpeciesId: Int = 0
    var couponeCode: String? = null //sergeev Обязательно ли наличие ваучера при регистрации?
    var petName: String? = null

    init {
        petSex.value = SEX_MALE
        petBirthday.value = Date(System.currentTimeMillis())
        loadSpecies()
        loadInterests()
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

    private val _interests = MutableLiveData<List<RegistrationInterest>>()
    private val interests: LiveData<List<RegistrationInterest>> = _interests

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

    companion object {
        const val SEX_MALE = 8
        const val SEX_FEMALE = 9
        const val SEX_UNKNOWN = 0
    }

}