package br.com.mobicare.cielo.coil.presentation.adress

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.COILS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.FILMS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_COIL_SUCCESS_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_FILM_CONFIRM_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_FILM_REQUEST_IN_PROGRESS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_FILM_SUCCESS_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_STICKER_CONFIRM_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_STICKER_REQUEST_IN_PROGRESS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_STICKER_SUCCESS_REQUEST
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.STICKERS
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.ManagerFragment.Companion.VALUE_ARRAY
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment
import br.com.mobicare.cielo.coil.domain.MerchantAddress
import br.com.mobicare.cielo.coil.domain.MerchantBuySupply
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.coil.presentation.success.ServiceSuccessBottomSheetDialogFragment
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.LETTER_S_AND_SPACE
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.SUCCESS
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.FragmentCoilAdressBinding
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import org.koin.android.ext.android.inject

class ServiceAddressFragment : BaseFragment(), ServiceAddressContract.View {

    private var binding: FragmentCoilAdressBinding? = null

    private lateinit var actionListener: ActivityStepCoordinatorListener
    private val presenter: ServiceAddressPresenter by inject()
    private var tagService = EMPTY_STRING
    private var tagTitle = EMPTY_STRING
    private var nameSupplies: String? = EMPTY_STRING

    private val valuesArray: ArrayList<CoilOptionObj>?
        get() = arguments?.getParcelableArrayList(
            VALUE_ARRAY
        )

    private val ga4: SelfServiceAnalytics by inject()
    private val screenPath
        get() = when (tagService.normalizeToLowerSnakeCase()) {
            COILS -> SCREEN_VIEW_REQUEST_MATERIALS_COIL_CONFIRM_REQUEST
            FILMS -> SCREEN_VIEW_REQUEST_MATERIALS_FILM_CONFIRM_REQUEST
            STICKERS -> SCREEN_VIEW_REQUEST_MATERIALS_STICKER_CONFIRM_REQUEST
            else -> EMPTY_STRING
        }

    companion object {
        fun create(
            actionListener: ActivityStepCoordinatorListener,
            coilOptions: ArrayList<CoilOptionObj>,
        ): ServiceAddressFragment {
            val fragment = ServiceAddressFragment()
            bundleAdd(coilOptions, fragment)
            fragment.actionListener = actionListener
            return fragment
        }

        private fun bundleAdd(
            coilOptions: ArrayList<CoilOptionObj>,
            fragment: ServiceAddressFragment
        ) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(VALUE_ARRAY, coilOptions)
            fragment.arguments = bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCoilAdressBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configListChosen()

        presenter.setView(this)
        presenter.loadAdress()

        configButtonNext()
    }

    private fun configContainerImportant(){
        binding?.frameErrorInclude?.apply {
            containerError.background = context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.shape_border_neutral_stroke_1_radius_10
                )
            }
            ivTitle.visible()
            tvTitle.text = getString(R.string.important_title)
            tvMessage.apply {
                text = getString(R.string.important_message)
                setTextColor(
                    ContextCompat.getColor(
                    context,
                    R.color.neutral_600
                ))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.setView(this)
        logScreenView()
    }

    override fun onDestroy() {
        presenter.onCleared()
        super.onDestroy()
    }

    private fun configButtonNext() {
        actionListener.setTitle(getString(R.string.service_title_confirm_request))
        binding?.buttonNext?.apply {
            isEnabled = false
            setOnClickListener {
                if (isAttached()) {
                    tagConfirmButton()
                    isEnabled = false
                    presenter.buySupples()
                }
            }
        }
    }

    private fun tagConfirmButton() {
        tagEvent(tagService, tagTitle)
    }

