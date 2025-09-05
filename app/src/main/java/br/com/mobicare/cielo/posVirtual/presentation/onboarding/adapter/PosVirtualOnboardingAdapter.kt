package br.com.mobicare.cielo.posVirtual.presentation.onboarding.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.databinding.LayoutPosVirtualOnboardingItemBinding
import br.com.mobicare.cielo.pix.constants.EMPTY

class PosVirtualOnboardingAdapter(
    private val items: List<PosVirtualOnboardingItem>
) : PagerAdapter() {

    override fun getCount() = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutPosVirtualOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        )

        setupLayout(binding, items[position])
        container.addView(binding.root)
        return binding.root
    }

    private fun setupLayout(
        binding: LayoutPosVirtualOnboardingItemBinding,
        item: PosVirtualOnboardingItem
    ) {
        binding.apply {
            tvTitleOnboardingItem.text = item.title
            tvSubtitleOnboardingItem.text = SpannableString(
                HtmlCompat.fromHtml(item.subtitle ?: EMPTY, HtmlCompat.FROM_HTML_MODE_LEGACY)
            )
            item.image?.let {
                ivOnboardingItem.setImageResource(it)
            }
        }
    }
}