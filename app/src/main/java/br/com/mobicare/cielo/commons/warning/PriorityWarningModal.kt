package br.com.mobicare.cielo.commons.warning

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import kotlinx.android.synthetic.main.warning_modal.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val MODAL_WIDTH = 0.85
const val ARG_FEATURE_TOGGLE_MODAL = "ARG_FEATURE_TOGGLE_MODAL"

class PriorityWarningModal : DialogFragment() {

    private val presenter: WarningModalPresenter by inject {
        parametersOf(this)
    }
    private val modal: FeatureToggleModal? by lazy {
        arguments?.getParcelable(ARG_FEATURE_TOGGLE_MODAL)
    }

    private val analytics: WarningAnalytics by inject()

    private var listener: WarningModalContract.View? = null
    private var isClickOut = true

    companion object {
        fun create(
            modal: FeatureToggleModal,
            listener: WarningModalContract.View? = null
        ) = PriorityWarningModal().apply {
            this.listener = listener
            arguments = Bundle().apply {
                putParcelable(ARG_FEATURE_TOGGLE_MODAL, modal)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.warning_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics.logShowModal(modal?.name)
        customModal()
    }

    override fun onStart() {
        super.onStart()
        setupDialog()
    }

    override fun onResume() {
        super.onResume()
        isClickOut = true
    }

    private fun setupDialog() {
        val width = (resources.displayMetrics.widthPixels * MODAL_WIDTH).toInt()

        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_warning_modal)
    }

    private fun customModal() {
        modal?.let { itModal ->
            if (itModal.actionTitle.isNullOrEmpty())
                btn_warning?.gone()
            else
                btn_warning?.setText(itModal.actionTitle)

            if (itModal.imageUrl.isNullOrEmpty())
                iv_warning?.gone()
            else
                ImageUtils.loadImage(iv_warning, itModal.imageUrl, true)

            tv_title_warning?.text = itModal.title
            tv_subtitle_warning?.text = itModal.message
            setupListeners(itModal)
        }
    }

    private fun setupListeners(modal: FeatureToggleModal) {
        btn_warning?.setOnClickListener {
            isClickOut = false
            analytics.logClickMainAction(modal.name)
            presenter.onSaveUserInteraction(modal)
            if (modal.external) {
                modal.actionUrl?.let { url ->
                    saveDataUser(modal)
                    Utils.openLink(requireActivity(), url)
                } ?: run {
                    saveDataUser(modal)
                }
            } else
                saveDataUser(modal)
        }

        iv_close_warning?.setOnClickListener {
            isClickOut = false
            analytics.logCloseModal(modal.name)
            saveDataUser(modal)
        }

    }

    private fun saveDataUser(modal: FeatureToggleModal) {
        presenter.onSaveUserInteraction(modal)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isClickOut) analytics.logCloseModal(modal?.name)
        listener?.onShowOtherWarning()
    }
}