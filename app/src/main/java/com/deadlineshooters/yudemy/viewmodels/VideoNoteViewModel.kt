package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.VideoNoteRepository

class VideoNoteViewModel : ViewModel(){
    private val videoNoteRepository = VideoNoteRepository()

}