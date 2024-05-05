package com.deadlineshooters.yudemy.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.TabsAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCourseLearningBinding
import com.deadlineshooters.yudemy.fragments.LectureLearningFragment
import com.deadlineshooters.yudemy.fragments.MoreLearningFragment
import com.deadlineshooters.yudemy.models.Certificate
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.viewmodels.CertificateViewModel
import com.deadlineshooters.yudemy.viewmodels.CourseProgressViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class CourseLearningActivity : BaseActivity() {
    private lateinit var courseLearningTabsAdapter: TabsAdapter
    private lateinit var binding: ActivityCourseLearningBinding

    private var isFullScreen = false

    private lateinit var learningCourse: Course
    private lateinit var instructorName: String
    private var progress: Int = 0

    private lateinit var courseProgressViewModel: CourseProgressViewModel
    private lateinit var certificateViewModel: CertificateViewModel
    var currentLecture: Map<Lecture, Boolean>? = null
    var exoPlayer: ExoPlayer? = null


    private var isUpdateProgress = false

    private lateinit var btFullscreen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        courseProgressViewModel = ViewModelProvider(this)[CourseProgressViewModel::class.java]

        val intent = intent
        learningCourse = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("course", Course::class.java)!!
        } else {
            intent.getParcelableExtra("course")!!
        }
        instructorName = intent.getStringExtra("instructorName") ?: ""
        progress = intent.getIntExtra("progress", 0)
        Log.d("CourseLearningActivity", "onCreate: $progress")

        binding.courseLearningTitle.text = learningCourse.name
        binding.courseLearningIns.text = instructorName

        val fragments = listOf(LectureLearningFragment.newInstance(learningCourse), MoreLearningFragment.newInstance(learningCourse))
        courseLearningTabsAdapter = TabsAdapter(fragments, supportFragmentManager, lifecycle)
        binding.mViewPager.adapter = courseLearningTabsAdapter

        TabLayoutMediator(binding.tabLayout, binding.mViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lectures"
                1 -> "More"
                else -> "Lectures"
            }
        }.attach()

        btFullscreen = findViewById(R.id.bt_fullscreen)
        btFullscreen.setOnClickListener { view ->
            rotateScreen()
        }

        findViewById<TextView>(R.id.btnBackFromPlayer).setOnClickListener {
            if(isFullScreen) {
                rotateScreen()
            }
            else {
                val intent = Intent()
                intent.putExtra("isUpdateProgress", isUpdateProgress)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    fun getVideoView(): PlayerView {
        return binding.videoView
    }

    fun getBtnMuteAudio(): ImageView {
        return findViewById(R.id.audio_mute)
    }

    fun updateProgress(isComplete: Boolean, numLectures: Int) {
        isUpdateProgress = true
        Log.d("CourseLearningActivity", "updateProgress: $progress, $isComplete, $numLectures")
        val numCompleteLectures = (progress * numLectures / 100).toDouble().roundToInt()
        val newProgress = ((numCompleteLectures + (if (isComplete) 1 else -1)) * 100 / numLectures).toDouble().roundToInt()
        if(newProgress != progress) {
            this.progress = newProgress
            courseProgressViewModel.updateCourseProgress(learningCourse.id, newProgress)
            if(newProgress == 100) {
                BaseActivity().showNoButtonDialogWithTimeout(
                    this,
                    "Course Completed!",
                    "Congratulations! You have completed the course!",
                    3000
                )
                createCertificate()
            }
        }
    }

    private fun createCertificate() {
        certificateViewModel = ViewModelProvider(this)[CertificateViewModel::class.java]

        val certificate = Certificate("", learningCourse.id, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString(), learningCourse.totalLength, learningCourse.name, BaseActivity().getCurrentUserID())
        certificateViewModel.addCertificate(BaseActivity().getCurrentUserID(), learningCourse.id, certificate)
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer != null && exoPlayer!!.isPlaying) {
            exoPlayer!!.playWhenReady = false
        }
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer!!.stop();
            exoPlayer!!.release();
            exoPlayer = null;
        }
    }

    private fun rotateScreen() {
        requestedOrientation = if (!isFullScreen) {
            btFullscreen.setImageDrawable(
                ContextCompat
                    .getDrawable(
                        applicationContext,
                        R.drawable.ic_close_fullscreen_fill
                    )
            )
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            btFullscreen.setImageDrawable(
                ContextCompat
                    .getDrawable(applicationContext, R.drawable.ic_open_in_full_fill)
            )
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        isFullScreen = !isFullScreen

        if(isFullScreen) {
            binding.contentLearningLayout.visibility = View.GONE
            binding.videoView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        else {
            binding.contentLearningLayout.visibility = View.VISIBLE
            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                200F, resources.displayMetrics
            ).toInt()
            binding.videoView.layoutParams.height = height
        }
    }
}