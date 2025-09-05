package br.com.mobicare.cielo.commons.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.enums.Onboarding
import br.com.mobicare.cielo.conciliador.onboarding.CieloFacilitaOnboardingContract
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.include_header_onboarding_facilita.view.*
import kotlinx.android.synthetic.main.layout_cielo_facilita_onboarding_items.view.*
import kotlinx.android.synthetic.main.layout_cielo_unifica_onboarding_items.view.*
import kotlinx.android.synthetic.main.layout_pix_keys_onboarding_item.view.*
import pl.droidsonroids.gif.GifDrawable

class OnboardingAdapter(
    private val items: List<OnboardingItem>,
    private val listener: CieloFacilitaOnboardingContract.View? = null,
    @LayoutRes private val layout: Int = R.layout.layout_cielo_facilita_onboarding_items,
    private val id: Int = Onboarding.FACILITA.id,
    @StyleRes private val titleStyleRes: Int = R.style.label_18_display_500_montserrat_bold_center
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(layout, container, false)

        if (items.isNotEmpty() && items.size > 1) {
            val item = items[position]
            when (id) {
                Onboarding.FACILITA.id -> setupLayoutFacilita(view, item)
                Onboarding.UNIFICA.id -> setupLayoutUnifica(view, item)
                Onboarding.DEFAULT.id -> setupLayoutDefault(view, item, titleStyleRes)
            }
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun setupLayoutFacilita(view: View, item: OnboardingItem) {
        view.tv_title_onboarding_facilita?.text = item.title
        view.tv_subtitle_onboarding_facilita?.text = item.subtitle
        item.image?.let { view.iv_onboarding_facilita?.setImageResource(it) }

        view.iv_close_onboarding_facilita?.setOnClickListener {
            listener?.onFinish()
        }
    }

    private fun setupLayoutUnifica(view: View, item: OnboardingItem) {
        view.tv_title_onboarding_unifica?.text = item.title?.fromHtml()
        view.tv_subtitle_onboarding_unifica?.text = item.subtitle?.fromHtml()

        item.image?.let {
            if (item.isGif) {
                view.giv_onboarding_unifica?.setImageResource(it)
                view.giv_onboarding_unifica?.visible()
                (view.giv_onboarding_unifica?.drawable as? GifDrawable)?.loopCount = ZERO
                view.iv_onboarding_unifica?.gone()
            } else {
                view.iv_onboarding_unifica?.setImageResource(it)
                view.iv_onboarding_unifica?.visible()
                view.giv_onboarding_unifica?.gone()
            }
        }
    }

    private fun setupLayoutDefault(
        view: View,
        item: OnboardingItem,
        @StyleRes titleStyleRes: Int
    ) {
        view.tv_title_pix_keys_onboarding_item?.text = item.title
        view.tv_title_pix_keys_onboarding_item?.setTextAppearance(titleStyleRes)

        view.tv_subtitle_pix_keys_onboarding_item?.text = item.subtitle?.fromHtml()
        item.image?.let { view.iv_pix_keys_onboarding_item?.setImageResource(it) }
    }
}