package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.LanguageRepository

class LanguageViewModel : ViewModel(){
    private val languageRepository = LanguageRepository()

}