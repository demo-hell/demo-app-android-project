package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import kotlinx.android.synthetic.main.item_banner_vp.*

@SuppressLint("ValidFragment")
class ItemFragmentVP(val item: ItemBannerMigration?) : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.item_banner_vp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val textFinal = SpannableStringBuilder()
        val title = item?.firstName
        var title1 = title?.indexOf(":").toString()

        val firstPart = title?.substring(0, title1.toInt())
        val secondPart = title?.substring(title1.toInt(), title.length)

        textFinal.append(firstPart?.addSpannable(TextAppearanceSpan(activity!!, R.style.TextBannerMigration14sp)))
        textFinal.append(secondPart?.addSpannable(TextAppearanceSpan(activity!!, R.style.TextBannerMigration14spBottom)))



        iv_banner_migration.apply {
            setImageDrawable(item?.imageUrl)
        }
        tv_banner_migration.apply {
            text = textFinal
        }
    }
}