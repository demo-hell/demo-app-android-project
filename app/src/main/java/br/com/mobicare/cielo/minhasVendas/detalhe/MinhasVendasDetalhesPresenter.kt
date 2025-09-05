package br.com.mobicare.cielo.minhasVendas.detalhe

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.commons.constants.Text.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.Text.PIX_CARDBRAND_CODE
import br.com.mobicare.cielo.commons.constants.Text.X
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ItemDetalhesVendas
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDateTime
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.minhasVendas.constants.*
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse

class MinhasVendasDetalhesPresenter(
        private val view: MinhasVendasDetalhesContract.View,
        private val repository: ChangeEcRepository) : MinhasVendasDetalhesContract.Presenter {

    lateinit var sale: Sale

    override fun load(sale: Sale, fingerprint: String) {
        this.sale = sale
        this.populateFields()
        this.verificationStatusSales()
        this.view.populateNote(sale)
        this.verifyMerchant(fingerprint)
    }

    private fun populateFields() {
        val listSystemMessagem = ArrayList<ItemDetalhesVendas>()

        sale.date?.let {
            listSystemMessagem.add(ItemDetalhesVendas( SALE_DATE, it?.convertTimeStampToDateTime(true)))
        }
        sale.authorizationDate?.let {
            listSystemMessagem.add(ItemDetalhesVendas(AUTHORIZATION_DATE, it?.convertTimeStampToDate()))
        }

        if(sale.cardBrandCode == PIX_CARDBRAND_CODE) {
            sale.transactionPixId?.let { pixId -> listSystemMessagem.add(ItemDetalhesVendas(ID,pixId)) }
        }else{
            sale.id?.let { listSystemMessagem.add(ItemDetalhesVendas(ID,it)) }
        }

        sale.transactionId?.let { listSystemMessagem.add(ItemDetalhesVendas( TID, it)) }
        sale.tid?.let {
            if (sale.transactionId.isNullOrEmpty()) {
                listSystemMessagem.add(ItemDetalhesVendas(TID, it))
            }
        }

        sale.nsu?.let { listSystemMessagem.add(ItemDetalhesVendas(NSU_DOC,it)) }

        sale.cardBrandDescription?.let { listSystemMessagem.add(ItemDetalhesVendas(FLAG, it)) }
        sale.cardBrand?.let {
            if (sale.cardBrandDescription.isNullOrEmpty()) {
                listSystemMessagem.add(ItemDetalhesVendas(FLAG, it))
            }
        }
        sale.grossAmount?.let { listSystemMessagem.add((ItemDetalhesVendas(SALE_VALUE, it?.toPtBrRealString()))) }
        sale.amount?.let { listSystemMessagem.add(ItemDetalhesVendas(SALE_VALUE, it?.toPtBrRealString())) }
        sale.netAmount?.let { listSystemMessagem.add(ItemDetalhesVendas(NET_VALUE, it?.toPtBrRealString())) }
        sale.administrationFee?.let { listSystemMessagem.add(ItemDetalhesVendas(RATE, "${it} %")) }
        sale.paymentType?.let {
            if (sale.installments != null && sale.installments!! > 0)
                listSystemMessagem.add(ItemDetalhesVendas(PAYMENT_METHODS, it + ONE_SPACE + sale.installments + X))
            else
                listSystemMessagem.add(ItemDetalhesVendas(PAYMENT_METHODS, it))

        }
        sale.paymentScheduleDate?.let {
                listSystemMessagem.add(ItemDetalhesVendas(PAYMENT_FORECAST, it?.convertTimeStampToDate()))
        }
        sale.channel?.let { listSystemMessagem.add(ItemDetalhesVendas(SALES_CHANNEL, it)) }

        sale.paymentSolutionType?.let { listSystemMessagem.add(ItemDetalhesVendas(CAPTURE_TYPE,it)) }


        this.view.populateDetail(listSystemMessagem)
    }

    private fun verificationStatusSales() {
        sale.statusCode?.let {
            val saleStatusCapitalize = sale.status?.lowercase().capitalizePTBR()
            when (it) {
                ExtratoStatusDef.APROVADA -> {
                    view.loadStatus(saleStatusCapitalize, R.color.success_400)
                    view.setupCancelSale(isShow = true, isEnabled = true)
                }
                ExtratoStatusDef.NEGADA -> {
                    view.loadStatus(saleStatusCapitalize, R.color.red)
                    view.setupCancelSale(isShow = false, isEnabled = false)
                }
                ExtratoStatusDef.ATUALIZAR -> {
                    view.loadStatus(saleStatusCapitalize, R.color.purple)
                    view.setupCancelSale(isShow = true, isEnabled = false)
                }
                else -> view.loadStatus(saleStatusCapitalize, R.color.gray_light)
            }
        }
    }

    private fun verifyMerchant(fingerprint: String) {
        this.sale.merchantId?.let { itMerchantId ->
            MenuPreference.instance.getEstablishment()?.let {
                if (it.ec != itMerchantId) {
                    this.sale.paymentNode?.let { itPaymentNode ->
                        impersonateToMerchantId(itPaymentNode.toString(),fingerprint)
                    }
                } else {
                    loadTempMerchantData(itMerchantId)
                }
            }
        } ?: loadMerchantWithDefaultToken()

    }

    private fun impersonateToMerchantId(id: String,fingerprint: String) {
        UserPreferences.getInstance().token?.let { itToken ->
            this.repository.impersonate(id, itToken, ChangeEcRepository.HierarchyType.NODE, object : APICallbackDefault<Impersonate, String> {
                override fun onError(error: ErrorMessage) {
                    this@MinhasVendasDetalhesPresenter.view.onError()
                }

                override fun onSuccess(response: Impersonate) {
                    response.accessToken?.let { itResponseToken ->
                        loadTempMerchantData(itResponseToken)
                    }
                }
            },fingerprint)
        }
    }

    private fun loadMerchantWithDefaultToken() {
        UserPreferences.getInstance().token?.let { itToken ->
            loadTempMerchantData(itToken)
        }
    }

    private fun loadTempMerchantData(token: String) {
        this.repository.loadMerchant(token, object : APICallbackDefault<UserOwnerResponse, String> {
            override fun onError(error: ErrorMessage) {
                this@MinhasVendasDetalhesPresenter.view.onError()
            }

            override fun onSuccess(response: UserOwnerResponse) {
                this@MinhasVendasDetalhesPresenter.view.merchantResponse(response)
            }
        })
    }

}