package br.com.mobicare.cielo.login.presentation.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.LOGIN_COMO_ACESSAR_PASSO_1
import br.com.mobicare.cielo.commons.analytics.LOGIN_COMO_ACESSAR_PASSO_2
import br.com.mobicare.cielo.commons.analytics.LOGIN_COMO_ACESSAR_PASSO_3
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerIntroduce
import kotlinx.android.synthetic.main.layout_introduce_login.*

@SuppressLint("ValidFragment")
class ItemIntroduceFragment : BaseFragment() {

    private var item: ItemBannerIntroduce? = null

    companion object {
        fun newInstance(itemBanner: ItemBannerIntroduce?) = ItemIntroduceFragment().apply {
            this.item = itemBanner
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_introduce_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_img_ilustrativa.visibility = if(item?.id == 1) View.GONE else View.VISIBLE
        txt_welcome_title.text = if(item?.id == 1) "Acessar com seu CPF ou E-mail" else "Onde encontrar nº do estabelecimento"
        txt_welcome_subtitle.text = if(item?.id == 1) getText01() else getText02()

            item?.id?.let {
                Analytics.trackScreenView(
                    screenName = when(it){
                        1->LOGIN_COMO_ACESSAR_PASSO_1
                        2->LOGIN_COMO_ACESSAR_PASSO_2
                        3->LOGIN_COMO_ACESSAR_PASSO_3
                        else -> LOGIN_COMO_ACESSAR_PASSO_1
                    },
                    screenClass = this.javaClass
                )
            }

        iv_banner_introduce.apply {
            setImageDrawable(item?.imageUrl)
        }

        txt_welcome_subtitle.apply {
            //text = textFinal
        }
    }

    private fun getText01(): CharSequence?{
        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.subtitle_vp_introduce_01))
        return  textFinal
    }

    private fun getText02(): CharSequence?{
        val textFinal = SpannableStringBuilder()

        textFinal.append(requireActivity().getString(R.string.subtitle_vp_introduce_02_a))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(requireActivity().getString(R.string.subtitle_vp_introduce_02_b))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start,textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        textFinal.append(requireActivity().getString(R.string.subtitle_vp_introduce_02_c))
        textFinal.append(" ")
        val end = textFinal.length

        textFinal.append(requireActivity().getString(R.string.subtitle_vp_introduce_02_d))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), end, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        return  textFinal
    }


}