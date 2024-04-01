package com.deadlineshooters.yudemy.fragments

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.adapters.CourseLearningAdapter
import com.deadlineshooters.yudemy.databinding.FragmentLectureLearningBinding
import com.deadlineshooters.yudemy.viewmodels.SectionViewModel
import com.deadlineshooters.yudemy.viewmodels.UserLectureViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE_ID = "courseId"

/**
 * A simple [Fragment] subclass.
 * Use the [LectureLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LectureLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var courseId: String = ""

    private lateinit var binding: FragmentLectureLearningBinding
    private lateinit var sectionViewModel: SectionViewModel
    private lateinit var userLectureViewModel: UserLectureViewModel

    private lateinit var videoView: PlayerView
    private lateinit var btnMuteAudio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            courseId = it.getString(ARG_COURSE_ID)
            courseId = "2tNxr8j5FosEueZrL3wH"
        }

        sectionViewModel = ViewModelProvider(this)[SectionViewModel::class.java]
        sectionViewModel.getSectionsCourseLearning(courseId)

        userLectureViewModel = ViewModelProvider(this)[UserLectureViewModel::class.java]
        userLectureViewModel.getUserLecturesByCourse("pQ7PAicEnDck3dBL8uIGZgKcUXM2", courseId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLectureLearningBinding.inflate(inflater, container, false)

        return binding.root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set video view
        videoView = (activity as CourseLearningActivity).getVideoView()

        val exoPlayer = ExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(10000)
            .build()

        exoPlayer.volume = 1f

        videoView.player = exoPlayer
        videoView.keepScreenOn = true

        btnMuteAudio = (activity as CourseLearningActivity).getBtnMuteAudio()
        btnMuteAudio.setOnClickListener {
            exoPlayer.volume = if (exoPlayer.volume == 0f) 1f else 0f
            btnMuteAudio.setImageResource(if (exoPlayer.volume == 0f) R.drawable.ic_volume_off_fill else R.drawable.ic_volume_up)
        }

        // Show the sections and lectures
        sectionViewModel.sectionsCourseLearning.observe(viewLifecycleOwner, Observer { sectionList ->
            userLectureViewModel.userLectures.observe(viewLifecycleOwner, Observer { userLectures ->
                val courseLearningAdapter = CourseLearningAdapter(sectionList, userLectures)
                binding.rvSections.adapter = courseLearningAdapter
                binding.rvSections.layoutManager = LinearLayoutManager(activity)

                var mediaItem = MediaItem.fromUri(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()

                courseLearningAdapter.onItemClick = { userLecture ->
                    Log.d("LectureLearningFragment", "User Lecture: $userLecture")

                    mediaItem = MediaItem.fromUri(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            })
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param courseId Course ID.
         * @return A new instance of fragment LectureLearningFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(courseId: String) =
            LectureLearningFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, courseId)
                }
            }
    }
}