package com.deadlineshooters.yudemy.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.DetailSectionAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCourseDetailBinding
import com.deadlineshooters.yudemy.helpers.StringUtils
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseFeedback
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.CourseFeedbackRepository
import com.deadlineshooters.yudemy.repositories.UserRepository
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import vn.momo.momo_partner.AppMoMoLib
import java.text.NumberFormat
import java.util.*


class CourseDetailActivity : AppCompatActivity() {
    private data class PendingPaymentRequest(val orderID: String)

    private lateinit var binding: ActivityCourseDetailBinding

    private val userRepository = UserRepository()
    private val courseFeedbackRepo = CourseFeedbackRepository()
    private lateinit var course: Course

    private var amount = ""
    private val fee = "0"
    private var environment = 1 //developer default
    private val merchantName = "Udemy"
    private val merchantCode = "CGV19072017"
    private val merchantNameLabel = "Nhà cung cấp"
    private var description = "Thanh toán khóa học "
    private var pendingPaymentRequest: PendingPaymentRequest? = null
    private lateinit var sectionAdapter: DetailSectionAdapter
    private lateinit var courseViewModel: CourseViewModel


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        course = intent.getParcelableExtra("course", Course::class.java) ?: Course()
        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]

        populateCourseDetails(course)
        courseViewModel.refreshSections(course.id)

        AppMoMoLib.getInstance()
            .setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        description += binding.tvTitle.text
        amount = binding.tvPrice.text.toString()

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvPrice.text = currencyFormat.format(amount.toInt())

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
                userRepository.addToWishlist(course.id) {}
                binding.btnWishlist.text = "Wishlisted"
            } else {
                userRepository.removeFromWishlist(course.id) {}
                binding.btnWishlist.text = "Add to wishlist"
            }
        }


        courseViewModel.sectionsWithLectures.observe(
            this
        ) { sectionsWithLectures ->
            sectionAdapter = DetailSectionAdapter(sectionsWithLectures)

            binding.rvSections.adapter = sectionAdapter
            binding.rvSections.layoutManager = LinearLayoutManager(this)


        }


    }

    private fun populateCourseDetails(course: Course) {
        binding.tvTitle.text = course.name

        binding.tvIntro.text = course.introduction

        Glide.with(this)
            .load(course.thumbnail.secure_url)
            .into(binding.ivThumbnail)

        binding.tvRating.text = course.avgRating.toString()
        var totalRatings = course.oneStarCnt + course.twoStarCnt + course.threeStarCnt + course.fiveStarCnt + course.fourStarCnt
        val figuresText =
            getString(R.string.course_figures, totalRatings, course.totalStudents)
        binding.tvFigures.text = figuresText
        binding.tvCreatedDate.text = getString(R.string.created_date, course.createdDate)
        binding.tvPrice.text = StringUtils.trimDecimalZero(course.price.toString())

        val totalLengthHours = course.totalLength / 3600
        val totalLengthMinutes = (course.totalLength % 3600) / 60
        val curriculumOverviewText = getString(
            R.string.curriculum_overview,
            course.totalSection,
            course.totalLecture,
            totalLengthHours,
            totalLengthMinutes
        )
        binding.tvCurriculumOverview.text = curriculumOverviewText

        binding.tvDesc.text = course.description

        userRepository.loadInstructorData(this, course.instructor)

        // inject the 2 latest feedback

        courseFeedbackRepo.getLatestCourseFeedback(course.id, this) { feedbackList ->
            // Clear the parent layout
            var parentLayout = binding.llFeedbacks
            parentLayout.removeAllViews()

            if (feedbackList.isNullOrEmpty()) {
                // Handle the case where there are no feedback entries
                // For example, display a message to the user
                Toast.makeText(this, "No feedback entries for this course yet.", Toast.LENGTH_SHORT)
                    .show()
            } else {

                // For each feedback, inflate a new feedback layout and add it to the parent layout
                feedbackList.forEach { feedback ->
                    // Inflate the feedback layout
                    val inflater = LayoutInflater.from(this)
                    val feedbackLayout =
                        inflater.inflate(R.layout.detail_feedback_layout, parentLayout, false)

                    // Update the feedback layout with the feedback
                    updateLinearLayoutWithFeedback(feedbackLayout, feedback)

                    // Add the feedback layout to the parent layout
                    parentLayout.addView(feedbackLayout)
                }
            }
        }


    }

    fun updateLinearLayoutWithFeedback(linearLayout: View, feedback: CourseFeedback) {
        val ratingBar = linearLayout.findViewById<RatingBar>(R.id.rb_review)
        val nameTextView = linearLayout.findViewById<TextView>(R.id.tv_name)
        val dateTextView = linearLayout.findViewById<TextView>(R.id.tv_date)
        val tvFeedback = linearLayout.findViewById<TextView>(R.id.tv_feedback)

        // Update the rating, name, and date
        ratingBar.rating = feedback.rating.toFloat()
        courseFeedbackRepo.getUserFullName(feedback.userId) { fullName ->
            nameTextView.text = fullName
        }
        dateTextView.text =
            feedback.createdDatetime // assuming you have a createdDatetime field in your feedback
        tvFeedback.text = feedback.feedback
    }

    fun setInstructor(ins: User) {
        binding.tvNameInstructor.text = ins.fullName
        binding.tvHeadline.text = ins.instructor!!.headline
        binding.tvReviews.text =
            getString(R.string.instructor_reviews, ins.instructor!!.totalReviews)
        binding.tvStudents.text =
            getString(R.string.instructor_students, ins.instructor!!.totalStudents)

        binding.btnViewProfile.setOnClickListener {
            val intent = Intent(this, InstructorProfileActivity::class.java)
            intent.putExtra("instructor", ins)
            startActivity(intent)
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
}