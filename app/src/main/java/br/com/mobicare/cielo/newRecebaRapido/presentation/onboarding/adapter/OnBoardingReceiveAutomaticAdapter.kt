package br.com.mobicare.cielo.newRecebaRapido.presentation.onboarding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.databinding.LayoutReceiveAutomaticOnboardingItemBinding

class OnBoardingReceiveAutomaticAdapter(
    private val items: List<OnboardingItem>,
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutReceiveAutomaticOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false)

        if (items.isNotEmpty() && items.size > ONE) {
            val item = items[position]
            setupLayoutDefault(binding, item)
        }
        container.addView(binding.root)
        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun setupLayoutDefault(
        binding: LayoutReceiveAutomaticOnboardingItemBinding,
        item: OnboardingItem
    ) {
        binding.apply {
            tvTitleReceiveAutoOnboardingItem.text = item.title
            tvSubtitleReceiveAutoOnboardingItem.text = item.subtitle
            item.image?.let { ivReceiveAutoOnboardingItem.setImageResource(it) }
        }
    }
}