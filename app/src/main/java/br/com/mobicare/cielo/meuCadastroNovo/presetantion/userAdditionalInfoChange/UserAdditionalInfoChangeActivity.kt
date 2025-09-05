package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.COMMA_SPACE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.ActivityChangeUserAdditionalInfoBinding
import br.com.mobicare.cielo.databinding.ItemAdditionalSimpleFieldBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.utils.AdditionalInfoUiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserAdditionalInfoChangeActivity : BaseLoggedActivity() {

    private val viewModel: UserAdditionalInfoChangeViewModel by viewModel()
    private lateinit var binding: ActivityChangeUserAdditionalInfoBinding
    private val userAdditionalInfo: GetUserAdditionalInfo? by lazy {
        intent?.extras?.getSerializable(UserDataConstants.ARG_CHANGE_USER_ADDITIONAL_DATA) as? GetUserAdditionalInfo
    }
    private lateinit var additionalFields: GetAdditionalInfoFields
    private var updatedFields = GetUserAdditionalInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUserAdditionalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading()
        viewModel.getAdditionalInfoFields()
        setupObservables()
        setupClickListeners()
    }

    private fun setupObservables() {
        viewModel.additionalFieldsInfoLiveData.observe(this) { uiState ->
            when (uiState) {
                is AdditionalInfoUiState.GetError -> {
                    hideLoading()
                    showCustomBottomSheet(
                        image = R.drawable.img_10_erro,
                        title = getString(R.string.user_data_change_additional_bs_error_title),
                        message = getString(R.string.user_data_change_additional_bs_error_message),
                        bt2Title = getString(R.string.text_try_again_label),
                        bt2Callback = {
                            finish()
                            false
                        },
                        closeCallback = {
                            finish()
                        }
                    )
                }

                is AdditionalInfoUiState.GetSuccess -> {
                    additionalFields = uiState.additionalFields
                    setupView()
                }

                is AdditionalInfoUiState.UpdateError -> {
                    showCustomBottomSheet(
                        image = R.drawable.img_10_erro,
                        title = getString(R.string.user_data_change_additional_bs_error_title),
                        message = getString(R.string.user_data_change_additional_bs_error_message),
                        bt2Title = getString(R.string.text_try_again_label),
                        bt2Callback = {
                            moveToHome()
                            false
                        },
                        closeCallback = {
                            moveToHome()
                        }
                    )
                }
                is AdditionalInfoUiState.UpdateSuccess -> {
                    hideLoading()
                    showCustomBottomSheet(
                        image = R.drawable.img_129_estrelas,
                        title = getString(R.string.user_data_change_additional_bs_update_success_title),
                        message = getString(R.string.user_data_change_additional_bs_update_success_message),
                        bt2Title = getString(R.string.finish),
                        bt2Callback = {
                            moveToHome()
                            false
                        },
                        closeCallback = {
                            moveToHome()
                        }
                    )
                }
            }

        }
    }

    private fun setupView() {
        binding.apply {
            communicationTi.editText?.setText(getCommunicationStringByDescription())

            contactTi.editText?.setText(getStringByDescription(userAdditionalInfo?.contactPreference?.description))

            pcdTi.editText?.setText(getStringByDescription(userAdditionalInfo?.pcdType?.description))

            contactHourTi.editText?.setText(getStringByDescription(userAdditionalInfo?.timeOfDay?.description))

            btnNext.setOnClickListener {
                validateUpdatedFields()
                showLoading()
                viewModel.putAdditionalInfo(
                    updatedFields.timeOfDay?.code.orEmpty(),
                    getCommunicationArrayByCode(),
                    updatedFields.contactPreference?.code.orEmpty(),
                    updatedFields.pcdType?.code.orEmpty()
                )
            }

            hideLoading()
        }
    }

    private fun getStringByDescription(description: String?): String {
        return if (description.isNullOrEmpty()) {
            getString(R.string.user_data_additional_information_not_defined)
        } else {
            description
        }
    }

    private fun getCommunicationStringByDescription(): String {
        if (userAdditionalInfo?.typeOfCommunication.isNullOrEmpty()) {
            return getString(R.string.user_data_additional_information_not_defined)
        } else {
            var communicationString = EMPTY
            userAdditionalInfo?.typeOfCommunication?.forEachIndexed { index, communication ->
                communicationString += if (index == ZERO) {
                    communication.description
                } else {
                    COMMA_SPACE + communication.description
                }
            }
            return communicationString
        }
    }

    private fun getCommunicationArrayByCode(): ArrayList<String> {
        var communicationArray = arrayListOf<String>()
        updatedFields.typeOfCommunication.forEach {
            it.code?.let { code ->
                communicationArray.add(code)
            }
        }
        return communicationArray
    }

    private fun setupClickListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                finish()
            }

            communicationTi.editText?.setOnClickListener {
                CommunicationTypeBottomSheet.create(
                    typesOfCommunication = additionalFields.typeOfCommunication,
                    onButtonClicked = {
                        updatedFields.typeOfCommunication.apply {
                            clear()
                            addAll(it)
                        }
                        communicationTi.editText?.setText(formatterCommunicationSting(it))
                        btnNext.isEnabled = true
                    }
                ).show(supportFragmentManager, this@UserAdditionalInfoChangeActivity.javaClass.simpleName)
            }

            contactTi.editText?.setOnClickListener {
                showContactBs()
            }

            pcdTi.editText?.setOnClickListener {
                showPcdBs()
            }

            contactHourTi.editText?.setOnClickListener {
                showContactHourBs()
            }
        }
    }

    private fun showContactBs() {
        SimpleFieldBottomSheet.create(
            screenTitle = getString(R.string.user_data_change_additional_bs_contact_title),
            layoutItemRes = R.layout.item_additional_simple_field,
            data = additionalFields.contactPreference,
            buttonClick = {
                binding.btnNext.isEnabled = true
                it.dismiss()
            },
            onViewBound = { contactPreference, isSelected, itemView ->
                val itemAdditionalSimpleFieldBinding =
                    ItemAdditionalSimpleFieldBinding.bind(itemView)
                itemAdditionalSimpleFieldBinding.apply {
                    rbChoose.isSelected = isSelected
                    tvValue.text = contactPreference.description
                }
            },
            onItemClicked = { contactPreference, position, bottomSheet ->
                binding.contactTi.editText?.setText(contactPreference.description)
                updatedFields.contactPreference = contactPreference
                bottomSheet.updateSelectedPosition(position)
            }
        ).show(
            supportFragmentManager,
            this@UserAdditionalInfoChangeActivity.javaClass.simpleName
        )
    }

    //
    private fun showPcdBs() {
        SimpleFieldBottomSheet.create(
            screenTitle = getString(R.string.user_data_change_additional_bs_pcd_title),
            layoutItemRes = R.layout.item_additional_simple_field,
            data = additionalFields.pcdType,
            buttonClick = {
                binding.btnNext.isEnabled = true
                it.dismiss()
            },
            onViewBound = { pcdType, isSelected, itemView ->
                val itemAdditionalSimpleFieldBinding =
                    ItemAdditionalSimpleFieldBinding.bind(itemView)
                itemAdditionalSimpleFieldBinding.apply {
                    rbChoose.isSelected = isSelected
                    tvValue.text = pcdType.description
                }
            },
            onItemClicked = { pcdType, position, bottomSheet ->
                binding.pcdTi.editText?.setText(pcdType.description)
                updatedFields.pcdType = pcdType
                bottomSheet.updateSelectedPosition(position)
            }
        ).show(
            supportFragmentManager,
            this@UserAdditionalInfoChangeActivity.javaClass.simpleName
        )
    }

    private fun showContactHourBs() {
        SimpleFieldBottomSheet.create(
            screenTitle = getString(R.string.user_data_change_additional_bs_hour_contact_title),
            layoutItemRes = R.layout.item_additional_simple_field,
            data = additionalFields.timeOfDay,
            buttonClick = {
                binding.btnNext.isEnabled = true
                it.dismiss()
            },
            onViewBound = { timeOfDay, isSelected, itemView ->
                val itemAdditionalSimpleFieldBinding =
                    ItemAdditionalSimpleFieldBinding.bind(itemView)
                itemAdditionalSimpleFieldBinding.apply {
                    rbChoose.isSelected = isSelected
                    tvValue.text = timeOfDay.description
                }
            },
            onItemClicked = { timeOfDay, position, bottomSheet ->
                binding.contactHourTi.editText?.setText(timeOfDay.description)
                updatedFields.timeOfDay = timeOfDay
                bottomSheet.updateSelectedPosition(position)
            }
        ).show(
            supportFragmentManager,
            this@UserAdditionalInfoChangeActivity.javaClass.simpleName
        )
    }

    private fun formatterCommunicationSting(typeOfCommunication: List<TypeOfCommunication>): String {
        if (typeOfCommunication.isEmpty()){
            return getString(R.string.user_data_additional_information_not_defined)
        } else {
            var communicationString = EMPTY
            typeOfCommunication.forEachIndexed { index, communication ->
                communicationString += if (index == ZERO){
                    communication.description
                } else {
                    COMMA_SPACE + communication.description
                }
            }
            return communicationString
        }
    }

    private fun showLoading() {
        binding.apply {
            messageProgressView.showLoading()
            scrollView.gone()
        }
    }

    private fun hideLoading() {
        binding.apply {
            scrollView.visible()
            messageProgressView.hideLoading()
        }
    }


    private fun validateUpdatedFields() {
        updatedFields.apply {
            if (timeOfDay == null) {
                timeOfDay = userAdditionalInfo?.timeOfDay
            }

            if (typeOfCommunication.isEmpty()){
                typeOfCommunication.clear()
                userAdditionalInfo?.typeOfCommunication?.let { typeOfCommunication.addAll(it) }
            }

            if (contactPreference == null){
                contactPreference = userAdditionalInfo?.contactPreference
            }

            if (pcdType == null){
                pcdType = userAdditionalInfo?.pcdType
            }
        }
    }

    fun showCustomBottomSheet(
        @DrawableRes image: Int? = null,
        title: String? = null,
        message: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: EMPTY,
                nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLACK,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                isCancelable = true,
                isFullScreen = false,
                isPhone = false
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }
}