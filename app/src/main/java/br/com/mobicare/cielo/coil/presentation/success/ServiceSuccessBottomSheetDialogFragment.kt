package br.com.mobicare.cielo.coil.presentation.success

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.FIVE
import br.com.cielo.libflue.util.SEVEN
import br.com.cielo.libflue.util.FOUR
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment
import br.com.mobicare.cielo.coil.domain.MerchantBuySupply
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.LETTERS_NS_LOWER_CASE
import br.com.mobicare.cielo.commons.constants.LETTER_M_LOWER_CASE
import br.com.mobicare.cielo.commons.constants.LETTER_S_AND_SPACE
import br.com.mobicare.cielo.commons.constants.LETTER_S_AND_SPACE_LOWER_CASE
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.constants.Text.CONCLUSAO
import br.com.mobicare.cielo.commons.constants.Text.OK
import br.com.mobicare.cielo.commons.constants.Text.RESUMO
import br.com.mobicare.cielo.commons.constants.Text.SUCCESS
import br.com.mobicare.cielo.commons.constants.Text.TAG_SERVICE
import br.com.mobicare.cielo.commons.constants.Text.VALUES_ARRAY
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.utils.analytics.pipeJoin
import br.com.mobicare.cielo.databinding.FragmentServiceSuccessBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ServiceSuccessBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var binding: FragmentServiceSuccessBinding? = null
    private var click: () -> Unit = {}
    private var tagService: String? = null
    private val ORDERED = "ORDERED"
    private val NOT_ALLOWED_QUANTITY = "NOT_ALLOWED_QUANTITY"
    private val INVALID_ADDRESS = "INVALID_ADDRESS"
    private val FAILURE = "FAILURE"

    companion object {
        fun create(
            tagService: String = EMPTY,
            supplies: ArrayList<MerchantBuySupply>,
            click: () -> Unit
        ): ServiceSuccessBottomSheetDialogFragment {
            val fragment = ServiceSuccessBottomSheetDialogFragment()
            fragment.click = click
            bundleAdd(tagService, supplies, fragment)
            return fragment
        }

        private fun bundleAdd(
            tagService: String,
            supplies: ArrayList<MerchantBuySupply>,
            fragment: ServiceSuccessBottomSheetDialogFragment
        ) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(VALUES_ARRAY, supplies)
            bundle.putString(TAG_SERVICE, tagService)
            fragment.arguments = bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentServiceSuccessBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                sendTagOkButton(VOLTAR)
                click()
                dialog.dismiss()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }

        }

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ZERO
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= FOUR) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val list = it.getParcelableArrayList<MerchantBuySupply>(VALUES_ARRAY)

            var bSuccess = false
            var bFail = false

            list?.forEach {
                if (it.status == SUCCESS) {
                    bSuccess = true
                    addItenSuccess(it)
                } else {
                    bFail = true
                    addItenError(it)
                }
            }

            this.tagService = it.getString(TAG_SERVICE)
            binding?.apply {
                if (!bSuccess)
                    textItensSuccessTitle.visibility = View.GONE
                if (!bFail)
                    textItensFailTitle.visibility = View.GONE
            }

            setTextSuccessFail(bSuccess, bFail)
        }

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ZERO
        }

        binding?.apply {
            btnWelcomeClose.setOnClickListener {
                click()
                dialog?.dismiss()
            }
            buttonNext.setOnClickListener {
                sendTagOkButton(OK)
                click()
                dialog?.dismiss()
            }
        }
    }

    private fun sendTagOkButton(buttonType: String) {
        this.tagService?.let { itService ->
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, AUTOATENDIMENTO),
                action = listOf(itService, CONCLUSAO),
                label = listOf(buttonType)
            )
        }
    }

    private fun setTextSuccessFail(bSuccess: Boolean, bFail: Boolean) {

        binding?.textViewTitle?.text = getString(
            if (bSuccess && bFail) {
                R.string.service_conclusion_title_description_parcial
            } else if (bSuccess && !bFail) {
                R.string.service_conclusion_title_description_completo
            } else {
                R.string.service_conclusion_title_description_fail
            }
        )
    }

    fun addItenError(merchantBuySupply: MerchantBuySupply) {
        val item_view = inflaterError()
        item_view?.let {
            addItemViewFail(item_view, merchantBuySupply)
            binding?.linearViewFail?.addView(it)
        }
    }

    fun addItenSuccess(merchantBuySupply: MerchantBuySupply) {
        val item_view = inflaterSuccess()
        item_view?.let {
            addItemViewSuccess(item_view, merchantBuySupply)
            binding?.linearViewSuccess?.addView(it)
        }
    }

    private fun addItemViewFail(item_view: View, merchantBuySupply: MerchantBuySupply) {
        val text_item_error = item_view.findViewById<TypefaceTextView>(R.id.text_item_fail)
        if (merchantBuySupply.quantity > ZERO) {
            var title = merchantBuySupply.title
            if (merchantBuySupply.quantity > ONE) {
                if (merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ICMP
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_D200
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ZIP
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.ADESIVO_MULTIVAN
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.ADESIVO_MULTIBANDEIRA
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.COIL_UNIFIELD
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.COIL_LIO
                ) {

                    val titleSplit = title.split(ONE_SPACE)
                    val stringBuffer = StringBuffer()
                    var flag = false
                    titleSplit.forEach {
                        stringBuffer.append(it)
                        if (!flag) {
                            stringBuffer.append(LETTER_S_AND_SPACE)
                            flag = true
                        } else {
                            stringBuffer.append(ONE_SPACE)
                        }
                    }

                    title = stringBuffer.toString()
                }
            }
            text_item_error.text = "${merchantBuySupply.quantity} ${title}"
        } else
            text_item_error.text = merchantBuySupply.title

        funcFailDescription(item_view, merchantBuySupply)
    }

    private fun funcFailDescription(item_view: View, merchantBuySupply: MerchantBuySupply) {
        val text_item_error_descripion =
            item_view.findViewById<TypefaceTextView>(R.id.text_view_error_description)

        text_item_error_descripion.text = when (merchantBuySupply.status) {
            ORDERED -> {
                getString(R.string.request_in_progress)
            }

            INVALID_ADDRESS -> {
                getString(R.string.invalid_address)
            }

            else ->
                getMessageReason(
                    code = merchantBuySupply.code,
                    status = merchantBuySupply.status,
                    limitQuantity = merchantBuySupply.limitQuantity
                )
        }

        if (merchantBuySupply.status == ORDERED) {
            Analytics.trackScreenView(
                screenName = pipeJoin(Category.APP_CIELO, AUTOATENDIMENTO, RESUMO),
                screenClass = this.javaClass
            )
        }
    }

    private fun addItemViewSuccess(item_view: View, merchantBuySupply: MerchantBuySupply) {
        val text_item = item_view.findViewById<TypefaceTextView>(R.id.text_item_success)
        if (merchantBuySupply.quantity > ZERO) {
            var title = merchantBuySupply.title
            if (merchantBuySupply.quantity > ONE) {
                if (merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ICMP
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_D200
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.PELICULA_ACESSIBILIDADE_ZIP
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.ADESIVO_MULTIVAN
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.ADESIVO_MULTIBANDEIRA
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.COIL_UNIFIELD
                    || merchantBuySupply.supplyCode == AutoAtendimentoMateriasFragment.COIL_LIO
                ) {

                    val titleSplit = title.split(ONE_SPACE)
                    val stringBuffer = StringBuffer()
                    var flag = false
                    titleSplit.forEach {
                        stringBuffer.append(it)
                        if (!flag) {
                            stringBuffer.append(LETTER_S_AND_SPACE_LOWER_CASE)
                            flag = true
                        } else {
                            stringBuffer.append(ONE_SPACE)
                        }
                    }

                    title = stringBuffer.toString()
                }
            }
            text_item.text = "${merchantBuySupply.quantity} ${title}"
        } else {
            text_item.text = merchantBuySupply.title
        }
    }

    private fun inflaterError(): View? {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val item_view = inflater.inflate(R.layout.item_service_fail, null)
        return item_view
    }

    private fun inflaterSuccess(): View? {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val item_view = inflater.inflate(R.layout.item_service_success, null)
        return item_view
    }

    private fun getMessageReason(code: Int, status: String, limitQuantity: Int): String {
        return if (status == NOT_ALLOWED_QUANTITY) {
            if (code == SEVEN) {
                getString(R.string.no_material_for_place)
            } else {
                if (limitQuantity > ZERO) {
                    String.format(
                        getString(R.string.not_allowed_quantity),
                        limitQuantity,
                        getEndPlural(limitQuantity)
                    )
                } else {
                    getString(R.string.not_allowed)
                }
            }
        } else {
            if (code == FIVE) {
                getString(R.string.material_already_requested)
            } else {
                getString(R.string.commons_generic_error_message)
            }
        }
    }

    private fun getEndPlural(limitQuantity: Int): String {
        return if (limitQuantity > ONE) {
            LETTERS_NS_LOWER_CASE
        } else {
            LETTER_M_LOWER_CASE
        }
    }
}