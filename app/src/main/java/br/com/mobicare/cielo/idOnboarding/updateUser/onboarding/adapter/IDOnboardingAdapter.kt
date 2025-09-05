package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.databinding.LayoutIdOnboardingItemBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.IDOnboardingContract
import br.com.mobicare.cielo.pix.constants.EMPTY

class IDOnboardingAdapter(
    private val items: List<IDOnboardingItem>,
    private val listener: IDOnboardingContract.View? = null,
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutIdOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        )

        setupLayoutIDOnboarding(binding, items[position])
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun setupLayoutIDOnboarding(
        binding: LayoutIdOnboardingItemBinding,
        item: IDOnboardingItem
    ) {
        binding.apply {
            tvTitleOnboardingItem.text = item.title
            tvSubtitleOnboardingItem.text = SpannableString(
                HtmlCompat
                    .fromHtml(
                        item.subtitle ?: EMPTY,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            )

            btnActionOnboardingItem.visible(item.isShowButton)
            btnActionOnboardingItem.text = item.buttonText

            item.image?.let { ivOnboardingItem.setImageResource(it) }

            btnActionOnboardingItem.setOnClickListener {
                listener?.onShowHelpCenter()
            }
        }
    }
}