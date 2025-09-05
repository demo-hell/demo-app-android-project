package br.com.mobicare.cielo.onboarding.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.onboarding.domains.entities.OnboardingObj

/**
 * Created by gustavon on 20/10/17.
 */
class OnBoardingAdapter(fragmentManager: FragmentManager, var onBoardingObj: OnboardingObj) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        var image: Int = getImage(position)
        return OnBoardingPagesFragment.getInstance(onBoardingObj.pages?.get(position)!!, image)
    }

    override fun getCount(): Int {
        return onBoardingObj.pages!!.size
    }

    private fun getImage(position: Int): Int {
        var image: Int

        when (position) {
            0 -> image = R.drawable.device_one
            1 -> image = R.drawable.device_two
            2 -> image = R.drawable.device_three
            3 -> image = R.drawable.device_four
            4 -> image = R.drawable.device_five
            else -> {
                image = R.drawable.device_one
            }
        }
        return image
    }

}