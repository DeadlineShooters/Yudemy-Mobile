package com.deadlineshooters.yudemy.fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.adapters.CourseLearningAdapter
import com.deadlineshooters.yudemy.adapters.BottomSheetDialogAdapter
import com.deadlineshooters.yudemy.databinding.FragmentLectureLearningBinding
import com.deadlineshooters.yudemy.dialogs.QADialog
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.models.CourseProgress
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.repositories.CourseProgressRepository
import com.deadlineshooters.yudemy.viewmodels.CourseProgressViewModel
import com.deadlineshooters.yudemy.viewmodels.SectionViewModel
import com.deadlineshooters.yudemy.viewmodels.UserLectureViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURSE = "course"
private const val ARG_PROGRESS = "progress"

/**
 * A simple [Fragment] subclass.
 * Use the [LectureLearningFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LectureLearningFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var course: Course? = null

    private lateinit var binding: FragmentLectureLearningBinding
    private lateinit var userLectureViewModel: UserLectureViewModel
    private lateinit var courseProgressViewModel: CourseProgressViewModel

    private lateinit var videoView: PlayerView
    private lateinit var btnMuteAudio: ImageView
    private var exoPlayer: ExoPlayer? = null

//    private var currentLecture: Map<Lecture, Boolean>? = null
    private var currentSectionIdx: Int = 0
    private var currentLectureIdx: Int = 0

    private var courseLearningAdapter: CourseLearningAdapter? = null

    private var numLectures = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_COURSE, Course::class.java)
            } else {
                it.getParcelable(ARG_COURSE)
            }
        }

        userLectureViewModel = ViewModelProvider(this)[UserLectureViewModel::class.java]
        userLectureViewModel.getLectureLearningData(course!!.id)

        courseProgressViewModel = ViewModelProvider(this)[CourseProgressViewModel::class.java]
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

        exoPlayer = ExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(10000)
            .build()

        exoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_ENDED) {
                    // Mark the lecture as completed
                    if((activity as CourseLearningActivity).currentLecture != null && !(activity as CourseLearningActivity).currentLecture!!.values.first()) {
                        userLectureViewModel.markLecture((activity as CourseLearningActivity).currentLecture!!.keys.first()._id, true, currentSectionIdx, currentLectureIdx)
                        updateProgress()
                        courseLearningAdapter?.notifyLectureMarked(currentSectionIdx, currentLectureIdx)
                    }
                }
            }
        })


        videoView.player = exoPlayer
        videoView.keepScreenOn = true

        btnMuteAudio = (activity as CourseLearningActivity).getBtnMuteAudio()
        btnMuteAudio.setOnClickListener {
            exoPlayer!!.volume = if (exoPlayer!!.volume == 0f) 1f else 0f
            btnMuteAudio.setImageResource(if (exoPlayer!!.volume == 0f) R.drawable.ic_volume_off_fill else R.drawable.ic_volume_up)
        }

        // Show the sections and lectures
        userLectureViewModel.combinedData.observe(
            viewLifecycleOwner,
            Observer { (sectionList, userLectures) ->
                numLectures = userLectures.flatten().size

                courseLearningAdapter = CourseLearningAdapter(sectionList, userLectures)
                binding.rvSections.adapter = courseLearningAdapter
                binding.rvSections.layoutManager = LinearLayoutManager(activity)

                if (userLectures.isNotEmpty() && userLectures[0].isNotEmpty()) {
                    (activity as CourseLearningActivity).currentLecture = userLectures[0][0]

                    var vidPath =  (activity as CourseLearningActivity).currentLecture!!.keys.first().content.secure_url

                    var mediaItem = MediaItem.fromUri(Uri.parse(vidPath))
                    exoPlayer!!.setMediaItem(mediaItem)
                    exoPlayer!!.prepare()
                    exoPlayer!!.play()

                    courseLearningAdapter!!.onItemClick = { userLecture, sectionIdx, lectureIdx ->
                        (activity as CourseLearningActivity).currentLecture = userLecture
                        currentSectionIdx = sectionIdx
                        currentLectureIdx = lectureIdx

                        vidPath = userLecture.keys.first().content.secure_url
                        mediaItem = MediaItem.fromUri(Uri.parse(vidPath))

                        exoPlayer!!.setMediaItem(mediaItem)
                        exoPlayer!!.prepare()
                        exoPlayer!!.play()
                    }

                    courseLearningAdapter!!.onLongPress = { userLecture, sectionIdx, lectureIdx ->
                        createActionsDialog(userLecture, sectionIdx, lectureIdx).show()
                    }
                } else {
                    // TODO: handle empty lectures or sections
                }
            })
//        })
    }

    private fun createActionsDialog(
        userLecture: Map<Lecture, Boolean>,
        sectionIdx: Int,
        lectureIdx: Int
    ): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheet = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        val rvActions = bottomSheet.findViewById<RecyclerView>(R.id.rvFilters)
        bottomSheet.findViewById<TextView>(R.id.tvDialogOptions).visibility = View.GONE

        val options = if (userLecture.values.first())
            arrayListOf(resources.getString(R.string.mark_as_incomplete))
        else
            arrayListOf(resources.getString(R.string.mark_as_complete))

        options.add(resources.getString(R.string.qa_for_lecture))
        val adapter = BottomSheetDialogAdapter(options)
        rvActions!!.adapter = adapter
        rvActions.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvActions.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter, filterIdx ->
            if (filterIdx == 0) {
                userLectureViewModel.markLecture(
                    userLecture.keys.first()._id,
                    !userLecture.values.first(),
                    sectionIdx,
                    lectureIdx
                )
                updateProgress(!userLecture.values.first())
                courseLearningAdapter?.notifyLectureMarked(sectionIdx, lectureIdx)
                dialog.dismiss()
            } else if (filterIdx == 1) {
                val qaDialog = QADialog(course!!.id, (activity as CourseLearningActivity).currentLecture!!.keys.first()._id)
                qaDialog.show(parentFragmentManager, "QADialog")
                dialog.dismiss()
            }
        }

        bottomSheet.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(bottomSheet)
        return dialog
    }

    private fun updateProgress(isComplete: Boolean = true) {
        if (numLectures != 0)
            (activity as CourseLearningActivity).updateProgress(isComplete, numLectures)
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
        fun newInstance(course: Course) =
            LectureLearningFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COURSE, course)
//                    putInt(ARG_PROGRESS, progress)
                }
            }
    }

    override fun onPause() {
        super.onPause()

        if (exoPlayer!!.isPlaying) {
            if (exoPlayer != null) {
                exoPlayer!!.playWhenReady = false
            }
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
}