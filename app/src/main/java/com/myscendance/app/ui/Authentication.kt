package com.myscendance.app.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.myscendance.app.R
import com.myscendance.app.databinding.ActivityAuthenticationBinding
import com.myscendance.app.utils.AppUtil
import com.myscendance.app.utils.LoadingUtils

class Authentication : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }

    private val appUtil by lazy{
        AppUtil(this)
    }

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if(appUtil.isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java).putExtra("url_redirect", "https://app.myascendance.com/app_redirect.php?user_id=${appUtil.getUserCredential()}"))
            finish()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)


        initClickListeners()

        initObservers()


    }

    private fun initObservers(){
        viewModel.loginUser.observe(this,{
            showSnackMessage(it)
        })

        viewModel.loginUserId.observe(this , {

            LoadingUtils.hideDialog()

            if(it != -1){
                appUtil.saveUserCredentials(it)
                if(binding.rememberBox.isChecked) appUtil.stayLoggedIn(true)

                startActivity(Intent(applicationContext, MainActivity::class.java).putExtra("url_redirect", "https://app.myascendance.com/app_redirect.php?user_id=$it"))
                finish()

            }

        })

    }

    private fun initClickListeners(){

       binding.apply {

           btnLogin.setOnClickListener {

               LoadingUtils.showDialog(this@Authentication, false)

               viewModel.loginUser(edtUsername.text.toString(), edtPassword.text.toString())



           }


           //click listener for text
           txtRegister.setOnClickListener {
               //code to open url in browser
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://app.myascendance.com/")))
           }

       }

    }

    //show message
    private fun showSnackMessage(message:String){
        Snackbar.make(window.decorView, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        if(appUtil.isDarkMode()){
            binding.imgLogo.setImageResource(R.drawable.logo_dark)
        }else{
            binding.imgLogo.setImageResource(R.drawable.logo_light)
        }
    }



}