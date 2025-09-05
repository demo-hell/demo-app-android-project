package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_OMBUDSMAN_MESSAGE
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_OMBUDSMAN_SUCCESS
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanResponse
import br.com.mobicare.cielo.commons.constants.HelpCenter.ARGUMENT_OMBUDSMAN
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.moveToHome
import kotlinx.android.synthetic.main.fragment_ombudsman_message.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class OmbudsmanMessageFragment : BaseFragment(), CieloNavigationListener,
    OmbudsmanMessageContract.View {

    val presenter: OmbudsmanMessagePresenter by inject {
        parametersOf(this)
    }
    private val ombudsman: OmbudsmanRequest? by lazy {
        arguments?.getParcelable(ARGUMENT_OMBUDSMAN)
    }
    private var navigation: CieloNavigation? = null
    private var subject: String? = null
    private var protocol: String? = null
    private var message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? =
        inflater.inflate(R.layout.fragment_ombudsman_message, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_ombudsman))
            navigation?.setTextButton(getString(R.string.confirmar))
            navigation?.showButton(true)
            navigation?.enableButton(false)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        trackScreenView(HELP_CENTER_OMBUDSMAN_MESSAGE)
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onButtonClicked(labelButton: String) {
        presenter.onSendProtocol(ombudsman, subject, protocol, message)
    }

    override fun showError(error: ErrorMessage?) {
        trackError(
            errorCode = error?.code.orEmpty(),
            errorMessage = error?.message.orEmpty(),
        )
        navigation?.showErrorBottomSheet(textButton = getString(R.string.ok), error = error)
    }

    override fun onSuccess(response: OmbudsmanResponse) {
        trackScreenView(HELP_CENTER_OMBUDSMAN_SUCCESS)
        val message = if (response.date.isNullOrEmpty()) {
            "${getString(R.string.text_title_bottom_sheet_success)} " +
                    getString(R.string.text_subtitle_bottom_sheet_success)

        } else {
            val clearDate = response.date.clearDate()
            val dateFormat = clearDate.formatterDate(LONG_TIME_NO_UTC)
            var hour = clearDate.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC)
            if (dateFormat == clearDate) hour = ""
            val date = getString(R.string.text_data_bottom_sheet_success, dateFormat, hour)
            "${getString(R.string.text_title_bottom_sheet_success)} $date " +
                    getString(R.string.text_subtitle_bottom_sheet_success)
        }

        bottomSheetGenericFlui(
            nameTopBar = "",
            R.drawable.ic_08,
            getString(R.string.text_toolbar_ombudsman),
            message,
            getString(R.string.text_btn_bottom_sheet_success),
            getString(R.string.text_btn_bottom_sheet_success),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                    requireActivity().moveToHome()
                }

                override fun onSwipeClosed() {
                    dismiss()
                    requireActivity().moveToHome()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun setupListeners() {
        edit_text_protocol_ombudsman_message?.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                s?.let { protocol ->
                    this.protocol = protocol.trim().toString()
                    setupError(
                        this.protocol.isNullOrEmpty().not(),
                        edit_text_protocol_ombudsman_message,
                        error_protocol_ombudsman_message
                    )
                }
            }
        )

        tv_message_ombudsman_message?.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                s?.let { message ->
                    this.message = message.trim().toString()
                    setupError(
                        this.message.isNullOrEmpty().not(),
                        container_message_ombudsman_message,
                        error_message_ombudsman_message
                    )
                }
            }
        )

        container_subject_ombudsman_message?.setOnClickListener {
            selectSubject()
        }
    }

    private fun setupError(isSuccess: Boolean, view: View?, error: TextView?) {
        if (isSuccess) {
            view?.setBackgroundResource(R.drawable.custom_edit_text)
            error?.gone()
        } else {
            view?.setBackgroundResource(R.drawable.background_error_dc392a)
            error?.visible()
        }

        enableButton()
    }

    private fun enableButton() {
        protocol = edit_text_protocol_ombudsman_message?.text?.trim().toString()
        message = tv_message_ombudsman_message?.text?.trim().toString()
        subject = tv_subject_ombudsman_message?.text?.trim().toString()

        val validate = (protocol.isNullOrBlank().not()
                && message.isNullOrBlank().not()
                && subject.isNullOrBlank().not())

        navigation?.enableButton(validate)
    }

    private fun selectSubject() {
        val subjects: ArrayList<String> = arrayListOf(
            getString(R.string.text_list_subject_suggestion),
            getString(R.string.text_list_subject_praise),
            getString(R.string.text_list_subject_complaint)
        )
        SelectorBottomSheet.create(items = subjects,
            title = getString(R.string.text_title_subject_ombudsman_message),
            result = {
                subject = it
                tv_subject_ombudsman_message?.text = subject
            }
        ).show(childFragmentManager, SelectorBottomSheet::class.java.simpleName)
    }

    private fun trackScreenView(screenName: String){
        if (isAttached()) {
            GA4.logScreenView(screenName)
        }
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER_OMBUDSMAN_MESSAGE, errorCode, errorMessage)
        }
    }
}