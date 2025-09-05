package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import android.app.Dialog
import android.os.Bundle
import android.view.*
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands


class CardBrandFeeDetailsDialogFragment : androidx.fragment.app.DialogFragment(),
        CardBrandExpandedDetailsFragment.OnCloseActionListener {


    companion object {
        const val CARD_BRAND_KEY = "br.com.cielo.cieloAtendimento.cardBrand"

        fun create(cardBrand: CardBrands) = CardBrandFeeDetailsDialogFragment()
                .apply {
                    arguments = Bundle().apply {
                        putSerializable(CARD_BRAND_KEY, cardBrand)
                    }
                }
    }


    private val cardBrand: CardBrands
        get() {
            return arguments?.getSerializable(CARD_BRAND_KEY) as CardBrands
        }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_card_brand_fee_details,
                container, false)
    }


    override fun onResume() {
        val params = dialog?.window?.attributes
        // Assign window properties to fill the parent
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT

        dialog?.window?.attributes = params

        setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen)
        super.onResume()
    }

    override fun onStart() {
        super.onStart()

        val fragmentTransaction = childFragmentManager.beginTransaction()

        val cardExpandedFragment: CardBrandExpandedDetailsFragment =
                CardBrandExpandedDetailsFragment.create(cardBrand)
                        as CardBrandExpandedDetailsFragment
        cardExpandedFragment.closeActionListener = this

        fragmentTransaction.replace(R.id.frameCardBrandDetail,
                cardExpandedFragment)

        fragmentTransaction.commit()

        Analytics.trackScreenView(
            screenName = currentePath(),
            screenClass = this.javaClass
        )
    }

    private fun currentePath() = "Inicio/MeuCadastro/BandeirasDetalhesModal"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        return dialog
    }

    override fun close() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPBUTTON),
            action = listOf(currentePath()),
            label = listOf(FECHAR)
        )
        dialog?.dismiss()
    }
}