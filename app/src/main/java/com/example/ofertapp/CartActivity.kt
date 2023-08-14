package com.example.ofertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ofertapp.adapter.MyCartAdapter
import com.example.ofertapp.databinding.ActivityCartBinding
import com.example.ofertapp.databinding.ActivityMainBinding
import com.example.ofertapp.databinding.ActivityPasarelaBinding
import com.example.ofertapp.eventbus.UpdateCartEvent
import com.example.ofertapp.listener.ICartLoadListener
import com.example.ofertapp.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity(), ICartLoadListener {

    private lateinit var binding : ActivityCartBinding
    var cartLoadListener:ICartLoadListener? = null




    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)


    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent){
        loadCartFromFirebase()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        loadCartFromFirebase()


    }



    private fun loadCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children){
                        val cartModel= cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key= cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    private fun init(){
        cartLoadListener=this

        val layoutManager= LinearLayoutManager(this)
        binding.recyclercart!!.layoutManager=layoutManager
        binding.recyclercart!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        binding.btnBack!!.setOnClickListener { finish() }

        binding.buttonPagar.setOnClickListener{startActivity(Intent(this,PasarelaActivity::class.java))}

    }



    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum = 0.0
        for(cartModel in cartModelList!!){
            sum+=cartModel!!.totalPrice
        }
        binding.txtTotal.text=StringBuilder("S/").append(sum)
        val adapter=MyCartAdapter(this,cartModelList)
        binding.recyclercart!!.adapter=adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }
}