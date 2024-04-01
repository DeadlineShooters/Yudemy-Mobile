package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Certificate
import com.deadlineshooters.yudemy.repositories.CertificateRepository

class CertificateViewModel : ViewModel() {
    private val certificateRepository = CertificateRepository()

    private val _certificate = MutableLiveData<Certificate>()
    val certificate get() = _certificate

    fun getCertificate(userId: String, courseId: String) {
        certificateRepository.getCertificate(userId, courseId) {
            _certificate.value = it
        }
    }

}