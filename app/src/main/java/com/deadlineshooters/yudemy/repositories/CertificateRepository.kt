package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Certificate
import com.google.firebase.firestore.FirebaseFirestore

class CertificateRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val certificateCollection = mFireStore.collection("certificate")

//    public fun getCertificateList(userId: String, callback: (ArrayList<Certificate>) -> Unit){
//        mFireStore.collection("certificates")
//            .whereEqualTo("userId", userId)
//            .get()
//            .addOnSuccessListener { result ->
//                val list: ArrayList<Certificate> = ArrayList()
//                for (document in result) {
//                    val certificate = document.toObject(Certificate::class.java)
//                    list.add(certificate)
//                }
//                callback(list)
//            }
//            .addOnFailureListener { exception ->
//                callback(ArrayList())
//            }
//    }
    fun getCertificate(userId: String, courseId: String, callback: (Certificate?) -> Unit){
        certificateCollection.whereEqualTo("userId", userId).whereEqualTo("courseId", courseId)
        .get().addOnSuccessListener { result ->
            if(result.isEmpty){
                Log.d("CertificateDialog", "Empty")
                callback(null)
            } else {
                val certificate = result.documents[0].toObject(Certificate::class.java)
                callback(certificate)
            }
        }
        .addOnFailureListener { exception ->
            callback(null)
        }
    }

    fun addCertificate(userId: String, courseId: String, certificate: Certificate, callback: (Certificate) -> Unit){
        getCertificate(userId, courseId){
            if(it == null){
                certificateCollection.add(certificate).addOnSuccessListener {
                    callback(certificate)
                }
            }
        }
    }
}