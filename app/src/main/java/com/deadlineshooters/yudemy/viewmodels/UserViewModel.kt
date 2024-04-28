package com.deadlineshooters.yudemy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val _reminderDays = MutableLiveData<ArrayList<Int>>()
    val reminderDays: LiveData<ArrayList<Int>> = _reminderDays

    private val _reminderTimes = MutableLiveData<ArrayList<Int>>()
    val reminderTimes: LiveData<ArrayList<Int>> = _reminderTimes

    private val _isToggleReminder = MutableLiveData<Boolean>()
    val isToggleReminder: LiveData<Boolean> = _isToggleReminder

    val dayTimeCombinedData = MediatorLiveData<Pair<ArrayList<Int>, ArrayList<Int>>>().apply {
        addSource(reminderDays) { days ->
            if(reminderTimes.value != null)
                value = Pair(days, reminderTimes.value!!)
        }
        addSource(reminderTimes) { times ->
            if(reminderDays.value != null)
                value = Pair(reminderDays.value!!, times)
        }
    }

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


    fun getReminderDays() {
        UserRepository().getReminderDays { days ->
            Log.d("UserViewModel", "getReminderDays: $days")
            _reminderDays.value = days.map { Math.toIntExact(it.toLong()) } as ArrayList<Int>
        }
    }

    fun getReminderTimes() {
        UserRepository().getReminderTimes { times ->
            Log.d("UserViewModel", "getReminderTimes: $times")
            _reminderTimes.value = times.map { Math.toIntExact(it.toLong()) } as ArrayList<Int>
        }
    }

    fun addReminderDay(day: Int) {
        _reminderDays.value?.add(day)
        UserRepository().addReminderDay(day)
    }

    fun removeReminderDay(day: Int) {
        _reminderDays.value?.remove(day)
        UserRepository().removeReminderDay(day)
    }

    fun addReminderTime(time: Int) {
        _reminderTimes.value?.add(time)
        UserRepository().addReminderTime(time)
    }

    fun removeReminderTime(time: Int) {
        _reminderTimes.value?.remove(time)
        UserRepository().removeReminderTime(time)
    }

    fun checkIfReminderToggled() {
        UserRepository().checkIfReminderToggled { isToggled ->
            _isToggleReminder.value = isToggled
        }
    }

    fun toggleReminder(isToggle: Boolean) {
        _isToggleReminder.value = isToggle
        UserRepository().toggleReminder(isToggle) {
            if(it) {
                _reminderDays.value?.add(0)
                _reminderTimes.value?.add(0)
            }
        }
    }
}