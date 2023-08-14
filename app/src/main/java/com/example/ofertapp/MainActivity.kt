package com.example.ofertapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ofertapp.adapter.MyDrinkAdapter
import com.example.ofertapp.databinding.ActivityMainBinding
import com.example.ofertapp.databinding.LayoutDrinkItemBinding
import com.example.ofertapp.eventbus.UpdateCartEvent
import com.example.ofertapp.listener.ICartLoadListener
import com.example.ofertapp.listener.IDrinkLoadListener
import com.example.ofertapp.model.CartModel
import com.example.ofertapp.model.DrinkModel
import com.example.ofertapp.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), IDrinkLoadListener, ICartLoadListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var  drinkLoadListener: IDrinkLoadListener
    lateinit var  cartLoadListener: ICartLoadListener

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
    fun onUpdateCartEvent(event:UpdateCartEvent){
        countCartFromFirebase()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        loadDrinkFromFirebase()
        countCartFromFirebase()



    }



    private fun countCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children){
                        val cartModel= cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key= cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadDrinkFromFirebase() {
        val drinkModels : MutableList<DrinkModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Drink")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists())
                    {
                        for(drinkSnapshot in snapshot.children)
                        {
                            val drinkModel=drinkSnapshot.getValue(DrinkModel::class.java)
                            drinkModel!!.key=drinkSnapshot.key
                            drinkModels.add(drinkModel)
                        }
                        drinkLoadListener.onDrinkLoadSuccess(drinkModels)



                    }
                    else
                        drinkLoadListener.onDrinkLoadFailed("Drink items not exists")
                }

                override fun onCancelled(error: DatabaseError) {
                    drinkLoadListener.onDrinkLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        drinkLoadListener=this
        cartLoadListener=this


        binding.btnCart.setOnClickListener{startActivity(Intent(this,CartActivity::class.java))}

    }

    override fun onDrinkLoadSuccess(drinkModelList: List<DrinkModel>?) {
        val adapter= MyDrinkAdapter(this,drinkModelList!!,cartLoadListener)
        binding.recyclerdrink.adapter=adapter
        binding.recyclerdrink2.adapter=adapter
        binding.recyclerdrink3.adapter=adapter
    }



    override fun onDrinkLoadFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum=0
        for(cartModel in cartModelList!!)cartSum+= cartModel!!.quantity
        binding.badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(binding.mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }


}