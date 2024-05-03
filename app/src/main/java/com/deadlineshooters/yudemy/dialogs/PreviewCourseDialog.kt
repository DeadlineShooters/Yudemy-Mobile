package com.deadlineshooters.yudemy.dialogs

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.databinding.DialogPreviewCourseBinding

class PreviewCourseDialog(private val videoPath: String): DialogFragment() {
    private lateinit var binding: DialogPreviewCourseBinding
    private var exoPlayer: ExoPlayer? = null
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPreviewCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(UnstableApi::class) override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exoPlayer = ExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(10000)
            .build()

        var mediaItem = MediaItem.fromUri(Uri.parse(videoPath))
        exoPlayer!!.setMediaItem(mediaItem)
        exoPlayer!!.prepare()
        exoPlayer!!.play()

        binding.previewVideoView.player = exoPlayer
        binding.previewVideoView.keepScreenOn = true

        val btnMuteAudio = binding.previewVideoView.findViewById<ImageView>(R.id.audio_mute)
        btnMuteAudio?.setOnClickListener {
            exoPlayer!!.volume = if (exoPlayer!!.volume == 0f) 1f else 0f
            btnMuteAudio.setImageResource(if (exoPlayer!!.volume == 0f) R.drawable.ic_volume_off_fill else R.drawable.ic_volume_up)
        }

        val btnFullscreen = binding.previewVideoView.findViewById<ImageView>(R.id.bt_fullscreen)
        btnFullscreen?.setOnClickListener { view ->
            isFullScreen = !isFullScreen

            activity?.requestedOrientation = if (isFullScreen) {
                btnFullscreen.setImageDrawable(
                    ContextCompat
                        .getDrawable(
                            requireContext(),
                            R.drawable.ic_close_fullscreen_fill
                        )
                )
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                btnFullscreen.setImageDrawable(
                    ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_open_in_full_fill)
                )
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        binding.btnClosePreview.setOnClickListener {
            if(isFullScreen) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                isFullScreen = false
            }
            dismiss()
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