package br.com.mobicare.cielo.solesp.ui.infoSend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOURTEEN
import br.com.mobicare.cielo.commons.helpers.CieloTextInputViewHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.databinding.FragmentSolespInfoSendBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.solesp.constants.SOLESP_MODEL_ARGS
import br.com.mobicare.cielo.solesp.model.SolespModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SolespInfoSendFragment : BaseFragment(), CieloNavigationListener,
    SolespInfoSendContract.View {

    private val presenter: SolespInfoSendPresenter by inject {
        parametersOf(this)
    }

    private val solespModel: SolespModel? by lazy {
        arguments?.getParcelable(SOLESP_MODEL_ARGS)
    }

    private var binding: FragmentSolespInfoSendBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSolespInfoSendBinding.inflate(
            inflater,
            container,
            false
        ).also { binding = it }.root
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
        setup()
        setupNavigation()
        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onButtonClicked(labelButton: String) {
        if (validateForm())
            sendRequest()
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
    }

    override fun showSuccess() {
        navigation?.showSuccess()
    }

    override fun showError() {
        navigation?.showError { sendRequest() }
    }

    private fun setup() {
        binding?.apply {
            itNumberEc.txtHint.text = getString(R.string.txt_label_number_ec_solesp_info_send)
            itNumberEc.txtValue.text = presenter.getNumberEc()
            itName.txtHint.text = getString(R.string.txt_label_name_solesp_info_send)
            itName.txtValue.text = presenter.getUserName()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.enviar_solicitacao))
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
            enableButtonSend()
        }
    }

    private fun setupListeners() {
        binding?.apply {
            CieloTextInputViewHelper.phoneInput(
                inputText = itPhoneNumber,
                phoneMask = PHONE_MASK_FORMAT,
                textChangedComplement = {
                    enableButtonSend()
                }
            )
            CieloTextInputViewHelper.emailInput(
                inputText = itEmail,
                textChangedComplement = {
                    enableButtonSend()
                }
            )
        }
    }

    private fun enableButtonSend() {
        val email = binding?.itEmail?.getText() ?: EMPTY
        val phone = binding?.itPhoneNumber?.getText() ?: EMPTY
        navigation?.enableButton(email.isNotBlank() && phone.length >= FOURTEEN)
    }

    private fun validateForm(): Boolean {
        val email = binding?.itEmail?.getText() ?: EMPTY
        val phone = binding?.itPhoneNumber?.getText() ?: EMPTY
        val checkEmail = emailIsValid(email)
        val checkPhone = phoneIsValid(phone)
        return checkEmail && checkPhone
    }

    private fun emailIsValid(email: String): Boolean {
        if (email.isBlank() || ValidationUtils.isEmail(email).not()) {
            binding?.itEmail?.setError(getString(R.string.txt_error_input_email_solesp_info_send))
            return false
        }
        return true
    }

    private fun phoneIsValid(phone: String): Boolean {
        if (phone.isBlank() || ValidationUtils.isValidPhoneNumber(phone).not()) {
            binding?.itPhoneNumber?.setError(getString(R.string.txt_error_input_number_phone_solesp_info_send))
            return false
        }
        return true
    }

    private fun sendRequest() {
        presenter.sendSolespRequest(
            SolespModel(
                solespModel?.typeSelected,
                solespModel?.periodSelected,
                solespModel?.startDate,
                solespModel?.endDate,
                binding?.itEmail?.getText() ?: EMPTY,
                binding?.itPhoneNumber?.getText() ?: EMPTY
            )
        )
    }

}