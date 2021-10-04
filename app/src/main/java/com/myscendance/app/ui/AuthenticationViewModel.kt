package com.myscendance.app.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.myscendance.app.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {

    private val _loginUser = MutableLiveData<String>()
    val loginUser:LiveData<String>
        get() = _loginUser


    private val _loginUserId = MutableLiveData<Int>()
    val loginUserId:LiveData<Int>
        get() = _loginUserId


    //validate input
    private fun validateInput(email:String, password:String) : Boolean{
        return !(email.isEmpty() || password.isEmpty())
    }

    //log in user
    fun loginUser(email: String, password: String){

        if(validateInput(email, password)){
            var id = -1
            //fetch id
            CoroutineScope(Dispatchers.IO).launch {
                val authData = ApiClient.apiService.loginUser(email, password)
                if(authData.isSuccessful){

                    val authBody = authData.body()
                    id = authBody?.user_id!!

                    withContext(Dispatchers.Main){
                        _loginUser.value = authBody.message
                        _loginUserId.value = id
                    }

                }else{
                    _loginUserId.value = -1
                }
            }

        }else{
            _loginUserId.value = -1
        }
    }







}