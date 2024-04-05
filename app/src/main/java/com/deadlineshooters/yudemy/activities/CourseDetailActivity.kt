package com.deadlineshooters.yudemy.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.ActivityCourseDetailBinding
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.utils.PaymentsUtil
import com.deadlineshooters.yudemy.viewmodels.CheckoutViewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.contract.TaskResultContracts
import vn.momo.momo_partner.AppMoMoLib
import java.text.NumberFormat
import java.util.*


class CourseDetailActivity : AppCompatActivity() {
    private data class PendingPaymentRequest(val orderID: String)

    private lateinit var binding: ActivityCourseDetailBinding

    private val userRepository = UserRepository()
    private lateinit var course: Course

    private var amount = ""
    private val fee = "0"
    private var environment = 1 //developer default
    private val merchantName = "Udemy"
    private val merchantCode = "CGV19072017"
    private val merchantNameLabel = "Nhà cung cấp"
    private var description = "Thanh toán khóa học "
    private var pendingPaymentRequest: PendingPaymentRequest? = null

    private val paymentDataLauncher =
        registerForActivityResult(TaskResultContracts.GetPaymentDataResult()) { taskResult ->
            when (taskResult.status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    taskResult.result!!.let {
                        Log.i("Google Pay result:", it.toJson())
                        model.setPaymentData(it)
                    }
                }
                //CommonStatusCodes.CANCELED -> The user canceled
                //AutoResolveHelper.RESULT_ERROR -> The API returned an error (it.status: Status)
                //CommonStatusCodes.INTERNAL_ERROR -> Handle other unexpected errors
            }
        }

    private val model: CheckoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        course = intent.getParcelableExtra<Course>("course") ?: Course()

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        description += binding.tvTitle.text
        amount = binding.tvPrice.text.toString()

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvPrice.text = currencyFormat.format(amount.toInt())

        when (environment) {
            0 -> {
                AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEBUG);
            }

            1 -> {
                AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
            }

            2 -> {
                AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.PRODUCTION);
            }
        }

        binding.btnBuy.setOnClickListener {
            requestPayment(Date().time.toString())
        }

        binding.btnWishlist.setOnClickListener {
            if (binding.btnWishlist.text == "Add to wishlist") {
                userRepository.addToWishlist(course.id) {}
                binding.btnWishlist.text = "Wishlisted"
            } else {
                userRepository.removeFromWishlist(course.id) {}
                binding.btnWishlist.text = "Add to wishlist"
            }
        }

        val googlePayButton = binding.googlePayButton

        googlePayButton.initialize(
            ButtonOptions.newBuilder()
                .setButtonType(ButtonConstants.ButtonType.PLAIN)
                .setCornerRadius(8)
                .setAllowedPaymentMethods(PaymentsUtil.allowedPaymentMethods.toString()).build()
        )
        googlePayButton.setOnClickListener { requestPayment() }

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
        userRepository.addToCourseList(course.id) {}
        startActivity(Intent(this@CourseDetailActivity, StudentMainActivity::class.java))
    }

    private fun handlePaymentFailure(message: String) {
        Log.d("message", message)
    }

    override fun onResume() {
        super.onResume()
        userRepository.isInWishlist(course.id) { isInWishlist ->
            if (isInWishlist) {
                binding.btnWishlist.text = "Wishlisted"
            } else {
                binding.btnWishlist.text = "Add to wishlist"
            }
        }
    }

    private fun requestPayment() {
        val task = model.getLoadPaymentDataTask(priceCents = 1299000L)
        task.addOnCompleteListener(paymentDataLauncher::launch)
    }
}