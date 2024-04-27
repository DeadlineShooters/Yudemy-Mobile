package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.activities.SignUpActivity
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.models.User
import com.deadlineshooters.yudemy.repositories.UserRepository

/**
 * The ViewModel should contain LiveData objects or observable properties to hold the user data and state.
 * */
class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _userList = MutableLiveData<List<User>>()
    private val _userData = MutableLiveData<User>()

    val userList: LiveData<List<User>> = _userList
    val userData: LiveData<User> = _userData
    val curUser get() = _userData.value

    fun registerUser(activity: SignUpActivity, userInfo: User) {
//        mFireStore.collection(Constants.USERS)...
    }

    fun refreshUserData() {
        val user = userRepository.getUserData()
        _userData.value = user
    }

    fun getCurUser() {
        userRepository.getCurUser  {
            _userData.value = it
        }
    }

    fun getUserById(userId: String){
        userRepository.getUserById(userId) {
            _userData.value = it
        }
    }

    fun updateUserImage(instructorId: String, image: Image){
        userRepository.updateUserImage(instructorId, image){
            _userData.value = it
        }
    }

}