package br.com.knowledge.capitulo8_fragment.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


interface OnFragmentManager{

    fun addFragment(
        layoutId: Int,
        fragment: Fragment,
        tag: String,
        backStack: Boolean,
        supportFragmentManager: FragmentManager
    )

    fun replaceFragment(
        layoutId: Int,
        fragment: Fragment,
        tag: String,
        backStack: Boolean,
        supportFragmentManager: FragmentManager
    )
    fun removeFragment(
        fragment: Fragment,
        supportFragmentManager: FragmentManager
    )
}