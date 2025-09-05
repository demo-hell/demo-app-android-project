package br.com.mobicare.cielo.commons.utils

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

fun LinearLayout.injectFragment(fm: FragmentManager, fragment: Fragment, tag: String) {
    val ll = LinearLayout(this.context)
    ll.orientation = LinearLayout.HORIZONTAL
    ll.id = Calendar.getInstance().timeInMillis.toInt()
    fm.beginTransaction().let { itTransaction ->
        itTransaction.add(ll.id, fragment, tag)
        itTransaction.commit()
    }
    this.addView(ll)
}