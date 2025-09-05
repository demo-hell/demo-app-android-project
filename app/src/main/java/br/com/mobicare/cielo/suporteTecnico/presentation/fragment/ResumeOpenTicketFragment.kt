package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.inputtext.CieloTextInputField.MaskFormat
import br.com.cielo.libflue.inputtext.CieloTextInputField.ValidationMode
import br.com.cielo.libflue.inputtext.CieloTextInputField.Validator
import br.com.cielo.libflue.util.ELEVEN
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_REVIEW_INFORMATIONS
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.deeplink.MEU_CADASTRO_TITLE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.BottomSheetRadioButtonBinding
import br.com.mobicare.cielo.databinding.FragmentResumeOpenTicketBinding
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.recebaMais.domain.OwnerAddress
import br.com.mobicare.cielo.recebaMais.domain.OwnerContact
import br.com.mobicare.cielo.recebaMais.domain.OwnerPhone
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.ScheduleDataResponse
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.ScheduleAvailabilityViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.BUSINESS_HOURS
import br.com.mobicare.cielo.suporteTecnico.utils.SELECT_ADDRESS
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class ResumeOpenTicketFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentResumeOpenTicketBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleAvailabilityViewModel by viewModel()
    private val args: ResumeOpenTicketFragmentArgs by navArgs()
    private lateinit var openTicket: OpenTicket
    private var problemSelected: String? = null
    private var scheduleSelected: String? = null
    private var selectedAddress: OwnerAddress? = null
    private var selectedContact: OwnerPhone? = null
    private var idAddress: String? = null
    private var idHours: String? = null

    private var navigation: CieloNavigation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTicket = args.requestTicket
        problemSelected = args.problemSelected
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentResumeOpenTicketBinding.inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onReload()
        setupNavigation()
        setupObservers()
        setListeners()
        GA4.logScreenView(PATH_REVIEW_INFORMATIONS)
    }

    override fun onResume() {
        super.onResume()
        onReload()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = true)
            navigation?.showCloseButton(isShow = false)
            navigation?.showHelpButton(isShow = false)
        }
    }

    fun onReload() {
        viewModel.getScheduleAvailability()
        viewModel.getMerchant()
    }

    private fun setupObservers() {
        viewModel.scheduleAvailability.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.HideLoading -> hideLoading()
                is UiState.Success -> onScheduleSuccess(state.data)
                else -> handleError()
            }
        }
        viewModel.merchantAddress.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.HideLoading -> hideLoading()
                is UiState.Success -> onAddressSuccess(state.data)
                else -> handleError()
            }
        }
    }

    private fun onScheduleSuccess(data: ScheduleDataResponse?) {
        data?.let {
            binding.apply {
                btnBusinessHour.setOnSelectListener { _, _ ->
                    viewModel.saveOriginalData(it)
                    viewModel.formatData(it)
                    openBottomSheet(
                        viewModel.formattedData as HashMap<String, String>,
                        BUSINESS_HOURS,
                    )
                }
            }
        }
    }

    private fun onAddressSuccess(data: UserOwnerSupportResponse?) {
        setupViewAddress()
        data?.let { itData ->
            binding.apply {
                tvChangeAddress.setOnClickListener {
                    viewModel.saveOriginalData(itData.addresses)
                    viewModel.formatData(itData)
                    openBottomSheet(
                        viewModel.formattedData as HashMap<String, String>,
                        SELECT_ADDRESS
                    )
                }
            }
            setupViewFacade(data)
            setupViewContact(data.contacts.firstOrNull())
        }
    }

    private fun hideLoading() {
        binding.apply {
            progress.root.gone()
            tvTitleResumeOpenTicket.visible()
            scrollView.visible()
            errorInclude.root.gone()
        }
    }

    private fun showLoading() {
        binding.apply {
            progress.root.visible()
            tvTitleResumeOpenTicket.gone()
            scrollView.gone()
            errorInclude.root.gone()
        }
    }

    private fun handleError() {
        binding.apply {
            tvTitleResumeOpenTicket.gone()
            scrollView.gone()
            progress.root.gone()
            errorInclude.root.visible()
            errorInclude.btnReload.setOnClickListener {
                onReload()
            }
        }
    }

    private fun setupViewAddress() {
        viewModel.populateViewIfOriginalDataIsNull()
        viewModel.addressSelected.observe(viewLifecycleOwner) { state ->
            state?.let {
                binding.apply {
                    tvAddress.text = getString(
                        R.string.address_complete,
                        it.streetAddress,
                        it.number.ifNull { EMPTY },
                        it.streetAddress2.ifNull { EMPTY },
                        it.neighborhood,
                        it.city,
                        it.state,
                        it.zipCode
                    )
                    selectedAddress = it
                    viewModel.convertAddressToAddress(
                        it,
                        binding.inputReferencePoint.textInputEditText.text.toString(),
                        binding.inputFacade.textInputEditText.text.toString()
                    )
                }
            }
        }
    }

    private fun setupViewFacade(data: UserOwnerSupportResponse?) {
        binding.inputFacade.textInputEditText.setText(data?.companyName ?: EMPTY)
    }

    private fun setupViewContact(contact: OwnerContact?) {
        val phoneInputFirst = contact?.ownerPhones?.first()

        binding.apply {
            inputContactName.apply {
                textInputEditText.setText(contact?.name ?: getString(R.string.insert_name_error))
                this.setValidators(
                    Validator(
                        rule = { it.extractedValue.length != ZERO },
                        errorMessage = getString(R.string.insert_name_error),
                        onResult = { _, _ ->
                            validateButton()
                        }
                    )
                )
                this.validationMode = ValidationMode.TEXT_CHANGED
                this.textInputEditText.requestFocus()
                validate()
            }

            inputPhone.apply {
                this.setMask(MaskFormat.PHONE_WITH_DDD)
                this.setValidators(
                    Validator(
                        rule = { it.extractedValue.length == ELEVEN },
                        errorMessage = getString(R.string.required_data_field_validation_error_field_number)
                    )
                )
                this.validationMode = ValidationMode.TEXT_CHANGED
                this.textInputEditText.requestFocus()
                this.textInputEditText.setOnEditorActionListener { _, _, _ ->
                    clearFocus()
                    false
                }
                text = phoneInputFirst?.areaCode + phoneInputFirst?.number
            }
        }
    }

    private fun contactForObjectObserver() {
        viewModel.contactInOriginalData.observe(viewLifecycleOwner) { state ->
            state?.let {
                selectedContact = it
            }
        }
    }

    private fun setTextSchedule() {
        binding.btnBusinessHour.text = scheduleSelected ?: binding.btnBusinessHour.hint
    }

    private fun validateButton() {
        binding.btnConfirm.isButtonEnabled =
            fieldsHasError().not() && hasFieldsEmpty().not() && scheduleSelected != null
    }

    private fun fieldsHasError(): Boolean {
        binding.apply {
            return inputContactName.hasError
                    || inputPhone.hasError
        }
    }

    private fun hasFieldsEmpty(): Boolean {
        binding.apply {
            return inputContactName.textInputEditText.text.isNullOrBlank()
                    || inputPhone.textInputEditText.text.isNullOrBlank()
        }
    }

    private fun setListeners() {
        binding.apply {
            btnConfirm.setOnClickListener {
                viewModel.saveOriginalPhone(this.inputPhone.textInputEditText.text.toString())
                contactForObjectObserver()
                if (isAllFieldsValid()) {
                    saveOpenTicket()
                    findNavController().navigate(
                        ResumeOpenTicketFragmentDirections.actionResumeOpenTicketFragmentToReviewInformationTicketFragment(
                            openTicket, problemSelected.toString()
                        )
                    )
                }
            }
        }
    }

    private fun isAllFieldsValid(): Boolean {
        binding.apply {
            return validateFields(
                inputContactName.textInputEditText.text.toString(),
            )
        }
    }

    private fun validateFields(
        name: String,
    ): Boolean {
        var isValid = true

        binding.apply {
            if (name.length <= ZERO || name.isBlank()) {
                inputContactName.setError(getString(R.string.insert_phone_empty))
                isValid = false
            }

            if (selectedContact == null) {
                inputPhone.setError(getString(R.string.insert_name_empty))
                isValid = false
            }

            if (btnBusinessHour.text == btnBusinessHour.hint) {
                btnBusinessHour.setError(getString(R.string.insert_business_hour_empty))
                isValid = false
            }

            if (inputPhone.textInputEditText.text.isNullOrBlank()) {
                inputPhone.setError(getString(R.string.insert_phone_empty))
                isValid = false
            }
        }
        return isValid
    }

    private fun openBottomSheet(listInfo: HashMap<String, String>, title: String) {
        GA4.logSelectContentStep(PATH_REVIEW_INFORMATIONS, title)

        var infoSelected: String? = null
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = title,
                titleAppearance = R.style.bold_montserrat_16_cloud_800
            ),
            layoutItemRes = R.layout.bottom_sheet_radio_button,
            data = listInfo.values.toList(),
            initialSelectedItem = infoSelected,
            onViewBound = { info, isSelected, itemView ->
                val binding = BottomSheetRadioButtonBinding.bind(itemView)
                binding.apply {
                    radioButton.isChecked = isSelected
                    root.isSelected = isSelected
                    radioButton.text = info
                }
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.continuar),
                buttonType = CieloBottomSheet.ButtonType.ROUNDED,
                startEnabled = false,
                onTap = {
                    if (title == SELECT_ADDRESS) {
                        idAddress?.let { id -> viewModel.getCollectionForOption(title, id) }
                    }
                    setTextSchedule()
                    validateButton()
                    it.dismiss()
                }
            ),
            secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.add_new),
                buttonType = CieloBottomSheet.ButtonType.ROUNDED,
                startEnabled = true,
                onTap = {
                    navigateToMyRegister()
                    it.dismiss()
                }
            ),
            onItemClicked = { infoItems, position, bottomSheet ->
                infoSelected = infoItems
                if (title == BUSINESS_HOURS) {
                    scheduleSelected = infoItems
                    idHours = listInfo.keys.toList()[position]
                }
                idAddress = listInfo.keys.toList()[position]
                bottomSheet.updateSelectedPosition(position)
                bottomSheet.setMainButtonEnabled(true)

            },
            disableExpandableMode = true
        ).show(childFragmentManager, EMPTY)
    }

    private fun saveOpenTicket() {
        var addressComplete = viewModel.convertAddressToAddress(
            selectedAddress,
            binding.inputReferencePoint.textInputEditText.text.toString(),
            binding.inputFacade.textInputEditText.text.toString()
        )
        openTicket = openTicket.copy(
            contactName = binding.inputContactName.textInputEditText.text.toString(),
            address = addressComplete,
            phones = listOf(selectedContact),
            openingHourText = scheduleSelected,
            openingHourCode = idHours
        )
    }

    private fun navigateToMyRegister() {
        Intent(context, RouterFragmentInActivity::class.java).apply {
            putExtra(FRAGMENT_TO_ROUTER, MeuCadastroFragmentAtualNovo::class.java.canonicalName)
            putExtra(TITLE_ROUTER_FRAGMENT, MEU_CADASTRO_TITLE)
            requireContext().startActivity(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }
}