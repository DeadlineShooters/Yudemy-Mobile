package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.repositories.CertificateRepository

class CertificateViewModel : ViewModel() {
    private val certificateRepository = CertificateRepository()

}