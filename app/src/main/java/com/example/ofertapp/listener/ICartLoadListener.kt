package com.example.ofertapp.listener

import android.os.Message
import com.example.ofertapp.model.CartModel

interface ICartLoadListener {

    fun onLoadCartSuccess(cartModelList: List<CartModel>)
    fun onLoadCartFailed(message:String?)

}