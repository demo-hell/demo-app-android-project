package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.phone
import kotlinx.android.synthetic.main.fragment_ombudsman_personal_data.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class OmbudsmanPersonalDataFragment : BaseFragment(), CieloNavigationListener, OmbudsmanPersonalDataContract.View {

    val presenter: OmbudsmanPersonalDataPresenter by inject {
        parametersOf(this)
    }
    private var navigation: CieloNavigation? = null
    private var responsible: String? = null
    private var ec: String? = null
    private var email: String? = null
    private var phone: String? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? =
            inflater.inflate(R.layout.fragment_ombudsman_personal_data, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
        presenter.onLoadPersonalData()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_ombudsman))
            navigation?.setTextButton(getString(R.string.continuar))
            navigation?.showButton(true)
            navigation?.enableButton(false)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onButtonClicked(labelButton: String) {
        val ombudsman = presenter.onCreateObject(
                userName = responsible,
                ec = ec,
                email = email,
                phone = phone)

        findNavController().navigate(
                OmbudsmanPersonalDataFragmentDirections.actionOmbudsmanPersonalDataFragmentToOmbudsmanMessageFragment(ombudsman)
        )
    }

    override fun onShowPersonalData(ombudsman: OmbudsmanRequest?) {
        ombudsman?.let { userData ->
            setText(edit_text_name_responsible_ombudsman, userData.contactPerson)
            setText(edit_text_number_ec_ombudsman, userData.merchant)

            if (ValidationUtils.isEmail(userData.email))
                setText(edit_text_email_ombudsman, userData.email)

            val phone = userData.phone?.phone()
            if (ValidationUtils.isValidPhoneNumber(phone))
                setText(edit_text_phone_ombudsman, phone)
            else
                setMaskPhone()

        } ?: run {
            setMaskPhone()
        }

        enableButton()
    }

    private fun setMaskPhone() {
        EditTextHelper.phoneField(editTextField = edit_text_phone_ombudsman,
                phoneMask = PHONE_MASK_FORMAT
        )
    }

    private fun setupListeners() {
        edit_text_email_ombudsman?.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let { email ->
                        val validate = email.trim().isNullOrEmpty().not()
                                && ValidationUtils.isEmail(email.toString())
                        setupError(validate, edit_text_email_ombudsman, error_email_ombudsman)
                    }
                }
        )

        edit_text_phone_ombudsman?.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let { phone ->
                        val validate = phone.trim().isNullOrEmpty().not()
                                && ValidationUtils.isValidPhoneNumber(phone.toString())
                        setupError(validate, edit_text_phone_ombudsman, error_phone_ombudsman)
                    }
                }
        )

        edit_text_name_responsible_ombudsman?.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let { responsible ->
                        val validate = responsible.trim().isNullOrEmpty().not()
                        setupError(validate, edit_text_name_responsible_ombudsman, error_name_responsible_ombudsman)
                    }
                }
        )

        edit_text_number_ec_ombudsman?.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let { ec ->
                        val validate = ec.trim().isNullOrEmpty().not()
                        setupError(validate, edit_text_number_ec_ombudsman, error_number_ec_ombudsman)
                    }
                }
        )
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

    private fun setText(editText: EditText?, information: String?) {
        if (information?.isNotEmpty() == true) {
            editText?.setText(information)
            editText?.isEnabled = false
        }
    }

    private fun enableButton() {
        responsible = edit_text_name_responsible_ombudsman?.text?.toString()
        ec = edit_text_number_ec_ombudsman?.text?.trim().toString()
        email = edit_text_email_ombudsman?.text?.trim().toString()
        phone = edit_text_phone_ombudsman?.text?.trim().toString()

        val validate = (responsible.isNullOrEmpty().not()
                && ec.isNullOrEmpty().not()
                && ValidationUtils.isEmail(email)
                && ValidationUtils.isValidPhoneNumber(phone))

        navigation?.enableButton(validate)
    }

    private fun trackScreenView(){
        if (isAttached()) {
            GA4.logScreenView(TechnicalSupportAnalytics.ScreenView.HELP_CENTER_OMBUDSMAN_DATA)
        }
    }
}