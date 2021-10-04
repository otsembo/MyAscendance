package com.myscendance.app.utils

import android.content.Context
import android.widget.Toast

open class LoadingUtils {

    companion object{

        private var loader : AnimatorDialog? = null

        fun showDialog(mCtx:Context?, isCancelable:Boolean){

            //hide any dialog present
            hideDialog()

            if(mCtx != null){

                try{

                    loader = AnimatorDialog(mCtx)
                    loader?.let {
                        it.setCanceledOnTouchOutside(isCancelable)
                        it.setCancelable(isCancelable)
                        it.show()
                    }

                }catch (e: Exception){

                    Toast.makeText(mCtx, "An unexpected error occurred, kindly restart the application.", Toast.LENGTH_SHORT).show()

                }

            }

        }


        fun hideDialog(){

            if (loader != null && loader?.isShowing!!){

                loader = try {

                    loader?.dismiss()
                    null
                }catch (e: Exception){

                    null
                }

            }
        }

    }

}