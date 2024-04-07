package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.activities.SignUpActivity
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

    fun registerUser(activity: SignUpActivity, userInfo: User) {
//        mFireStore.collection(Constants.USERS)...
    }


}