package br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivosResumo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutListPagamentoAtivosResumoBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.invisible
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos.PagamentoLinkListAtivosContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos.PagamentoLinkListAtivosPresenter
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.LAST_ACTIVE_LINKS
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SEE_ALL_GENERATED_LINKS
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.presentation.ui.SuperLinkPaymentFragmentDirections
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class PaymentLinkListAtivosResumoFragment : BaseFragment(), PagamentoLinkListAtivosContract.View {

    private var binding: LayoutListPagamentoAtivosResumoBinding? = null

    private val presenter: PagamentoLinkListAtivosPresenter by inject {
        parametersOf(this)
    }

    private val ga4: PaymentLinkGA4 by inject()

    companion object {

        const val SCREEN_NAME = "SCREEN_NAME"
        const val CORREIOS = "CORREIOS"
        const val LOGGI = "LOGGI"
        const val FIXED_AMOUNT = "FIXED_AMOUNT"

        fun create(): PaymentLinkListAtivosResumoFragment {
            return PaymentLinkListAtivosResumoFragment()
        }

        /**
         * método para verificar se é Digital
         * */
        fun visibleImageTypeDigital(
            txt_type: TypefaceTextView,
            image_type: ImageView,
            txtTypeDelivere: TypefaceTextView,
            requireContext: Context
        ) {
            txt_type.invisible()
            image_type.gone()
            txtTypeDelivere.invisible()
        }

        /**
         * método para verificar se é Lojista
         * */
        fun visibleImageTypeLogista(
            txt_type: TypefaceTextView,
            image_type: ImageView,
            txtTypeDelivere: TypefaceTextView,
            requireContext: Context
        ) {
            txt_type.visible()
            image_type.gone()
            txtTypeDelivere.visible()
        }

        /**
         * método para verificar se é Loggi
         * */
        fun visibleImageTypeLoggi(
            txt_type: TypefaceTextView,
            image_type: ImageView,
            txtTypeDelivere: TypefaceTextView,
            requireContext: Context
        ) {
            image_type.background = ContextCompat.getDrawable(requireContext, R.drawable.ic_loggi)
            txt_type.gone()
            image_type.visible()
            txtTypeDelivere.visible()
        }

        /**
         * método para verificar se é Correio
         * */
        fun visibleImageTypeCorreio(
            txt_type: TypefaceTextView,
            image_type: ImageView,
            txtTypeDelivere: TypefaceTextView,
            requireContext: Context
        ) {
            image_type.background =
                ContextCompat.getDrawable(requireContext, R.drawable.ic_correios_inline)
            txt_type.gone()
            image_type.visible()
            txtTypeDelivere.visible()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutListPagamentoAtivosResumoBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setView(this)
        setupView()
    }

    private fun setupView() {
        binding?.apply {
            linearSeeMoreAction.linearSeeMoreContent.setOnClickListener {
                logClickButtonLastActiveLinks()
                requireActivity()
                    .findNavController(R.id.nav_host_fragment)
                    .navigate(
                        SuperLinkPaymentFragmentDirections
                            .actionLinkPaymentFragmentToPagamentoLinkListAtivosFragment2()
                    )
            }

            linearError.buttonLoanSimulationErrorRetry.setOnClickListener {
                presenter.setView(this@PaymentLinkListAtivosResumoFragment)

                linearSeeMoreAction.linearSeeMoreContent.setOnClickListener {
                    logClickButtonLastActiveLinks()
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment)
                        .navigate(
                            SuperLinkPaymentFragmentDirections
                                .actionLinkPaymentFragmentToPagamentoLinkListAtivosFragment2()
                        )
                }
            }
        }
    }

    override fun onResume() {
        gaSendScreenView()
        presenter.setView(this)
        super.onResume()
        presenter.loadListAtivosResumo()
    }

    override fun onPause() {
        super.onPause()
        presenter.onCleared()
    }

    override fun showListAtivos(itens: List<PaymentLink>) {
        if (isAttached()) {
            binding?.apply {
                linearViewAtivos.removeAllViews()
                textNotFound.gone()
                linearViewAtivos.visible()
                containerSeeMoreAction.visible(itens.size > TWO)

                val flagLine = true
                val id = 123456

                itens.take(TWO).map {
                    val itemView = inflaterLinkActive()
                    addItemViewLinkActive(itemView!!, it, flagLine, id)
                    linearViewAtivos.addView(itemView)
                }
            }
        }
    }

    override fun showLastLinks(items: List<PaymentLink>) {
        if (items.isEmpty()) {
            showEmptyLinks()
        } else {
            showListAtivos(items)
        }
    }

    override fun showEmptyLinks() {
        if (isAttached()) {
            binding?.apply {
                textNotFound.visible()
                linearError.root.gone()
                containerSeeMoreAction.gone()
                linearViewAtivos.gone()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun inflaterLinkActive(): View? {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.pagamento_link_list_item_ativos_new, null)
    }

    @SuppressLint("ResourceType")
    private fun addItemViewLinkActive(
        itemView: View,
        item: PaymentLink,
        flagLine: Boolean,
        id: Int
    ) {
        val textItemLink = itemView.findViewById<TypefaceTextView>(R.id.text_fantasy_name)
        val textItemValue = itemView.findViewById<TypefaceTextView>(R.id.text_value)
        val imageType = itemView.findViewById<ImageView>(R.id.im_type)
        val txtType = itemView.findViewById<TypefaceTextView>(R.id.txt_type)
        val txtTypeDeliver = itemView.findViewById<TypefaceTextView>(R.id.text_type_delivere)

        val constraintMain = itemView.findViewById<ConstraintLayout>(R.id.constraint_view)
        constraintMain.id = id

        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(ZERO, ZERO, ZERO, THIRTY)
        constraintMain.layoutParams = params

        textItemLink.text = item.name
        textItemValue.text = item.price?.toPtBrRealString()

        when {
            item.shipping?.type.equals(CORREIOS) -> {
                visibleImageTypeCorreio(txtType, imageType, txtTypeDeliver, requireContext())
            }
            item.shipping?.type.equals(LOGGI) -> {
                visibleImageTypeLoggi(txtType, imageType, txtTypeDeliver, requireContext())
            }
            item.shipping?.type.equals(FIXED_AMOUNT) -> {
                visibleImageTypeLogista(txtType, imageType, txtTypeDeliver, requireContext())
            }
            else -> {
                visibleImageTypeDigital(txtType, imageType, txtTypeDeliver, requireContext())
            }
        }

        RxView.clicks(constraintMain)
            .throttleFirst(TIME_CLICK, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (item.shipping?.type.equals(FIXED_AMOUNT))
                    gaSendItem("LOJISTA")
                else gaSendItem(item.shipping?.type.orEmpty())
                findNavController().navigate(
                    SuperLinkPaymentFragmentDirections.actionLinkPaymentFragmentToLinkOrdersFragment(
                        item
                    )
                )
            }, {})
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                if (isAttached()) {
                    hideLoading()
                    AlertDialogCustom.Builder(
                        requireActivity(),
                        getString(R.string.ga_esqueci_senha)
                    )
                        .setMessage(it.message)
                        .setBtnRight(getString(android.R.string.ok))
                        .setOnclickListenerRight {
                            presenter.resubmit()
                        }
                        .show()
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        msg?.let {
            if (isAttached()) {
                binding?.apply {
                    linearViewAtivos.gone()
                    containerSeeMoreAction.gone()
                    linearError.root.gone()
                    textNotFound.gone()
                    progressView.visible()

                    AlertDialogCustom.Builder(
                        requireContext(),
                        getString(R.string.home_ga_screen_name)
                    )
                        .setTitle(R.string.app_name)
                        .setMessage(it.message)
                        .setBtnRight(getString(R.string.ok))
                        .setOnclickListenerRight {
                            if (isAttached()) {
                                Utils.logout(requireActivity())
                            }
                        }
                        .show()
                }
            }
        }
    }

    override fun showSubmit(error: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                linearViewAtivos.gone()
                containerSeeMoreAction.gone()
                linearError.root.visible()
                textNotFound.gone()
                progressView.gone()
            }
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            super.showLoading()
            binding?.apply {
                linearViewAtivos.gone()
                containerSeeMoreAction.gone()
                linearError.root.gone()
                textNotFound.gone()
                progressView.visible()
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            super.hideLoading()
            binding?.apply {
                linearError.root.gone()
                progressView.gone()
            }
        }
    }

    private fun gaSendItem(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.LINK, Action.LINKS_ATIVOS),
                label = listOf(labelButton)
            )
        }
    }

    private fun gaSendScreenView() {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = "/pagamento-por-link/super-link/links-ativos",
                screenClass = this.javaClass
            )
        }
    }

    private fun logClickButtonLastActiveLinks() =
        ga4.logClickButtonHome(SEE_ALL_GENERATED_LINKS, LAST_ACTIVE_LINKS)

}