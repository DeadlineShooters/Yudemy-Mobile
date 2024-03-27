package com.deadlineshooters.yudemy.repositories

import android.app.Activity
import android.util.Log
import com.deadlineshooters.yudemy.fragments.AccountFragment
import com.deadlineshooters.yudemy.models.Instructor
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val usersCollection = mFireStore.collection(Constants.USERS)

    /**
     * Load user data onto any page
     * */
    fun loadUserData(context: Any) {
        val documentReference = usersCollection.document(getCurrentUserID())

        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(this.javaClass.simpleName, "DocumentSnapshot data: ${document.data}")
                    val loggedInUser = document.toObject(User::class.java)!!

                    when (context) {
                        is AccountFragment -> {
                            context.setUserData(loggedInUser)
                        }
                    }

                } else {
                    Log.d(this.javaClass.simpleName, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(this.javaClass.simpleName, "get failed with ", exception)
            }
    }

    fun initInstructor() {
        val userRef = usersCollection.document(getCurrentUserID())

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(this.javaClass.simpleName, "DocumentSnapshot data: ${document.data}")

                // Check if the instructor field is null
                if (document.get("instructor") == null) {
                    val newInstructor = Instructor()

                    userRef.update("instructor", newInstructor)
                        .addOnSuccessListener {
                            Log.d(
                                this.javaClass.simpleName,
                                "DocumentSnapshot successfully updated!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                this.javaClass.simpleName,
                                "Error updating document",
                                e
                            )
                        }
                }
            } else {
                Log.d(this.javaClass.simpleName, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(this.javaClass.simpleName, "get failed with ", exception)
            }
    }

    fun addUser(user: User) {
        mFireStore.collection(Constants.USERS)
            .document(mAuth.currentUser!!.uid)
            .set(user)
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid.toString()
    }

}