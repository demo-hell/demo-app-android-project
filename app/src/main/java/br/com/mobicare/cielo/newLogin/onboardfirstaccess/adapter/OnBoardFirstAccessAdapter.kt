package br.com.mobicare.cielo.newLogin.onboardfirstaccess.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.OnBoardFirstAccessActivity
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.OnBoardFirstAccessPagerFragment
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.model.Item

class OnBoardFirstAccessAdapter(val itemList: List<Item>,
                                val listener: OnBoardFirstAccessActivity.CallProcedeUserInformation?,
                                fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int) = OnBoardFirstAccessPagerFragment
            .newInstance(itemList[position], position, listener)

    override fun getCount() = itemList.size
}