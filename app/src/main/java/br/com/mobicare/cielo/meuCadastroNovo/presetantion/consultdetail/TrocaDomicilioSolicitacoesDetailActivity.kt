package br.com.mobicare.cielo.meuCadastroNovo.presetantion.consultdetail

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.extensions.formatBankName
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meuCadastroNovo.domain.DomicilioDetailEnum
import br.com.mobicare.cielo.meuCadastroNovo.domain.Item
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.PAYMENT_ACCOUNTS_DOMICILE_ITEM
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.TrocaDomicilioSolicitacoesActivity
import kotlinx.android.synthetic.main.activity_troca_domicilio_solicitacoes_detail.*
import kotlinx.android.synthetic.main.item_conta_status.view.*
import kotlinx.android.synthetic.main.item_troca_domicilio_solicitacoes_detalhes.view.*

class TrocaDomicilioSolicitacoesDetailActivity : BaseLoggedActivity() {

    private var paymanetAccount: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_troca_domicilio_solicitacoes_detail)
        intent?.let {
            paymanetAccount = it.extras?.getParcelable(PAYMENT_ACCOUNTS_DOMICILE_ITEM)
        }
        init()
    }

    private fun init() {
        setupToolbar(toolbar_domicile as Toolbar,
                getString(R.string.toolbar_title_track_detail))

        textViewBankName.text = paymanetAccount?.bankName?.formatBankName()
        textViewStatus.text = paymanetAccount?.status

        BrandCardHelper.getLoadBrandImageGeneric(paymanetAccount?.bankCode ?: "-1")
                .let { itUrl ->
                    ImageUtils.loadImage(imageViewBank, itUrl, R.drawable.bank_000)
                }
        paymanetAccount?.code?.let { changeColorStatus(it, textViewStatus) }

        paymanetAccount?.brands?.forEach { brand ->
            val viewItem = LayoutInflater
                    .from(this)
                    .inflate(R.layout.item_troca_domicilio_solicitacoes_detalhes, null)

            brand.codeBrand?.let {
                BrandCardHelper.getUrlBrandImageByCode(it)?.let { itUrl ->
                    ImageUtils.loadImage(viewItem.imageViewBrand, itUrl)
                }
            }
            brand.nameBrand?.let { viewItem.textViewBrandName.text = it }

            DomicilioDetailEnum
                    .values()
                    .find {
                        brand.statusCode == it.statusCode
                    }?.let {
                        viewItem.textViewStatusItem.text = it.statusLabel
                        viewItem.textViewStatusItem
                                .setTextColor(ContextCompat.getColor(this, it.getColor()))

                        if (brand.statusCode == DomicilioDetailEnum.CANCEL.statusCode
                                && brand.messageReason.isNullOrEmpty().not()) {
                            viewItem.imageViewInfo.visible()
                            viewItem.imageViewInfo.setOnClickListener {
                                DomicilioMessageErrorBottomSheetFragment.newInstance(brand, supportFragmentManager)
                            }
                        }
                    }
            linearLayoutItem.addView(viewItem)
        }
    }

    private fun changeColorStatus(statusCode: Int, view: AppCompatTextView) {
        when (statusCode) {
            TrocaDomicilioSolicitacoesActivity.CONCLUDED, TrocaDomicilioSolicitacoesActivity.CONCLUDED_PARTIALLY -> view.setTextColor(ContextCompat.getColor(this, R.color.color_009e55))
            TrocaDomicilioSolicitacoesActivity.ERROR, TrocaDomicilioSolicitacoesActivity.CANCEL, TrocaDomicilioSolicitacoesActivity.REJECT -> view.setTextColor(ContextCompat.getColor(this, R.color.red_EE2737))
            TrocaDomicilioSolicitacoesActivity.CHECKING, TrocaDomicilioSolicitacoesActivity.PENDING -> view.setTextColor(ContextCompat.getColor(this, R.color.color_f98f25))
            else -> view.setTextColor(ContextCompat.getColor(this, R.color.color_f98f25))
        }
    }
}