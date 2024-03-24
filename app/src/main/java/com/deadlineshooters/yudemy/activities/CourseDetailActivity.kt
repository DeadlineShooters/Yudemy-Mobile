package com.deadlineshooters.yudemy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityCourseDetailBinding
import com.deadlineshooters.yudemy.repositories.UserRepository
import vn.momo.momo_partner.AppMoMoLib
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


class CourseDetailActivity : AppCompatActivity() {
    private data class PendingPaymentRequest(val orderID: String)

    private lateinit var binding: ActivityCourseDetailBinding

    private val userRepository = UserRepository()

    private var amount = ""
    private val fee = "0"
    private var environment = 1 //developer default
    private val merchantName = "Udemy"
    private val merchantCode = "CGV19072017"
    private val merchantNameLabel = "Nhà cung cấp"
    private var description = "Thanh toán khóa học "
    private var pendingPaymentRequest: PendingPaymentRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        description += binding.tvTitle.text
        amount = binding.tvPrice.text.toString()

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvPrice.text = "${currencyFormat.format(amount.toInt())}"

        if (environment == 0) {
            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEBUG);
        } else if (environment == 1) {
            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        } else if (environment == 2) {
            AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION);
        }

        binding.btnBuy.setOnClickListener {
            requestPayment(Date().time.toString())
        }

        binding.btnWishlist.setOnClickListener {
            if (binding.btnWishlist.text == "Add to wishlist") {
                // userRepository.addToWishlist(courseID)
                binding.btnWishlist.text = "Wishlisted"
            } else {
                // userRepository.removeFromWishlist(courseID)
                binding.btnWishlist.text = "Add to wishlist"
            }
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    //Get token through MoMo app
    private fun requestPayment(orderID: String) {
        pendingPaymentRequest = PendingPaymentRequest(orderID)

        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT)
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN)

        val eventValue: MutableMap<String, Any> = HashMap()
        //client Required
        eventValue["merchantname"] = merchantName
        eventValue["merchantcode"] = merchantCode
        eventValue["amount"] = amount
        eventValue["orderId"] = orderID
        eventValue["orderLabel"] = "Mã đơn hàng"

        //client Optional - bill info
        eventValue["merchantnamelabel"] = merchantNameLabel
        eventValue["fee"] = 0
        eventValue["description"] = description

        //client extra data
        eventValue["requestId"] = merchantCode + "merchant_billId_" + System.currentTimeMillis()
        eventValue["partnerCode"] = merchantCode

        eventValue["extra"] = ""
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue)
    }

    //Get token callback from MoMo app a submit to server side
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                val status = data.getIntExtra("status", -1)
                when (status) {
                    0 -> { // Payment success
                        val token = data.getStringExtra("data")
                        val phoneNumber = data.getStringExtra("phonenumber")
                        // Send phoneNumber & token to your server for payment processing
                        handlePaymentSuccess(token, phoneNumber)
                    }

                    1 -> { // Payment failed
                        val message = data.getStringExtra("message") ?: "Thất bại"
                        handlePaymentFailure(message)
                    }

                    2 -> handlePaymentFailure("Payment cancelled")
                    else -> handlePaymentFailure("Unknown error")
                }
            } else {
                handlePaymentFailure("Data is null")
            }
        } else {
            handlePaymentFailure("Invalid request")
        }
    }

    private fun handlePaymentSuccess(token: String?, phoneNumber: String?) {
        Log.d("message", "success")
        // userRepository.addToCourseList(courseID)
        startActivity(Intent(this@CourseDetailActivity, StudentMainActivity::class.java))
    }

    private fun handlePaymentFailure(message: String) {
        Log.d("message", message)
    }


}