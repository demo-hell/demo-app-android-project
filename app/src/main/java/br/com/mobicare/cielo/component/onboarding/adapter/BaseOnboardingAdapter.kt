package br.com.mobicare.cielo.component.onboarding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.component.onboarding.model.BaseOnboardingPage
import br.com.mobicare.cielo.databinding.LayoutBaseOnboardingItemBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pix.constants.EMPTY

class BaseOnboardingAdapter(
    private val items: List<BaseOnboardingPage>
) : PagerAdapter() {

    override fun getCount() = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutBaseOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        )

        setupLayout(binding, items[position])
        container.addView(binding.root)
        return binding.root
    }

    private fun setupLayout(
        binding: LayoutBaseOnboardingItemBinding,
        item: BaseOnboardingPage
    ) {
        val context = binding.root.context
        binding.apply {
            tvTitleOnboardingItem.text = item.title?.let { context.getString(it) } ?: EMPTY
            tvSubtitleOnboardingItem.text =
                (item.subtitle?.let { context.getString(it) } ?: EMPTY).fromHtml()
            item.image?.let {
                ivOnboardingItem.setImageResource(it)
            }
        }
    }

}