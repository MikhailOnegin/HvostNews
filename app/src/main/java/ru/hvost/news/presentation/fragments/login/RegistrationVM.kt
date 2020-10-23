package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.data.api.APIService
import ru.hvost.news.models.Species
import ru.hvost.news.models.toSpecies
import ru.hvost.news.utils.events.Event
import java.lang.Exception
import java.util.*

class RegistrationVM : ViewModel() {

    enum class RegStep { USER, PET, INTERESTS }

    //registration data
    val petSex = MutableLiveData<Int>()
    val petBirthday = MutableLiveData<Date>()
    //registration data

    init {
        petSex.value = SEX_MALE
        petBirthday.value = Date(System.currentTimeMillis())
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

    companion object {
        const val SEX_MALE = 8
        const val SEX_FEMALE = 9
        const val SEX_UNKNOWN = 0
    }

}