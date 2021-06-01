package com.example.slickypasscode.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slickypasscode.network.ServerInterface
import com.example.slickypasscode.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private var password: String? = null

    private val _digits = MutableLiveData<LinkedList<Int>>()
    val digits: LiveData<LinkedList<Int>> =_digits

    val currentPassword: List<Int> = mutableListOf()

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun fetchPasswordData() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            when(val result = mainRepository.getPasscodeData()) {
                is MainRepository.PasscodeRequestResult.SuccessRequest ->
                    insertPasswordData(result.password, result.digits)
                MainRepository.PasscodeRequestResult.FailureRequest -> {

                }
            }
        }
    }

    fun

    private fun insertPasswordData(passcode: String, passcodeDigits: LinkedList<Int>) {
        password = passcode
        _digits.postValue(passcodeDigits)
    }
}