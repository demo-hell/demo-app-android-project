package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticEndBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAAF
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEKLY
import br.com.mobicare.cielo.pix.constants.EMPTY

class ReceiveAutomaticEndFragment : BaseFragment(), CieloNavigationListener {
    private var binding: FragmentReceiveAutomaticEndBinding? = null
    private var navigation: CieloNavigation? = null
    private val args: ReceiveAutomaticEndFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentReceiveAutomaticEndBinding.inflate(
            inflater,
            container,
            false,
        ).also {
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        logScreenView()
    }

    override fun onResume() {
        super.onResume()
        showTextChoiceSuccess()
    }

    private fun logScreenView() {
        RAAF.logRAHiredScreenView(args.transactiontype)
    }

    private fun showTextChoiceSuccess() {
        binding?.tvDescriptionRac?.text =
            messageCustom(
                args.transactiontype,
                args.periodicity,
                args.weekday,
                args.monthday,
            )
    }

    private fun setupListeners() {
        binding?.btnSecond?.apply {
            setTextAppearance(R.style.semi_bold_montserrat_16)
            setOnClickListener {
                goToTaxAndPlans()
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(false)
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    isExpanded = true,
                    disableExpandableMode = true,
                    toolbarMenu =
                        CollapsingToolbarBaseActivity.ToolbarMenu(
                            menuRes = R.menu.menu_common_close_blue,
                            onOptionsItemSelected = {
                                returnHome()
                            },
                        ),
                    showBackButton = false,
                ),
            )
        }
    }

    override fun onButtonClicked(labelButton: String) {
        goToTaxAndPlans()
    }

    private fun goToTaxAndPlans() {
        Router.navigateTo(
            requireContext(),
            Menu(
                Router.APP_ANDROID_RATES,
                EMPTY,
                listOf(),
                getString(R.string.txp_header),
                false,
                EMPTY,
                listOf(),
                show = false,
                showItems = false,
                menuTarget = MenuTarget(),
            ),
        ).also {
            activity?.finishAndRemoveTask()
        }
    }

    private fun messageCustom(
        typeTransactionSelected: String,
        periodSelected: String,
        weekSelected: String,
        daySelected: String,
    ): String {
        var text: String = EMPTY_VALUE
        when (typeTransactionSelected) {
            ConstantsReceiveAutomatic.BOTH -> {
                when (periodSelected) {
                    DAILY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_two_days,
                                ConstantsReceiveAutomatic.CREDIT_CASH_AND_INSTALLMENT,
                            )
                    }
                    WEEKLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_week,
                                ConstantsReceiveAutomatic.CREDIT_CASH_AND_INSTALLMENT,
                                ConstantsReceiveAutomatic.WEEKLY_TEXT,
                                weekSelected,
                            )
                    }
                    MONTHLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_month,
                                ConstantsReceiveAutomatic.CREDIT_CASH_AND_INSTALLMENT,
                                ConstantsReceiveAutomatic.MONTHLY_TEXT,
                                daySelected,
                            )
                    }
                }
            }
            ConstantsReceiveAutomatic.CREDIT -> {
                when (periodSelected) {
                    DAILY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_two_days,
                                ConstantsReceiveAutomatic.CREDIT_CASH,
                            )
                    }
                    WEEKLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_week,
                                ConstantsReceiveAutomatic.CREDIT_CASH,
                                ConstantsReceiveAutomatic.WEEKLY_TEXT,
                                weekSelected,
                            )
                    }
                    MONTHLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_month,
                                ConstantsReceiveAutomatic.CREDIT_CASH,
                                ConstantsReceiveAutomatic.MONTHLY_TEXT,
                                daySelected,
                            )
                    }
                }
            }
            ConstantsReceiveAutomatic.INSTALLMENT -> {
                when (periodSelected) {
                    DAILY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_two_days,
                                ConstantsReceiveAutomatic.CREDIT_INSTALLMENT,
                            )
                    }
                    WEEKLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_week,
                                ConstantsReceiveAutomatic.CREDIT_INSTALLMENT,
                                ConstantsReceiveAutomatic.WEEKLY_TEXT,
                                weekSelected,
                            )
                    }
                    MONTHLY -> {
                        text =
                            getString(
                                R.string.receive_auto_contract_success_description_month,
                                ConstantsReceiveAutomatic.CREDIT_INSTALLMENT,
                                ConstantsReceiveAutomatic.MONTHLY_TEXT,
                                daySelected,
                            )
                    }
                }
            }
        }
        return text
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            },
        )
        return false
    }

    private fun returnHome() {
        requireActivity().backToHome()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
