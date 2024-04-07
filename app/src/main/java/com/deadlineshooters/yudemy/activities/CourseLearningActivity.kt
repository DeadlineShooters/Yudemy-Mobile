package com.deadlineshooters.yudemy.activities

import android.content.pm.ActivityInfo
import android.media.Image
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.ui.PlayerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.TabsAdapter
import com.deadlineshooters.yudemy.databinding.ActivityCourseLearningBinding
import com.deadlineshooters.yudemy.fragments.LectureLearningFragment
import com.deadlineshooters.yudemy.fragments.MoreLearningFragment
import com.deadlineshooters.yudemy.viewmodels.CourseViewModel
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.google.android.material.tabs.TabLayoutMediator


class CourseLearningActivity : AppCompatActivity() {
    private lateinit var courseLearningTabsAdapter: TabsAdapter
    private lateinit var binding: ActivityCourseLearningBinding
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var instructorViewModel: InstructorViewModel

    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
//        val courseId = intent.getStringExtra("courseId")// TODO: Uncomment this line
        val courseId = "2tNxr8j5FosEueZrL3wH"

        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        courseViewModel.getLearningCourse(courseId!!)

        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]

        courseViewModel.learningCourse.observe(this, Observer{
            binding.courseLearningTitle.text = it?.name
            instructorViewModel.getLearningInstructorName(it?.instructor!!)

            instructorViewModel.learningInstructorName.observe(this, Observer {name ->
                binding.courseLearningIns.text = name
            })
        })

        val fragments = listOf(LectureLearningFragment.newInstance(courseId!!), MoreLearningFragment.newInstance(courseId))
        courseLearningTabsAdapter = TabsAdapter(fragments, supportFragmentManager, lifecycle)
        binding.mViewPager.adapter = courseLearningTabsAdapter

        TabLayoutMediator(binding.tabLayout, binding.mViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lectures"
                1 -> "More"
                else -> "Lectures"
            }
        }.attach()

        val btFullscreen = findViewById<ImageView>(R.id.bt_fullscreen)
        btFullscreen.setOnClickListener { view ->
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

        findViewById<TextView>(R.id.btnBackFromPlayer).setOnClickListener {
            finish()
        }
    }

    fun getVideoView(): PlayerView {
        return binding.videoView
    }

    fun getBtnMuteAudio(): ImageView {
        return findViewById(R.id.audio_mute)
    }
}