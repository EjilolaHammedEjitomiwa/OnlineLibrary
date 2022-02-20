package com.example.digitalbooks.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo

import com.example.digitalbooks.adaper.CartAdapter
import com.example.digitalbooks.adaper.DownloadedBookAdapter
import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.CartModel
import com.example.digitalbooks.model.DownloadedBookModel
import com.teamapt.monnify.sdk.Monnify
import com.teamapt.monnify.sdk.MonnifyTransactionResponse
import com.teamapt.monnify.sdk.Status
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.model.PaymentMethod
import com.teamapt.monnify.sdk.service.ApplicationMode
import es.dmoral.toasty.Toasty
import example.digitalbooks.R
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.downloading_dialogue.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.math.BigDecimal

class CartActivity : AppCompatActivity() {
    private var cartList = ArrayList<CartModel>()
    private var cartAdapter: CartAdapter? = null

    var monnify = Monnify.instance

    var INITIATE_PAYMENT_REQUEST_CODE = 20
    var KEY_RESULT = "card_pay"

    var totalOrderPrice =  0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartAdapter = CartAdapter(this, cartList)
        cart_recyclerview.setHasFixedSize(true)
        val cartManager = LinearLayoutManager(this)
        cart_recyclerview.layoutManager = cartManager
        cart_recyclerview.adapter = cartAdapter

        getCarts()

        cart_payNow.setOnClickListener {
            Utils.showLoader(this,"Processing Payment...")
            pay()
        }
    }

    private fun pay() {
        monnify.setApiKey(Constants.monnnifyApiKey)
        monnify.setContractCode(Constants.monnifyContractCode)
        monnify.setApplicationMode(ApplicationMode.TEST)

        val transaction = TransactionDetails.Builder()
            .amount(BigDecimal(totalOrderPrice))
            .currencyCode("NGN")
            .customerName("test name")
            .customerEmail("testemail@gmail.com")
            .paymentReference(System.currentTimeMillis().toString())
            .paymentDescription("${title} purchase")
            .paymentMethods(arrayListOf(
                PaymentMethod.CARD
            ))
            .build()

        monnify.initializePayment(
            this,
            transaction,
            INITIATE_PAYMENT_REQUEST_CODE,
            KEY_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utils.dismissLoader()
        val monnifyTransactionResponse = data?.getParcelableExtra<MonnifyTransactionResponse>(KEY_RESULT) as MonnifyTransactionResponse

        var message = ""
        message = when(monnifyTransactionResponse.status) {
            Status.PENDING -> "Pending"
            Status.PAID -> "Successful"
            Status.OVERPAID -> "Successful"
            Status.PARTIALLY_PAID -> "Successful"
            Status.FAILED -> "Transaction Failed"
            Status.PAYMENT_GATEWAY_ERROR -> "Payment gateway error"
            else -> "null"
        }
        when(message){
            "Successful" ->{
                Toasty.success(this,"Payment Successful, Downloading book.....",Toasty.LENGTH_LONG).show()
                val mDialogueView = LayoutInflater.from(this).inflate(R.layout.downloading_dialogue, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogueView)
                val mAlertDualogue = mBuilder.show()
                mAlertDualogue.downloading_title.text = title
                Handler(Looper.getMainLooper()).postDelayed({
                    mAlertDualogue.downloading_downloadCompleteButton.visibility = View.VISIBLE
                    mAlertDualogue.downloading_downloadedBooksRecyclerView.visibility = View.VISIBLE
                    mAlertDualogue.downloading_gif.visibility = View.GONE
                }, 8000)
                // delete cart
                Utils.database()
                    .collection(Constants.carts)
                    .document(Utils.currentUserID())
                    .collection(Utils.currentUserID())
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty){
                            for (data in it.documents){
                                Utils.database()
                                    .collection(Constants.carts)
                                    .document(Utils.currentUserID())
                                    .collection(Utils.currentUserID())
                                    .document(data.id)
                                    .delete()
                            }
                        }
                    }
                 val downloadedBookList = ArrayList<DownloadedBookModel>()
                 val downloadedBookAdapter = DownloadedBookAdapter(this, downloadedBookList)

                mAlertDualogue.downloading_downloadedBooksRecyclerView.setHasFixedSize(true)
                val downloadedBookManager = LinearLayoutManager(this)
                mAlertDualogue.downloading_downloadedBooksRecyclerView.layoutManager = downloadedBookManager
                mAlertDualogue.downloading_downloadedBooksRecyclerView.adapter = downloadedBookAdapter

                for (data in cartList){
                    downloadedBookList.add(DownloadedBookModel(data.link,data.name))
                }

                downloadedBookAdapter.notifyDataSetChanged()

                mAlertDualogue.setOnDismissListener {
                    finish()
                    Animatoo.animateSwipeRight(this)
                }
            }
            "Transaction Failed" ->{
                Toasty.error(this,"Error occur",Toasty.LENGTH_LONG).show()
            }
            else ->{
                Toasty.error(this,message, Toasty.LENGTH_LONG).show()
            }

        }
    }

    fun removeFromCart(id:String){
        Utils.database().collection(Constants.carts).document(Utils.currentUserID()).collection(Utils.currentUserID()).document(id).delete()
            .addOnSuccessListener {
                Toasty.success(this,"Removed Successfully",Toasty.LENGTH_LONG).show()
                getCarts()
            }
    }

    fun calculatePrice(){
        var totaPrice =  0
        for (data in cartList){
            val orderPrice =  data.price!!.toInt() * data.quantity!!
            totaPrice += orderPrice
        }
        cart_total.text = totaPrice.toString()
        totalOrderPrice =  totaPrice
    }

    private fun getCarts() {
        Utils.database()
            .collection(Constants.carts)
            .document(Utils.currentUserID())
            .collection(Utils.currentUserID())
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    cartList.clear()
                    for (data in it.documents){
                        val item = data.toObject(CartModel::class.java)
                        cartList.add(item!!)
                    }
                    cartAdapter!!.notifyDataSetChanged()
                    calculatePrice()
                    Utils.dismissLoader()
                }else{
                    cartList.clear()
                    cartAdapter!!.notifyDataSetChanged()
                    cart_payNow.visibility = View.GONE
                    Utils.dismissLoader()
                    Toasty.info(this,"Empty Cart",Toasty.LENGTH_LONG).show()
                }
            }
    }
}