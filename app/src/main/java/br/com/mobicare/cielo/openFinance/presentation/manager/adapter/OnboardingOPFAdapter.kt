package br.com.mobicare.cielo.openFinance.presentation.manager.adapter

import android.app.Activity
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceOnboardingItemBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pix.constants.EMPTY

class OnboardingOPFAdapter(private val items: List<OnboardingItem>) :
    PagerAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun isViewFromObject(view: View, item: Any): Boolean {
        return view == item as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutOpenFinanceOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        )
        setupLayoutOnboarding(binding, items[position])
        container.addView(binding.root)
        return binding.root
    }

    private fun setupLayoutOnboarding(
        binding: LayoutOpenFinanceOnboardingItemBinding,
        item: OnboardingItem
    ) {
        binding.apply {
            tvTitleOpenFinanceOnboardingItem.text = item.title
            tvSubtitleOpenFinanceOnboardingItem.text = item.subtitle?.fromHtml()
            item.image?.let { ivOpenFinanceOnboardingItem.setImageResource(it) }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}