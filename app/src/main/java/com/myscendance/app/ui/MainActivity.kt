package com.myscendance.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.myscendance.app.R
import com.myscendance.app.data.NotificationsManager
import com.myscendance.app.databinding.ActivityMainBinding
import com.myscendance.app.utils.AppUtil
import com.myscendance.app.utils.LoadingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //binding
    private lateinit var binding: ActivityMainBinding

    //intent
    private val myIntent by lazy {
        intent
    }

    //view model
    private val viewModel by lazy{
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    //app util
    private val appUtil by lazy {
        AppUtil(this)
    }

    //file choosing options
    val FILE_INPUT_REQUEST = 1
    var mFilePathCallback:ValueCallback<Array<Uri>>? = null
    var mediaPath: String? = null
    lateinit var urlIntent:Intent
    lateinit var loadingUrl:String
    lateinit var mCameraPhotoPath:String
    val REQUEST_CODE_LOLIPOP = 1
    private val RESULT_CODE_ICE_CREAM = 2
    var mUploadMessage: ValueCallback<Uri>? = null
    //provide error code
    var errorCode = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialize binding
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

    }

    private fun initNetworkAndData(){
        var validConnection: Boolean

        CoroutineScope(Dispatchers.IO).launch {
            validConnection = appUtil.pingNetwork()

            this.launch {
                (Dispatchers.Main){
                    if(!validConnection){
                        LoadingUtils.hideDialog()
                        viewModel.showNetworkNeededDialog(this@MainActivity)
                    }else{
                        initUI()
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        LoadingUtils.showDialog(this, false)
        //check network and data
        initNetworkAndData()
        //permission
        setupPermissions()

    }

    //initialize variables
    private fun  initUI() {
        //show notifications
        NotificationsManager(this)
        if(!pendingNotification()) setUpWebView()
    }

    //set up web view
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(){
        binding.apply {
            with(webView){
                settings.javaScriptEnabled = true
                intent.getStringExtra("url_redirect")?.let { loadUrl(it) }
                webViewClient = AppWebViewClient()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //go back when back pressed
        if(keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun pendingNotification(): Boolean{
        var isNotify = false
        val data = myIntent.getStringExtra("action")
        data?.let {
            isNotify = true
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient = AppWebViewClient()
            binding.webView.webChromeClient = chromeClient
            binding.webView.loadUrl("https://app.myascendance.com/app_redirect.php?action=$it&user_id=${appUtil.getUserCredential()}")
        }
        return isNotify
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }


    // Create an image file
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        @SuppressLint("SimpleDateFormat") val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RESULT_CODE_ICE_CREAM -> {
                var uri: Uri? = null
                if (data != null) {
                    uri = data.data
                }

                mUploadMessage?.onReceiveValue(uri)
                mUploadMessage = null
            }

            REQUEST_CODE_LOLIPOP -> {
                var result: Array<Uri>? = null
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        result = arrayOf<Uri>(data.data!!)
                    }
                }
                mFilePathCallback?.onReceiveValue(result)
                mFilePathCallback = null
            }

        }

        if (requestCode == 2000) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this,"APP Update Failed. Try Again.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private val chromeClient = object: WebChromeClient(){
        lateinit var TAG:String

        fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(
                Intent.createChooser(i, "File Chooser"),
                RESULT_CODE_ICE_CREAM
            )
        }

        // For Android 3.0+
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String?) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                RESULT_CODE_ICE_CREAM
            )
        }

        //For Android 4.1
        fun openFileChooser(
            uploadMsg: ValueCallback<Uri>,
            acceptType: String?,
            capture: String?
        ) {
            mUploadMessage = uploadMsg
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            startActivityForResult(
                Intent.createChooser(i, "File Chooser"),
                RESULT_CODE_ICE_CREAM
            )
        }

        //For Android5.0+
        override fun onShowFileChooser(
            webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (mFilePathCallback != null) {
                mFilePathCallback?.onReceiveValue(null)
            }
            mFilePathCallback = filePathCallback
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"
            val intentArray: Array<Intent> = arrayOf(contentSelectionIntent)
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, REQUEST_CODE_LOLIPOP)
            return true
        }

    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if(permission != PackageManager.PERMISSION_GRANTED)
            makeRequest()

    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2000
        )
    }

    private fun newMailIntent(mCtx: Context, mailAddress: String) : Intent{

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
//        intent.putExtra(Intent.EXTRA_TEXT, mailBody)
//        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject)
//        intent.putExtra(Intent.EXTRA_CC, mailCC)
        intent.type = "message/rfc822"
        return intent

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(
                this@MainActivity,
                "THIS APP CAN NOT WORK WITHOUT Storage Permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    inner class AppWebViewClient: WebViewClient(){

        private var activityRef: WeakReference<Activity>? = WeakReference<Activity>(this@MainActivity)

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            val logoutUrl = "https://app.myascendance.com/logout.php"
            val url = view?.url

            //check if my own page
            if(Uri.parse(view?.url!!).host == "app.myascendance.com"){
                if(view.url.equals(logoutUrl)){
                    appUtil.logOutUser()
                    startActivity(Intent(this@MainActivity, Authentication::class.java))
                    finish()
                }
                return false
            }else{

                if (url!!.startsWith("tel:") || url.startsWith("whatsapp:")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                    return true
                }else if(url.startsWith("mailto:")){
                    val activity:Activity = activityRef?.get()!!
                    val mailTo = MailTo.parse(url)
                    val i = newMailIntent(activity, mailTo.to)
                    activity.startActivity(i)
                    view?.reload()
                    return true
                }else{

                    view.loadUrl(viewModel.web_url)

                }
                }
               return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            //show dialog
            LoadingUtils.showDialog(this@MainActivity, false)
        }


        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            //hide dialog
            LoadingUtils.hideDialog()
        }

    }

}