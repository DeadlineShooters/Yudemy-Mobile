package com.deadlineshooters.yudemy.fragments

import android.net.Uri
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
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.viewmodels.SectionViewModel
import com.deadlineshooters.yudemy.viewmodels.UserLectureViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

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
    private var exoPlayer: ExoPlayer? = null

//    private var currentLecture: Lecture? = null
    private var currentLecture: Map<Lecture, Boolean>? = null
    private var currentSectionIdx: Int = 0
    private var currentLectureIdx: Int = 0

    private var courseLearningAdapter: CourseLearningAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            courseId = it.getString(ARG_COURSE_ID)
            // TODO: remove the below and retrieve courseId from argument
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

        exoPlayer = ExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(10000)
            .build()

        exoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_ENDED) {
                    // Mark the lecture as completed
                    if(currentLecture != null && !currentLecture!!.values.first()) {
                        userLectureViewModel.markLecture("pQ7PAicEnDck3dBL8uIGZgKcUXM2", currentLecture!!.keys.first()._id, true, currentSectionIdx, currentLectureIdx)
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
        sectionViewModel.sectionsCourseLearning.observe(viewLifecycleOwner, Observer { sectionList ->
            userLectureViewModel.userLectures.observe(viewLifecycleOwner, Observer { userLectures ->
                courseLearningAdapter = CourseLearningAdapter(sectionList, userLectures)
                binding.rvSections.adapter = courseLearningAdapter
                binding.rvSections.layoutManager = LinearLayoutManager(activity)

                currentLecture = userLectures[0][0]

                var mediaItem = MediaItem.fromUri(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
                exoPlayer!!.setMediaItem(mediaItem)
                exoPlayer!!.prepare()
                exoPlayer!!.play()

                courseLearningAdapter!!.onItemClick = { userLecture, sectionIdx, lectureIdx ->
                    currentLecture = userLecture
                    currentSectionIdx = sectionIdx
                    currentLectureIdx = lectureIdx

                    mediaItem = MediaItem.fromUri(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

                    exoPlayer!!.setMediaItem(mediaItem)
                    exoPlayer!!.prepare()
                    exoPlayer!!.play()
                }

                courseLearningAdapter!!.onLongPress = { userLecture, sectionIdx, lectureIdx ->
                    createActionsDialog(userLecture, sectionIdx, lectureIdx).show()
                }
            })
        })
    }

    private fun createActionsDialog(userLecture: Map<Lecture, Boolean>, sectionIdx: Int, lectureIdx: Int): BottomSheetDialog {
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
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvActions.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter, filterIdx ->
            if(filterIdx == 0) {
                userLectureViewModel.markLecture("pQ7PAicEnDck3dBL8uIGZgKcUXM2", userLecture.keys.first()._id, !userLecture.values.first(), sectionIdx, lectureIdx)
                courseLearningAdapter?.notifyLectureMarked(sectionIdx, lectureIdx)
                dialog.dismiss()
            }
            else if(filterIdx == 1) {
                val qaDialog = QADialog()
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