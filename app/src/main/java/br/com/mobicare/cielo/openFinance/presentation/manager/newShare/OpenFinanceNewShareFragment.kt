package br.com.mobicare.cielo.openFinance.presentation.manager.newShare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.stepper.util.StatusStep
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieFragment
import br.com.mobicare.cielo.commons.constants.Intent.PDF
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.TWENTY_TWO
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceBsBankItemBinding
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceBsInfoBrandBinding
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceBsResourceGroupBinding
import br.com.mobicare.cielo.databinding.OpenFinanceNewShareFragmentBinding
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.model.DeadLine
import br.com.mobicare.cielo.openFinance.domain.model.ResourceGroup
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.presentation.manager.OpenFinanceManagerViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter.InstitutionAdapter
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter.ResourceGroupAdapter
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.adapter.SelectPeriodAdapter
import br.com.mobicare.cielo.openFinance.presentation.utils.CheckTypePeriod.checkTypePeriod
import br.com.mobicare.cielo.openFinance.presentation.utils.DefaultIconBank
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFile
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFilterList
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.BRAND_SELECTED
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.CITIZEN_PORTAL_BRANDS
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_SHARE
import com.google.gson.Gson
import kotlinx.android.synthetic.main.open_finance_flow_new_share_activity.compSteps
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceNewShareFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceNewShareFragmentBinding? = null
    private var navigation: CieloNavigation? = null
    private val newShareViewModel by viewModel<OpenFinanceNewShareViewModel>()
    private var brand: Brand? = null
    private val managerViewModel: OpenFinanceManagerViewModel by viewModel()
    private var bsLoading: CieloBottomSheet? = null
    private var bsBanks: CieloBottomSheet? = null
    private val typeShare by lazy { requireActivity().intent.getIntExtra(TYPE_SHARE, ZERO) }

    private val toolbarNewShare
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title =  if (typeShare == ONE) getString(R.string.renew_share) else getString(R.string.new_share_opf),
                showBackButton = false,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_close_blue,
                    onOptionsItemSelected = {
                        requireActivity().finish()
                    }
                )
            )
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceNewShareFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        setMinimumHeight()
        configureSteps()
        createShareFromIntentExtras()
        observeCreateShare()
        setListeners()
        observeBanks()
        observeList()
        observeUpdateShare()
        observeDocument()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarNewShare)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun createShareFromIntentExtras() {
        val jsonBrand = requireActivity().intent.getStringExtra(BRAND_SELECTED)
        brand = Gson().fromJson(jsonBrand, Brand::class.java)
        newShareViewModel.createShare(
            brand?.institutions?.first()?.authorizationServerId,
            brand?.institutions?.first()?.organizationId,
        )
    }

    private fun configureSteps() {
        activity?.compSteps?.apply {
            stepActive(R.layout.open_finance_manager_fragment, null)
            setStatusStep(StatusStep.DONE)
            stepActive(R.layout.open_finance_new_share_fragment, null)
        }
    }

    private fun observeCreateShare() {
        newShareViewModel.createShareLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsentDetail.Loading -> {
                    stateLoadingCreateShare()
                }

                is UIStateConsentDetail.Success -> {
                    stateSuccessCreateShare(uiState)
                }

                is UIStateConsentDetail.Error -> {
                    stateErrorCreateShare()
                }
            }
        }
    }

    private fun stateErrorCreateShare() {
        binding?.apply {
            shimmerLifecycleIndicator.gone()
            unexpectedError.visible()
            containerCreateShare.gone()
        }
    }

    private fun stateSuccessCreateShare(uiState: UIStateConsentDetail.Success<CreateShare>) {
        binding?.apply {
            containerCreateShare.visible()
            unexpectedError.gone()
            shimmerLifecycleIndicator.gone()
        }
        uiState.data?.let { createShare ->
            mountView(createShare)
        }
    }

    private fun stateLoadingCreateShare() {
        binding?.apply {
            unexpectedError.gone()
            shimmerLifecycleIndicator.visible()
            containerCreateShare.gone()
        }
    }

    private fun showBottomSheetCancel() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.cancel_sharing)),
                contentLayoutRes = R.layout.layout_open_finance_bs_cancel_consent,
                mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.text_pix_transfer_cancel),
                    onTap = {
                        requireActivity().finish()
                    },
                    drawableRes = R.drawable.button_background_selector_red_500
                ),
                secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.change_ec_btn_back),
                    onTap = {
                        it.dismiss()
                    }
                ),
            ).show(childFragmentManager, EMPTY)
    }

    private fun showBottomSheetResourceGroup(
        registerData: Boolean,
        listResourceGroup: List<ResourceGroup>
    ) {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(if (registerData) R.string.data_register else R.string.complementary_information)),
                contentLayoutRes = R.layout.layout_open_finance_bs_resource_group,
                disableExpandableMode = true,
                secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.know_more_citizen_portal),
                    onTap = {
                        it.dismiss()
                        Utils.openBrowser(requireActivity(), CITIZEN_PORTAL_BRANDS)
                    }
                ),
                onContentViewCreated = { view, bs ->
                    LayoutOpenFinanceBsResourceGroupBinding.bind(view).apply {
                        if (listResourceGroup.isEmpty()) {
                            tvNoDataFor.visible()
                            rvResourceGroup.gone()
                        } else {
                            rvResourceGroup.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = ResourceGroupAdapter(listResourceGroup.toMutableList())
                            }
                        }
                    }
                }
            ).show(childFragmentManager, EMPTY)
    }

    private fun showBottomSheetInstitution() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.institution_detail)),
                contentLayoutRes = R.layout.layout_open_finance_bs_info_brand,
                onContentViewCreated = { view, bs ->
                    LayoutOpenFinanceBsInfoBrandBinding.bind(view).apply {
                        tvBrand.text = brand?.brand
                        tvBrandDesc.text = brand?.institutions?.first()?.brandDescription
                        DefaultIconBank.checkTypeImage(
                            brand?.institutions?.first()?.logoUri,
                            iconBrand,
                            requireContext()
                        )
                        rvInstitutions.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = InstitutionAdapter(brand?.institutions ?: emptyList())
                        }
                    }

                },
                secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.know_more_citizen_portal),
                    onTap = {
                        it.dismiss()
                        Utils.openBrowser(requireActivity(), CITIZEN_PORTAL_BRANDS)
                    }
                ),
            ).show(childFragmentManager, EMPTY)
    }

    private fun mountView(createShare: CreateShare) {
        binding?.apply {
            tvBrand.text = createShare.authorizationServer.customerFriendlyName
            tvDocument.text = applyMasks(createShare.userInformation.document)
            tvRedirectInstitution.text = getString(
                R.string.you_are_redirect_to_bank,
                createShare.authorizationServer.customerFriendlyName
            ).fromHtml()
            tvInstitutions.text = getString(
                if ((brand?.institutions?.size
                        ?: ZERO) > ONE
                ) R.string.institutions
                else R.string.institution, brand?.institutions?.size
            )
            DefaultIconBank.checkTypeImage(
                createShare.authorizationServer.logoUri,
                iconBrand,
                requireContext()
            )
            if (createShare.authorizationServer.customerFriendlyName.length > TWENTY_TWO) {
                tvBrand.setTextAppearance(R.style.regular_montserrat_14_cloud_600_spacing_3)
            }
            setSelectPeriod(createShare.deadLines)
            checkTypeShare(createShare)
        }
    }

    private fun checkTypeShare(createShare: CreateShare){
        val deadLine = newShareViewModel.getDeadlineFromDetails()
        binding?.apply {
            when(typeShare){
                ZERO -> {
                    containerInfo.gone()
                    spinnerPeriod.visible()
                    containerBankChangeOrRenew.gone()
                    containerBankNewShare.visible()
                    periodRenew.gone()
                }
                ONE -> {
                    textInfo.text = getString(R.string.info_renovation)
                    spinnerPeriod.gone()
                    periodRenew.visible()
                    containerBankNewShare.gone()
                    containerBankChangeOrRenew.visible()
                    binding?.tvDatePeriod?.text = getString(R.string.until, periodInString(deadLine), dateFormatedByDeadline(deadLine) ?: EMPTY)
                    textRenew.text = periodInString(deadLine)
                    tvBrandChangeOrRenew.text = brand?.brand
                    DefaultIconBank.checkTypeImage(
                        createShare.authorizationServer.logoUri,
                        iconBrandChangeOrRenew,
                        requireContext()
                    )
                }
                TWO ->{
                    textInfo.text = getString(R.string.info_alteration)
                    spinnerPeriod.visible()
                    periodRenew.gone()
                    containerBankNewShare.gone()
                    containerBankChangeOrRenew.visible()
                    tvBrandChangeOrRenew.text = brand?.brand
                    DefaultIconBank.checkTypeImage(
                        createShare.authorizationServer.logoUri,
                        iconBrandChangeOrRenew,
                        requireContext()
                    )
                }
            }
        }
    }

    private fun setListeners() {
        binding?.apply {
            containerRegisterData.setOnClickListener {
                showBottomSheetResourceGroup(true, newShareViewModel.getRegisterData())
            }
            containerComplementaryData.setOnClickListener {
                showBottomSheetResourceGroup(false, newShareViewModel.getComplementaryData())
            }

            changeInstitution.setOnClickListener {
                managerViewModel.getBanks(EMPTY)
            }

            infoInstitution.setOnClickListener {
                showBottomSheetInstitution()
            }

            btnCancel.setOnClickListener {
                showBottomSheetCancel()
            }

            cancelSharing.setOnClickListener {
                showBottomSheetCancel()
            }

            updatePage.setOnClickListener {
                createShareFromIntentExtras()
            }

            btnConfirmConsent.setOnClickListener {
                newShareViewModel.apply {
                    updateShare(binding?.spinnerPeriod?.selectedItem as DeadLine, typeShare)
                    saveInformationToConclusion()
                }
            }

            termsOfUse.setOnClickListener {
                newShareViewModel.getTermsOfUse()
            }
        }
    }

    private fun openNewSharing(data: List<Brand>) {
        var bankSelected: Brand? = null
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.select_institution),
            ),
            searchConfigurator = CieloBottomSheet.SearchConfigurator(
                isShowSearchBar = true,
                isShowSearchIcon = true,
                hintSearchBar = getString(R.string.busca),
                onSearch = { searchString, bottomSheet ->
                    onSearch(searchString, bottomSheet)
                }
            ),
            layoutItemRes = R.layout.layout_open_finance_bs_bank_item,
            data = data,
            initialSelectedItem = bankSelected,
            onViewBound = { bank, isSelected, itemView ->
                mountViewBound(bank, isSelected, itemView)
            },
            onItemClicked = { bankAccount, position, bottomSheet ->
                bankSelected = bankAccount
                itemSelected(position, bottomSheet, bankSelected)
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.selecionar),
                onTap = {},
                startEnabled = false,
            )
        ).show(childFragmentManager, BiometricTokenSelfieFragment.TAG)
    }

    private fun onSearch(searchString: String, bottomSheet: CieloBottomSheet) {
        (bottomSheet as CieloListBottomSheet<Brand>).updateSelectedPosition(ONE_NEGATIVE)
        bottomSheet.changeButtonStatus(false)
        managerViewModel.filterList(searchString)
        bsBanks = bottomSheet
    }

    private fun mountViewBound(bank: Brand, isSelected: Boolean, itemView: View) {
        LayoutOpenFinanceBsBankItemBinding.bind(itemView).apply {
            bank.institutions?.firstOrNull()
                ?.let { DefaultIconBank.checkTypeImage(it.logoUri, ivIconBank, requireContext()) }
            tvBankName.text = bank.brand
            tvInstitutions.text = getString(
                if ((bank.institutions?.size
                        ?: ZERO) > ONE
                ) R.string.institutions
                else R.string.institution, bank.institutions?.size
            )
            ivRadioButton.isSelected = isSelected
            root.isSelected = isSelected
        }
    }

    private fun itemSelected(
        position: Int,
        bottomSheet: CieloListBottomSheet<Brand>,
        bankSelected: Brand?
    ) {
        bottomSheet.updateSelectedPosition(position)
        bottomSheet.updateMainButtonConfigurator(
            CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.selecionar),
                onTap = {
                    requireActivity().startActivity<OpenFinanceFlowNewShareActivity>(
                        BRAND_SELECTED to Gson().toJson(bankSelected),
                        TYPE_SHARE to ZERO
                    )
                    requireActivity().finish()
                },
                startEnabled = true,
            )
        )
    }

    private fun showBottomSheetLoading() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.select_institution)),
                contentLayoutRes = R.layout.layout_open_finance_loading_bottomsheet,
                onContentViewCreated = { view, bs -> bsLoading = bs }
            ).show(childFragmentManager, EMPTY)
    }

    private fun showBottomSheetError() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.select_institution)),
                contentLayoutRes = R.layout.layout_open_finance_error_bottomsheet,
            ).show(childFragmentManager, EMPTY)
    }

    private fun observeBanks() {
        managerViewModel.getBanksLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsentDetail.Loading -> {
                    showBottomSheetLoading()
                }

                is UIStateConsentDetail.Success -> {
                    bsLoading?.dismiss()
                    uiState.data?.let { openNewSharing(it) }
                }

                is UIStateConsentDetail.Error -> {
                    bsLoading?.dismiss()
                    showBottomSheetError()
                }
            }
        }
    }

    private fun observeList() {
        managerViewModel.getListFilterLiveData.observe(viewLifecycleOwner) { uiState ->
            if (bsBanks == null) return@observe
            val bottomSheet = (bsBanks as CieloListBottomSheet<Brand>)
            when (uiState) {
                is UIStateFilterList.ListFiltered -> {
                    bsBanks?.hideSearchErrorMessage()
                    uiState.data?.let { bottomSheet.updateList(it) }
                }

                is UIStateFilterList.NotFound -> {
                    uiState.data?.let { bottomSheet.updateList(it) }
                    bottomSheet.showSearchErrorMessage(getString(R.string.brands_opf_filter_list_error))
                }
            }
        }
    }

    private fun setSelectPeriod(listPeriod: List<DeadLine>) {
        binding?.spinnerPeriod?.let { spinner ->
            val adapterSpinner = SelectPeriodAdapter(requireContext(), listPeriod)
            spinner.adapter = adapterSpinner
            spinner.setSelection(ZERO)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val deadLine = listPeriod[position]
                    binding?.tvDatePeriod?.text = getString(R.string.until, periodInString(deadLine), dateFormatedByDeadline(deadLine) ?: EMPTY)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            spinner.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
                adapterSpinner.spinnerOpen = hasFocus
                adapterSpinner.notifyDataSetChanged()
            }
        }
    }

    private fun applyMasks(text: String): String {
        return when {
            ValidationUtils.isCNPJ(text) -> addMaskCPForCNPJ(
                text,
                getString(R.string.mask_cnpj_step4)
            )

            ValidationUtils.isCPF(text) -> addMaskCPForCNPJ(
                text,
                getString(R.string.mask_cpf_step4)
            )

            else -> text
        }
    }

    private fun observeUpdateShare() {
        newShareViewModel.updateShareLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsentDetail.Loading -> {
                    stateLoadingUpdateShare()
                }

                is UIStateConsentDetail.Success -> {
                    stateSuccessUpdateShare(uiState)
                }

                is UIStateConsentDetail.Error -> {
                    stateErrorUpdateShare()
                }
            }
        }
    }

    private fun stateErrorUpdateShare() {
        binding?.apply {
            containerLoading.gone()
            containerErrorConfirmShare.visible()
            containerCreateShare.gone()
        }
    }

    private fun stateSuccessUpdateShare(uiState: UIStateConsentDetail.Success<UpdateShare>) {
        binding?.containerLoading.gone()
        activity?.compSteps?.setStatusStep(StatusStep.DONE)
        uiState.data?.let { updateShare ->
            findNavController().navigate(
                OpenFinanceNewShareFragmentDirections.actionOpenFinanceNewShareFragmentToOpenFinanceRedirectFragment(
                    updateShare.redirectUri
                )
            )
        }
    }

    private fun stateLoadingUpdateShare() {
        binding?.apply {
            containerLoading.visible()
            containerCreateShare.gone()
        }
    }

    private fun observeDocument() {
        newShareViewModel.termsOfUseLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateFile.LoadingDocument -> {
                    stateLoadingDocument()
                }

                is UIStateFile.SuccessDocument -> {
                    stateSuccessDocument(uiState)
                }

                is UIStateFile.ErrorDocument -> {
                    stateErrorDocument()
                }
            }
        }
    }

    private fun stateErrorDocument() {
        binding?.apply {
            loadDocument.gone()
            termsOfUse.isEnabled = true
        }
        showBottomSheetErrorDocument()
    }

    private fun stateSuccessDocument(uiState: UIStateFile.SuccessDocument<String>) {
        binding?.apply {
            loadDocument.gone()
            uiState.data?.let { base64 ->
                val tempFile = FileUtils(requireContext()).convertBase64ToFile(
                    base64String = base64,
                    fileName = getString(R.string.terms_of_use_opf),
                    fileType = PDF
                )
                FileUtils(requireContext()).startShare(tempFile)
            }
            termsOfUse.isEnabled = true
        }
    }

    private fun stateLoadingDocument() {
        binding?.apply {
            loadDocument.visible()
            termsOfUse.isEnabled = false
        }
    }

    private fun showBottomSheetErrorDocument() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.commons_generic_error_title)),
                contentLayoutRes = R.layout.layout_open_finance_bs_error_document,
            ).show(childFragmentManager, EMPTY)
    }

    private fun periodInString(deadLine: DeadLine?): String {
        val typePeriod = deadLine?.type?.let { checkTypePeriod(requireContext(), it) }
        return deadLine?.total.toString() + " " + typePeriod
    }
    private fun dateFormatedByDeadline(deadLine: DeadLine?): String?{
        return deadLine?.expirationDate?.formatterDate(
            SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS,
            SIMPLE_DT_FORMAT_MASK
        )
    }
}