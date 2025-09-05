package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_conta_digital_step03.*
import org.jetbrains.anko.startActivity

class ContaDigitalCatenoPageTypeFragment03 : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_conta_digital_step03, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){
        txtStep03Subtitle.text = configureSubtitle()
        txtStep03Subtitle.setOnClickListener {
            startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_CONTA_DIGITAL)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = ContaDigitalCatenoPageTypeFragment03()
    }

    private fun configureSubtitle(): SpannableStringBuilder {
        val text = SpannableStringBuilder()

        text.append(getString(R.string.conta_digital_step03_subtitle_01)
            .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Paragraph_300_display_400)))
        text.append(" ")

        text.append(getString(R.string.conta_digital_step03_subtitle_02)
            .addSpannable(TextAppearanceSpan(requireActivity(), R.style.Paragraph_300_display_400_bs)))

        return text
    }

    fun startHelpCenter(tagKey: String) {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to tagKey,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.txt_name_bank),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true)
    }

}