    private fun tagEvent(tagService: String, tagTitle: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(tagService.toLowerCasePTBR(), tagTitle, CONFIRMACAO),
            label = listOf(Label.BOTAO, binding?.buttonNext?.text?.toString().orEmpty())
        )
    }

    private fun configListChosen() {
        valuesArray?.apply {
            presenter.setSupplies(this)

            forEach {
                if (tagService.isEmpty()) {
                    tagService = it.tagService
                    tagTitle = it.title
                }
                addItemChosen(it)
            }
        }
    }

    private fun addItemChosen(coiloption: CoilOptionObj) {
        val item_view = inflaterChosen()
        item_view?.let {
            addItemViewChosen(item_view, coiloption)
            binding?.linearItemChosen?.addView(it)
        }
    }

    private fun addItemViewChosen(item_view: View, coiloption: CoilOptionObj) {
        val text_item = item_view.findViewById<TypefaceTextView>(R.id.text_item_chosen)
        if (coiloption.quantity > ZERO) {
            var title = coiloption.title
            if (coiloption.quantity > ONE) {
                if (coiloption.code == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ICMP
                    || coiloption.code == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_D200
                    || coiloption.code == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ZIP
                    || coiloption.code == AutoAtendimentoMateriasFragment.ADESIVO_MULTIVAN
                    || coiloption.code == AutoAtendimentoMateriasFragment.ADESIVO_MULTIBANDEIRA
                    || coiloption.code == AutoAtendimentoMateriasFragment.COIL_UNIFIELD
                    || coiloption.code == AutoAtendimentoMateriasFragment.COIL_LIO
                ) {

                    val titleSplit = title.split(ONE_SPACE)
                    val sb = StringBuffer()
                    var flag = false
                    titleSplit.forEach {
                        sb.append(it)
                        if (!flag) {
                            sb.append(LETTER_S_AND_SPACE)
                            flag = true
                        } else {
                            sb.append(ONE_SPACE)
                        }
                    }

                    title = sb.toString()
                }
            }
            text_item.text = "${coiloption.quantity} ${title}"
        } else
            text_item.text = coiloption.title
    }

    private fun inflaterChosen(): View? {
        val inflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val item_view = inflater.inflate(R.layout.card_coil_chosen, null)
        return item_view
    }

    @SuppressLint("SetTextI18n")
    override fun showAddress(response: MerchantAddress) {
        if (isAttached()) {
            binding?.apply {
                with(response) {
                    textDescriptionAddress.text =
                        "$streetAddress, $number - $neighborhood - $city - $state"
                }
                buttonNext.isEnabled = true
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
                    action = listOf(Action.CALLBACK, tagService, tagTitle),
                    label = listOf(ERRO, "${it.httpStatus}")
                )
                logError(it)

                binding?.buttonNext?.isEnabled = true
                activity?.showMessage(
                    it.message,
                    it.title
                ) {
                    setBtnRight(getString(R.string.ok))
                }
                binding?.containerErrorLimit.gone()
            }
        }
    }

    override fun showSubmit(error: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                buttonNext.isEnabled = true
                buttonNext.gone()
                mainView.gone()
                includeError.root.visible()
                includeError.buttonUpdate.setOnClickListener {
                    if (isAttached()) {
                        showLoading()
                        includeError.root.gone()
                        presenter.resubmit()
                    }
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        actionListener.onLogout()
    }

    override fun showLoading() {
        super.showLoading()
        if (isAttached()) {
            binding?.apply {
                frameProgressView.visible()
                mainView.visible()
                buttonNext.visible()
                includeError.root.gone()
            }
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) binding?.frameProgressView.gone()
    }

    override fun showSucess(response: MerchantBuySupplyChosenResponse) {
        if (isAttached()) {
            if (this.tagService.isNotEmpty()) {
                response.supplies.forEach {
                    nameSupplies += it.description
                }
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
                    action = listOf(Action.CALLBACK, tagService, nameSupplies.toString()),
                    label = listOf(SUCESSO)
                )
            }
            logSuccess(response.supplies)
            val fragment =
                ServiceSuccessBottomSheetDialogFragment.create(this.tagService, response.supplies) {
                    actionListener.onNextStep(true)
                }

            fragment.show(requireFragmentManager(), "BottomSheetDialogFragment")

            binding?.containerErrorLimit.gone()
        }
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logSuccess(items: ArrayList<MerchantBuySupply>) {
        when (tagService.normalizeToLowerSnakeCase()) {
            COILS -> {
                ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_COIL_SUCCESS_REQUEST)
                ga4.logPurchaseCoil(tagTitle)
            }
            FILMS -> {
                if (verifySuccess(items)) {
                    ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_FILM_SUCCESS_REQUEST)
                    ga4.logPurchaseFilm(itemsWithSuccess(items))
                } else {
                    ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_FILM_REQUEST_IN_PROGRESS)
                }
            }
            STICKERS -> {
                if (verifySuccess(items)) {
                    ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_STICKER_SUCCESS_REQUEST)
                    ga4.logPurchaseSticker(itemsWithSuccess(items))
                } else {
                    ga4.logScreenView(SCREEN_VIEW_REQUEST_MATERIALS_STICKER_REQUEST_IN_PROGRESS)
                }
            }
        }
    }

    private fun verifySuccess(items: ArrayList<MerchantBuySupply>): Boolean {
        var isSuccess = false
        items.forEach {
            if (it.status == SUCCESS) {
                isSuccess = true
                return@forEach
            }
        }
        return isSuccess
    }

    private fun itemsWithSuccess(items: ArrayList<MerchantBuySupply>): ArrayList<CoilOptionObj> {
        return items.filter { it.status == SUCCESS }.map {
            CoilOptionObj().apply {
                quantity = it.quantity
                title = it.title
            }
        }.toCollection(ArrayList())
    }

    private fun logError(error: ErrorMessage?) = ga4.logException(screenPath, error)

}