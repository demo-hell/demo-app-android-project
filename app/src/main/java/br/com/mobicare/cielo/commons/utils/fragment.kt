package br.com.mobicare.cielo.commons.utils

import androidx.viewbinding.ViewBinding
import androidx.fragment.app.Fragment

inline fun <reified  T : ViewBinding> Fragment.viewBinding() =
    ViewBindingDelegate(T::class.java)