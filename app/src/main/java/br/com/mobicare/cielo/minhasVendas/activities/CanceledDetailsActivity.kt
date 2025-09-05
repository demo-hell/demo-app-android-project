package br.com.mobicare.cielo.minhasVendas.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.ActivityCanceledDetailsBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.minhasVendas.enums.CanceledSalesStatusEnum
import kotlinx.android.synthetic.main.activity_canceled_details.*
import kotlinx.android.synthetic.main.layout_canceled_sale_details_request_data.*
import kotlinx.android.synthetic.main.layout_canceled_sale_details_sale_data.*

class CanceledDetailsActivity : BaseActivity() {

    companion object {
        const val CANCELED_SALE_ARGS = "CANCELED_SALE_ARGS"
    }

    private val canceledSell: CanceledSale? by lazy {
        intent?.getParcelableExtra(CANCELED_SALE_ARGS)
    }

    private var binding: ActivityCanceledDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCanceledDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupToolbar(
            toolbarCanceledDetails as Toolbar,
            getString(R.string.text_title_canceled_details)
        )
        setupView()
        gaScreenView()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun setupView() {
        canceledSell?.let {
            binding?.apply {
                txtRequestDateValue.text = it.date?.dateFormatToBr()
                txtRequestChannelValue.text = it.channel
                it.situation?.let { situation ->
                    txtRequestSituationLabel.visible()
                    txtRequestSituationValue.visible()
                    txtRequestSituationValue.text = situation
                }
                txtSaleDateValue.text = it.saleDate?.dateFormatToBr()
                txtSaleTypePaymentValue.text = it.paymentType
                txtSaleAmountValue.text = it.saleAmount?.toPtBrRealString()
                txtSaleRefundValue.text = it.refundAmount?.toPtBrRealString()
                txtMerchantNumberValue.text = it.merchantId
                txtAuthorizationCodeValue.text = it.authorizationCode
                txtNSUValue.text = it.nsu
                txtCardBrandValue.text = it.cardBrand?.lowercase().capitalizePTBR()
                val status = it.status?.lowercase().capitalizePTBR()
                txtCanceledStatusValue.text = status
                when (status) {
                    CanceledSalesStatusEnum.REVIEW.value -> {
                        txtCanceledStatusValue.setTextColor(
                            ContextCompat.getColor(
                                baseContext,
                                CanceledSalesStatusEnum.REVIEW.color
                            )
                        )
                        txtCanceledSaleDetailsObs.visible()
                        txtCanceledSaleDetailsObs.text =
                            getString(R.string.text_canceled_sale_obs_review)
                    }
                    CanceledSalesStatusEnum.EFFECTIVE.value -> txtCanceledStatusValue.setTextColor(
                        ContextCompat.getColor(
                            baseContext,
                            CanceledSalesStatusEnum.EFFECTIVE.color
                        )
                    )
                    CanceledSalesStatusEnum.REVERSED.value -> {
                        txtCanceledStatusValue.setTextColor(
                            ContextCompat.getColor(
                                baseContext,
                                CanceledSalesStatusEnum.REVERSED.color
                            )
                        )
                        txtCanceledSaleDetailsObs.visible()
                        txtCanceledSaleDetailsObs.text =
                            getString(R.string.text_canceled_sale_obs_reversed)
                    }
                    CanceledSalesStatusEnum.REJECTED.value ->
                        txtCanceledStatusValue.setTextColor(
                            ContextCompat.getColor(
                                baseContext,
                                CanceledSalesStatusEnum.REJECTED.color
                            )
                        )
                    else -> txtCanceledStatusValue.setTextColor(
                        ContextCompat.getColor(
                            baseContext,
                            R.color.color_cloud_600
                        )
                    )
                }
            }
        }
    }

    private fun gaScreenView() {
        Analytics.trackScreenView(
            screenName = SCREENVIEW_VENDA_JA_CANCELADA_DETALHES,
            screenClass = javaClass
        )
    }

}