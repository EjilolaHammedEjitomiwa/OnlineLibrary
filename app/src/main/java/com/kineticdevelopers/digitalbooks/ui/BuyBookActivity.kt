package com.kineticdevelopers.digitalbooks.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.button.MaterialButton
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Constants
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.model.CategoryModel
import com.kineticdevelopers.digitalbooks.model.UsersModel
import com.rajat.pdfviewer.PdfViewerActivity
import com.teamapt.monnify.sdk.Monnify
import com.teamapt.monnify.sdk.MonnifyTransactionResponse
import com.teamapt.monnify.sdk.Status
import com.teamapt.monnify.sdk.data.model.TransactionDetails
import com.teamapt.monnify.sdk.model.PaymentMethod
import com.teamapt.monnify.sdk.service.ApplicationMode
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_buy_book.*
import kotlinx.android.synthetic.main.downloading_dialogue.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.NullPointerException
import java.math.BigDecimal

class BuyBookActivity : AppCompatActivity() {
    var title:String? = null
    var description:String? = null
    var price:String? = null
    var image:String? = null
    var link:String? = null
    var bookID:String? = null

    var monnify = Monnify.instance

    var INITIATE_PAYMENT_REQUEST_CODE = 20
    var KEY_RESULT = "card_pay"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_book)

        title = intent.getStringExtra(Constants.title)
        description = intent.getStringExtra(Constants.description)
        price = intent.getStringExtra(Constants.price)
        image = intent.getStringExtra(Constants.image)
        link = intent.getStringExtra(Constants.link)
        bookID = intent.getStringExtra(Constants.bookID)

        buy_title.text = title
        buy_description.text = description
        buy_price.text = "$ ${price}"
        Utils.loadImage(this,image.toString(),buy_image)

        buy_buyButton.setOnClickListener {
            Utils.showLoader(this,"Processing Payment...")
            pay()
        }

        incrementView()


    }

    private fun incrementView() {
        Utils.database().collection(Constants.books)
            .document(bookID!!)
            .get()
            .addOnSuccessListener {
                if (it.exists()){
                   var currentView = it["views"].toString().toInt()
                    currentView+=1

                    Toasty.info(this,currentView.toString(),Toasty.LENGTH_LONG).show()
                    // update view
                    val map = HashMap<String,Any>()
                    map["views"] = currentView
                    Utils.database().collection(Constants.books)
                        .document(bookID!!)
                        .update(map)
                }
            }
    }

    private fun pay() {
        monnify.setApiKey(Constants.monnnifyApiKey)
        monnify.setContractCode(Constants.monnifyContractCode)
        monnify.setApplicationMode(ApplicationMode.TEST)

        val transaction = TransactionDetails.Builder()
            .amount(BigDecimal(price))
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
                    mAlertDualogue.downloading_gif.visibility = View.GONE
                }, 6000)

                mAlertDualogue.downloading_downloadCompleteButton.setOnClickListener {
                    val title = "$title${DateFormat.format("EE, dd MMM yyyy hh-mm-ss a", System.currentTimeMillis())})"
                    startActivity(PdfViewerActivity.buildIntent(this, link, false, title, "DigitalBooks/BooksPurchased", enableDownload = true))
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
}