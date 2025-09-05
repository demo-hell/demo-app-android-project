package br.com.mobicare.cielo.commons.utils

import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

class ViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver{

    private var binding: T? = null

    @MainThread
    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        binding = null
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (binding == null){
            binding = bindingClass.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, thisRef.layoutInflater) as T
            thisRef.viewLifecycleOwner.lifecycle.addObserver(this)
        }
        return binding!!
    }

}