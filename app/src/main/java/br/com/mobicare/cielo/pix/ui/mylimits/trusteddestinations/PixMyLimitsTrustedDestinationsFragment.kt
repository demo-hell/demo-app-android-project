package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.keyTypes
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.FragmentPixMyLimitsTrustedDestinationsBinding
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.PIX_MY_LIMITS_IS_HOME_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_SHOW_DIALOG_ADD_NEW_RELIABLE_CONTACT_ARGS
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.model.ListKeyType
import br.com.mobicare.cielo.pix.ui.mylimits.PixMyLimitsNavigationFlowActivity
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.adapter.PixMyLimitsTrustedDestinationsAdapter
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorContract
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixMyLimitsTrustedDestinationsFragment : BaseFragment(), CieloNavigationListener,
    PixMyLimitsTrustedDestinationsContract.View {

    private val presenter: PixMyLimitsTrustedDestinationsPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentPixMyLimitsTrustedDestinationsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixMyLimitsTrustedDestinationsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        onClickListenerNewTrustedDestination()
        showDialogAddNewReliableContact()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.getTrustedDestinations()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_pix_my_limits_account_registration))
            navigation?.showContainerButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun hideView() {
        binding?.containerWithoutMainTrusted?.gone()
        binding?.rvTrustedDestination?.gone()
    }

    private fun onClickListenerNewTrustedDestination() {
        binding?.containerAddTrusted?.setOnClickListener {
            PixSelectorBottomSheet.onCreate(
                object : PixSelectorContract.Result {
                    override fun onShowKeyTypeSelected(keyType: PixKeyTypeEnum) {
                        if (keyType == PixKeyTypeEnum.ACCOUNT)
                            goToSelectBank()
                        else
                            goToInsertKey(keyType)
                    }
                },
                ListKeyType(keyTypes(requireContext(), isEVP = true, isBranch = true)),
                getString(R.string.pix_text_my_limits_add_new_trusted_destination_options)
            ).show(childFragmentManager, tag)
        }
    }

    private fun goToSelectBank() {
        findNavController().navigate(
            PixMyLimitsTrustedDestinationsFragmentDirections.actionPixMyLimitsTrustedDestinationsFragmentToPixSelectBankFragment(
                true,
                DEFAULT_BALANCE
            )
        )
    }

    private fun goToInsertKey(keyType: PixKeyTypeEnum) {
        findNavController().navigate(
            PixMyLimitsTrustedDestinationsFragmentDirections.actionPixMyLimitsTrustedDestinationsFragmentToPixInsertKeyFragment(
                keyType,
                DEFAULT_BALANCE,
                true
            )
        )
    }

    private fun goToHomeMyLimits() {
        requireActivity().finish()
        requireActivity().startActivity<PixMyLimitsNavigationFlowActivity>(
            PIX_MY_LIMITS_IS_HOME_ARGS to true
        )
    }

    override fun showLoading() {
        navigation?.showLoading(true)
        hideView()
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_try_again_label),
            error = processErrorMessage(
                error,
                getString(R.string.business_error),
                getString(R.string.text_pix_error_try_again)
            ),
            title = getString(R.string.pix_text_my_limits_trusted_error_title),
            isFullScreen = false
        )
    }

    private fun showDialogAddNewReliableContact(){
        val isShowDialogAddNewReliableContact = arguments?.getBoolean(PIX_SHOW_DIALOG_ADD_NEW_RELIABLE_CONTACT_ARGS, false) ?: false
        if(isShowDialogAddNewReliableContact) {
            CieloDialog.create(
                getString(R.string.pix_title_dialog_alert_add_new_reliable_contact),
                getString(R.string.pix_message_dialog_alert_add_new_reliable_contact),
            )
                .setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setPrimaryButton(getString(R.string.entendi))
                .show(requireActivity().supportFragmentManager, null)
            arguments?.putBoolean(PIX_SHOW_DIALOG_ADD_NEW_RELIABLE_CONTACT_ARGS, false)
        }
    }

    override fun onClickSecondButtonError() {
        presenter.getTrustedDestinations()
    }

    override fun onNoTrustedDestinations() {
        binding?.containerWithoutMainTrusted?.visible()
    }

    override fun onSuccessTrustedDestinations(trustedDestinations: List<PixTrustedDestinationResponse>) {
        binding?.rvTrustedDestination?.visible()
        binding?.rvTrustedDestination?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding?.rvTrustedDestination?.adapter =
            PixMyLimitsTrustedDestinationsAdapter(trustedDestinations, this)
    }

    override fun onShowDetails(trustedDestination: PixTrustedDestinationResponse) {
        findNavController().navigate(
            PixMyLimitsTrustedDestinationsFragmentDirections.actionPixMyLimitsTrustedDestinationsFragmentToPixMyLimitsTrustedDestinationsDetailFragment(
                trustedDestination
            )
        )
    }

    override fun onBackButtonClicked(): Boolean {
        goToHomeMyLimits()
        return true
    }

    override fun onActionSwipe() {
        goToHomeMyLimits()
    }
}