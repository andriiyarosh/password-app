package com.example.slickypasscode.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slickypasscode.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private var password: String? = null

    private val isValidationStarted = AtomicBoolean(false)

    private val _digits = MutableLiveData<ArrayList<Int>>()
    val digits: LiveData<ArrayList<Int>> =_digits

    private val _validationResult = MutableLiveData<ValidationState>()
    val validationResult: LiveData<ValidationState> = _validationResult

    private val currentPassword = LinkedList<Int>()

    private val _currentPasswordLD = MutableLiveData<Int>()
    val currentPasswordLD: LiveData<Int> = _currentPasswordLD

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun addInput(input: Int) {
        if(!isValidationStarted.compareAndSet(false, true))
            return

        currentPassword.add(input)
        if (currentPassword.size == 4) validatePassword()
        else isValidationStarted.set(false)

        _currentPasswordLD.value = currentPassword.size
    }

    fun deleteInput() {
        if (isValidationStarted.get() || currentPassword.size < 1) return

        currentPassword.removeLast()
        _currentPasswordLD.value = currentPassword.size
    }

    fun cleanInput() {
        if (isValidationStarted.get() || currentPassword.size < 1) return

        currentPassword.clear()
        _currentPasswordLD.value = currentPassword.size
    }

    fun fetchPasswordData() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            when(val result = mainRepository.getPasscodeData()) {
                is MainRepository.PasscodeRequestResult.SuccessRequest ->
                    insertPasswordData(result.password, result.digits)
                MainRepository.PasscodeRequestResult.FailureRequest ->
                    Log.e(MainViewModel::class.simpleName, "Failed to fetch password data")
            }
        }
    }


    private fun insertPasswordData(passcode: String, passcodeDigits: ArrayList<Int>) {
        password = passcode
        _digits.postValue(passcodeDigits)
    }

    private fun validatePassword() {
        _validationResult.value = ValidationState.STARTED
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)

            val correctPassword = password ?: return@launch
            val resultString = StringBuilder()
            currentPassword.onEach {
                resultString.append(it.toString())
            }
            resultString.toString()

           val isValid = resultString.toString().equals(correctPassword, true)

            _validationResult.postValue(
                if (isValid) ValidationState.VALID
                else ValidationState.INVALID
            )
            currentPassword.clear()
            _currentPasswordLD.postValue(currentPassword.size)
            isValidationStarted.set(false)

            if (isValid) fetchPasswordData()
        }
    }

    enum class ValidationState {
        STARTED, VALID, INVALID
    }
}