package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.COMMA_SPACE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentUserDataBinding
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_ADDITIONAL_DATA
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_EMAIL
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_FOREIGN
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.ARG_CHANGE_USER_DATA_PHONE
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.MULTICHANNEL
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.ContactPreference
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PcdType
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TimeOfDay
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosUsuarioPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange.UserAdditionalInfoChangeActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.userDataChange.UserDataChangeActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DadosUsuarioFragment : BaseFragment(), MeuCadastroContract.DadosUsuarioView {

    private var binding: FragmentUserDataBinding? = null
    private var userEmail: String = EMPTY
    private var userPhone: String = EMPTY
    private var userIsForeign: Boolean? = false
    val presenter: DadosUsuarioPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance() = DadosUsuarioFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDataBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUser()
    }

    private fun initUser() {
        showProgressScreen()
        presenter.loadDadosUser(UserPreferences.getInstance().token)
        initView()
    }

    private fun initView() {
        binding?.apply {
            btChangeData.setOnClickListener {
                showPictureBottomSheet()
            }

            btCallCenter.setOnClickListener {
                CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
            }

            btChangeAdditionalData.setOnClickListener {
                requireActivity().startActivity<UserAdditionalInfoChangeActivity>(
                    ARG_CHANGE_USER_ADDITIONAL_DATA to presenter.userAdditionalInfo
                )
            }
        }
    }

    override fun showUser(user: MeResponse?) {
        binding?.apply {
            user?.let {
                userIsForeign = it.identity?.foreigner

                if (it.username.isNotEmpty()) {
                    nameText.text = it.username
                } else {
                    nameLabel.gone()
                    nameText.gone()
                }

                if (!it.identity?.cpf.isNullOrEmpty()) {
                    cpfText.text =
                        EditTextHelper.cpfMaskFormatter(it.identity?.cpf.orEmpty()).formattedText.string
                } else {
                    cpfText.gone()
                    cpfLabel.gone()
                }

                if (!it.phoneNumber.isNullOrEmpty()) {
                    userPhone = it.phoneNumber
                    phoneText.text = it.phoneNumber
                } else {
                    phoneLabel.gone()
                    phoneText.gone()
                }

                if (it.email.isNotEmpty()) {
                    userEmail = it.email
                    emailText.text = it.email
                } else {
                    emailLabel.gone()
                    emailText.gone()
                }

                it.mainRole?.let { role ->
                    profileText.text = when (role) {
                        UserObj.MainRoleEnum.ADMIN.name -> UserObj.MainRoleEnum.ADMIN.description
                        UserObj.MainRoleEnum.READER.name -> UserObj.MainRoleEnum.READER.description
                        UserObj.MainRoleEnum.MASTER.name -> UserObj.MainRoleEnum.MASTER.description
                        UserObj.MainRoleEnum.CUSTOM.name -> UserObj.MainRoleEnum.CUSTOM.description
                        else -> EMPTY
                    }
                }
                btChangeData.isEnabled = it.digitalId?.p2Approved ?: false

                if (it.status == MULTICHANNEL) {
                    presenter.getAdditionalInfo()
                } else {
                    cardAdditionalInfo.gone()
                    hideProgressScreen()
                }
            }
        }
    }

    override fun showAdditionalInfo(
        typeOfCommunication: ArrayList<TypeOfCommunication>,
        contactPreference: ContactPreference?,
        timeOfDay: TimeOfDay?,
        pcdType: PcdType?
    ) {
        binding?.apply {
            communicationText.text = filterCommunication(typeOfCommunication)

            contactText.text = filterAdditionalStrings(contactPreference?.description)

            pcdText.text = filterAdditionalStrings(pcdType?.description)

            contactTimeText.text = filterAdditionalStrings(timeOfDay?.description)
        }
        hideProgressScreen()
    }

    private fun filterAdditionalStrings(description: String?): String {
        return if (description.isNullOrEmpty()) {
            requireContext().getString(R.string.user_data_additional_information_not_defined)
        } else {
            description
        }
    }

    private fun filterCommunication(typeOfCommunication: java.util.ArrayList<TypeOfCommunication>): String {
        if (typeOfCommunication.isEmpty()){
            return requireContext().getString(R.string.user_data_additional_information_not_defined)
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

    override fun showProgressScreen() {
        binding?.apply {
            errorView.root.gone()
            scrollView.gone()
            progressView.root.visible()
        }
    }

    override fun hideProgressScreen() {
        binding?.apply {
            errorView.root.gone()
            progressView.root.gone()
            scrollView.visible()
        }
    }

    override fun logout() {
        baseLogout()
    }

    override fun error() {
        binding?.apply{
            scrollView.gone()
            progressView.root.gone()
            errorView.root.visible()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        presenter.onCleared()
    }

    private fun showPictureBottomSheet() {
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = R.drawable.img_43_selfie,
                title = getString(R.string.user_change_bs_picture_title),
                subtitle = getString(R.string.user_change_bs_picture_subtitle),
                nameBtn2Bottom = getString(R.string.continuar),
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLACK,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK_CENTER,
                statusBtnFirst = false,
                statusBtnClose = false,
                isCancelable = true,
                isFullScreen = false,
                isPhone = false
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            requireActivity().startActivity<UserDataChangeActivity>(
                                ARG_CHANGE_USER_DATA_EMAIL to userEmail,
                                ARG_CHANGE_USER_DATA_PHONE to userPhone,
                                ARG_CHANGE_USER_DATA_FOREIGN to userIsForeign
                            )
                            dismiss()
                        }

                        override fun onSwipeClosed() {
                            dismiss()
                        }
                    }
            }.show(
                childFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }
}