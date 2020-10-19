package ru.hvost.news.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hvost.news.models.Species
import ru.hvost.news.utils.events.Event

class RegistrationVM : ViewModel() {

    enum class RegStep { USER, PET, INTERESTS }

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

    private val _species = MutableLiveData<List<Species>>()
    val species: LiveData<List<Species>> = _species
    fun loadSpecies() {
        _species.value = Species.getTestList()
    }

}