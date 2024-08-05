package com.dhruva.userauthentication.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhruva.userauthentication.repository.AuthRepository
import java.security.InvalidParameterException

class LoginActivityViewModelFactory(
    val authRepository: AuthRepository,
    val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginActivityViewModel::class.java)) {
            return LoginActivityViewModel(authRepository, application) as T
        }

        throw InvalidParameterException("Unable to construct LoginActivityViewModel")
    }
}