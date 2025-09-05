package br.com.mobicare.cielo.commons.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentDetector private constructor() : FragmentManager.FragmentLifecycleCallbacks() {


    companion object {
        val fragmentStack: LinkedHashSet<String> = LinkedHashSet()

        private var instance: FragmentDetector? = null

        fun getFragmentDetectorInstance(): FragmentDetector {

            if (instance == null) {
                instance = FragmentDetector()
            }

            return instance as FragmentDetector
        }

    }

    override fun onFragmentCreated(fm: FragmentManager,
                                   f: Fragment,
                                   savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        fragmentStack.add(f::class.java.simpleName)
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        fragmentStack.remove(f::class.java.simpleName)
    }



}