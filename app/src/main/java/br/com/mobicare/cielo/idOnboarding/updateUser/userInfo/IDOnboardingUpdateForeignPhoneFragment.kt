package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingForeignPhoneUpdateBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pix.constants.WITH_COUNTRY_CODE
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingUpdateForeignPhoneFragment: BaseFragment(), CieloNavigationListener,
    IDOnboardingUpdateForeignPhoneContract.View {

    private val presenter: IDOnboardingUpdateForeignPhonePresenter by inject {
        parametersOf(this)
    }
    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingForeignPhoneUpdateBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentIdOnboardingForeignPhoneUpdateBinding
        .inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupClickListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupClickListeners() {
        textNameConfiguration()
        binding?.apply {
            btBackArrow.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvForeignPhone.doOnTextChanged { text, _, _, _ ->
                nextButtonForeignName.isEnabled = text.isNullOrEmpty().not()
            }

            nextButtonForeignName.setOnClickListener {
                presenter.sendForeignCellphone(WITH_COUNTRY_CODE + tvForeignPhone.text.toString().trim())
            }
        }
    }

    override fun successSendForeignCellphone() {
        findNavController().navigate(
            IDOnboardingUpdateForeignPhoneFragmentDirections
                .actionIdOnboardingUpdateForeignPhoneFragmentToIdOnboardingValidateP1PolicyFragment()
        )
    }

    private fun textNameConfiguration() {
        binding?.apply {
            tvForeignPhone.inputType = InputType.TYPE_CLASS_PHONE
            tvForeignPhone.requestFocus()
            requireActivity().showSoftKeyboard(tvForeignPhone)
        }
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_10_erro,
                    title = requireContext().getString(R.string.error_title_something_wrong),
                    message = requireContext().getString(R.string.id_onboarding_update_foreign_phone_error)
                        .fromHtml()
                        .toString(),
                    bt2Title = requireContext().getString(R.string.entendi),
                    bt2Callback = {
                        baseLogout()
                        false
                    },
                    closeCallback = {
                        baseLogout()
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}