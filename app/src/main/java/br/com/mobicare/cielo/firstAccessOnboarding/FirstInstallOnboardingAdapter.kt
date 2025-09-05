package br.com.mobicare.cielo.firstAccessOnboarding

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.databinding.LayoutFirstInstallOnboardingItemBinding
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.adapter.IDOnboardingItem
import br.com.mobicare.cielo.pix.constants.EMPTY

class FirstInstallOnboardingAdapter(
    private val items: List<FirstInstallOnboardingItem>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = LayoutFirstInstallOnboardingItemBinding.inflate(
            LayoutInflater.from(container.context), container, false
        )

        val item = items[position]
        setupLayoutIDOnboarding(binding, item)

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun setupLayoutIDOnboarding(
        binding: LayoutFirstInstallOnboardingItemBinding,
        item: FirstInstallOnboardingItem
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

            item.image?.let { ivOnboardingItem.setImageResource(it) }
        }
    }
}