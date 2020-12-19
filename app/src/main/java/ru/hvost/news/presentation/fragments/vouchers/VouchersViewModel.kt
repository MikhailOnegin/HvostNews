package ru.hvost.news.presentation.fragments.vouchers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hvost.news.App
import ru.hvost.news.data.api.APIService
import ru.hvost.news.data.api.response.SimpleResponse
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class VouchersViewModel : ViewModel() {

    private val _isReadyToCheckVoucher = MutableLiveData(false)
    val isReadyToCheckVoucher: LiveData<Boolean> = _isReadyToCheckVoucher
    private val _isVoucherCorrect = MutableLiveData(false)
    val isVoucherCorrect: LiveData<Boolean> = _isVoucherCorrect
    var voucherProgram: String? = null
        private set

    fun setIsReadyToCheckVoucher(isReady: Boolean) {
        _isReadyToCheckVoucher.value = isReady
    }

    private val _checkVoucherEvent = MutableLiveData<NetworkEvent<State>>()
    val checkVoucherEvent: LiveData<NetworkEvent<State>> = _checkVoucherEvent

    fun checkVoucher(code: String?) {
        viewModelScope.launch {
            _checkVoucherEvent.value = NetworkEvent(State.LOADING)
            try {
                val response = APIService.API.getVoucherProgramAsync(code).await()
                if(response.result == "success") {
                    voucherProgram = response.program
                    _checkVoucherEvent.value = NetworkEvent(State.SUCCESS)
                    _isVoucherCorrect.value = true
                } else {
                    _checkVoucherEvent.value = NetworkEvent(State.ERROR, response.error)
                    voucherProgram = null
                    _isVoucherCorrect.value = false
                }
            } catch (exc: Exception) {
                _checkVoucherEvent.value = NetworkEvent(State.FAILURE, exc.toString())
                voucherProgram = null
                _isVoucherCorrect.value = false
            }
        }
    }

    fun reset() {
        _isReadyToCheckVoucher.value = false
        _isVoucherCorrect.value = false
        voucherProgram = null
    }

    private val _registerVoucherEvent = MutableLiveData<NetworkEvent<State>>()
    val registerVoucherEvent: LiveData<NetworkEvent<State>> = _registerVoucherEvent
    var registerVoucherResponse: SimpleResponse? = null

    fun registerVoucher(
        voucherCode: String?,
        petId: String?,
        forceRegister: Boolean
    ) {
        viewModelScope.launch {
            try {
                registerVoucherResponse = APIService.API.registerVoucherAsync(
                    userToken = App.getInstance().userToken,
                    voucherCode = voucherCode,
                    petId = petId,
                    forceRegister = forceRegister
                ).await()
                if(registerVoucherResponse?.result == "success") {
                    _registerVoucherEvent.value = NetworkEvent(State.SUCCESS)
                } else {
                    _registerVoucherEvent.value = NetworkEvent(
                        State.ERROR,
                        registerVoucherResponse?.error
                    )
                }
            } catch (exc: Exception) {
                _registerVoucherEvent.value = NetworkEvent(State.FAILURE, exc.toString())
            }
        }
    }

}