package com.deadlineshooters.yudemy.helpers

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Course

class FragmentHelper {
    companion object {
        fun replaceFragment(fragment: Fragment, course: Course? = null, currentFragment: Fragment) {
            val bundle = Bundle()
            if (course != null) {
                bundle.putParcelable("course", course)
                fragment.arguments = bundle
            }

            val fragmentManager = currentFragment.requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayoutInstructor, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

}