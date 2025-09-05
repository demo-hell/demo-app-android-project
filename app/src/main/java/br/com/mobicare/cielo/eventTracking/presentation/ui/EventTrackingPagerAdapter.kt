package br.com.mobicare.cielo.eventTracking.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.eventTracking.utils.ScreenData

class EventTrackingPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentsData: MutableList<ScreenData>,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    fun getEnabledTitles(): List<String> {
        return fragmentsData.filter { it.isEnabled }.map { it.title }
    }

    override fun getItemCount(): Int {
        return fragmentsData.count { it.isEnabled }
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsData.filter { it.isEnabled }[position].createFragment.invoke()
    }
}