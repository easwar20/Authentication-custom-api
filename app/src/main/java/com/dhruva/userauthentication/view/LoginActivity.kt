package com.dhruva.userauthentication.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.dhruva.userauthentication.R
import com.dhruva.userauthentication.data.LoginBody
import com.dhruva.userauthentication.databinding.ActivityLoginBinding
import com.dhruva.userauthentication.repository.AuthRepository
import com.dhruva.userauthentication.utils.APIService
import com.dhruva.userauthentication.utils.VibrateView
import com.dhruva.userauthentication.view_model.LoginActivityViewModel
import com.dhruva.userauthentication.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        mBinding.loginWithGoogleBtn.setOnClickListener(this)
        mBinding.loginBtn.setOnClickListener(this)
        mBinding.toRegister.setOnClickListener(this)
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.passwordEt.setOnKeyListener(this)
        mViewModel = ViewModelProvider(
            this,
            LoginActivityViewModelFactory(AuthRepository(APIService.getService()), application)
        ).get(LoginActivityViewModel::class.java)
        setContentView(mBinding.root)
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this) {
            val fromErrorKeys = arrayOf("fullName", "email", "password")
            val message = StringBuilder()
            it.map { entry ->
                if (fromErrorKeys.contains(entry.key)) {
                    when (entry.key) {
                        "email" -> {
                            mBinding.emailTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }

                        "password" -> {
                            mBinding.passwordTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                    }
                } else {
                    message.append(entry.value).append("\n")
                }

                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                        .show()
                }
            }
        }
        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun validateEmail(
        shouldUpdateValue: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.emailEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email ID is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email ID is invalid"
        }

        if (errorMessage != null && shouldUpdateValue) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(
        shouldUpdateValue: Boolean = true,
        shouldVibrateView: Boolean = true
    ): Boolean {
        var errorMessage: String? = null
        val value: String = mBinding.passwordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) {
            errorMessage = "Password must be longer than 6 characters"
        }

        if (errorMessage != null && shouldUpdateValue) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity, this)
            }
        }

        return errorMessage == null
    }

    private fun validate(): Boolean {
        var isValid = true

        if (!validateEmail(false)) isValid = false
        if (!validatePassword(false)) isValid = false

        if (!isValid) VibrateView.vibrate(this, mBinding.cardView)
        return isValid
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.loginBtn -> {
                    submitForm()
                }

                R.id.toRegister -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.emailEt -> {
                    if (hasFocus) {
                        if (mBinding.emailTil.isErrorEnabled) {
                            mBinding.emailTil.isErrorEnabled = false
                        }
                    } else {
                        validateEmail()
                    }
                }

                R.id.passwordEt -> {
                    if (hasFocus) {
                        if (mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }
            }
        }
    }

    private fun submitForm() {
        if (validate()) {
            mViewModel.loginUser(
                LoginBody(
                    mBinding.emailEt.text.toString(),
                    mBinding.passwordEt.text.toString()
                )
            )
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_ENTER == keyCode && keyEvent!!.action == KeyEvent.ACTION_UP)
            submitForm()
        return false
    }
}