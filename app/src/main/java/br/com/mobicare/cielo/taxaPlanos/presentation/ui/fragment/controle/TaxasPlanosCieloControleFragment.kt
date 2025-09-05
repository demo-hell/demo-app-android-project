package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.controle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.AnimateProgressBarHelper
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.fragment_taxas_planos_cielo_controle.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class TaxasPlanosCieloControleFragment : BaseFragment(), TaxasPlanosCieloControleContract.View {

    val presenter: TaxasPlanosCieloControlePresenter by inject {
        parametersOf(this)
    }

    var onLogoutListener: LogoutListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_taxas_planos_cielo_controle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViews()
        configureListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutListener) {
            this.onLogoutListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.presenter?.onDestroy()
        this.onLogoutListener = null
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            this.presenter.load()
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            include_error.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
            shimmerLoading.visibility = View.GONE
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            this.onLogoutListener?.onLogout()
        }
    }

    private fun configureViews() {
        val defaultValue = "R$ 0,00"
        this.pbView.progress = 0
        this.pbView.max = 100
        this.valorFaturamentoAtualText.text = defaultValue
        this.valorFranquiaContratadaText.text = defaultValue
        this.valorFaturamentExcedenteText.text = defaultValue
        this.valorTarifaExcedenteText.text = defaultValue
        this.context?.let { itContext ->
            this.valorFaturamentoAtualText.setTextColor(ContextCompat.getColor(itContext, R.color.color_009e55))
            this.pbView.progressDrawable = ContextCompat.getDrawable(itContext, R.drawable.progressbar_green_drawable)
        }
    }

    private fun configureListeners() {
        this.buttonUpdate?.setOnClickListener {
            this.presenter.load()
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            include_error.visibility = View.GONE
            contentLayout.visibility = View.GONE
            shimmerLoading.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            shimmerLoading.visibility = View.GONE
            include_error.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }

    override fun onLogout() {
    }

    override fun onError(error: ErrorMessage) {
        showError(error)
    }

    override fun showData(taxaPlanosDetail: TaxaPlanosDetailsResponse) {
        val locale = Locale("pt", "BR")
        val df = NumberFormat.getNumberInstance(locale) as DecimalFormat
        df.applyPattern("#,##0.00")

        this.valorFranquiaContratadaText?.setText("R$ ${df.format(taxaPlanosDetail.maximumAllowedRevenue)}")
        this.valorFaturamentExcedenteText?.setText("R$ ${df.format(taxaPlanosDetail.exceedingRevenue)}")
        this.valorTarifaExcedenteText?.setText("R$ ${df.format(taxaPlanosDetail.exceedingRevenueFee)}")

        AnimateProgressBarHelper.setLegacyAnimationProgressBar(this.pbView, taxaPlanosDetail.totalRevenue, taxaPlanosDetail.maximumAllowedRevenue.toInt(), {
            this.valorFaturamentoAtualText?.text = "R$ ${df.format(it.toFloat())}"
        }, {
            this.valorFaturamentoAtualText?.text = "R$ ${df.format(taxaPlanosDetail.totalRevenue)}"
            if (taxaPlanosDetail.totalRevenue >= taxaPlanosDetail.maximumAllowedRevenue) {
                this.context?.let { itContext ->
                    this.pbView.progressDrawable = ContextCompat.getDrawable(itContext, R.drawable.progressbar_orange_drawable)
                    this.valorFaturamentoAtualText?.setTextColor(ContextCompat.getColor(itContext, R.color.color_f98f25))
                }
            }
        })

    }

    companion object {
        fun create(): TaxasPlanosCieloControleFragment {
            return TaxasPlanosCieloControleFragment()
        }
    }

}