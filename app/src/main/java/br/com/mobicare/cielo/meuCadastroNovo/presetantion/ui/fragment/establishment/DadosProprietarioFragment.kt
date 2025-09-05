package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.recycler.MarginItemDecoration
import br.com.mobicare.cielo.databinding.McnFragmentDadosProprietarioBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.OwnersAdapter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ESTABLISHMENT_ANALYTICS
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MY_REGISTER
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.DATA_UPDATE
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_0f
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment.Companion.VALUE_ROTATION_90f
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DadosProprietarioFragment : BaseFragment(), AlertaCadastralContract.View, ShowLayoutListener {

    private val presenter: DadosProprietarioPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private lateinit var owners: ArrayList<Owner>
    private var showAlert = false
    private var isEditBlocked = false

    private lateinit var _binding: McnFragmentDadosProprietarioBinding
    val binding: McnFragmentDadosProprietarioBinding get() = _binding

    companion object {
        const val OWNER = "owner"
        const val ARGS_SHOW_ALERT = "ARGS_SHOW_ALERT"
        const val EDIT_BLOCK = "edit_block"
        const val PADDING_HORIZONTAL_32 = 32
        const val PADDING_VERTICAL_0 = 0

        fun create(owners: ArrayList<Owner>, showAlert: Boolean, isEditBlocked: Boolean) =
            DadosProprietarioFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(OWNER, owners)
                    putBoolean(ARGS_SHOW_ALERT, showAlert)
                    putBoolean(EDIT_BLOCK, isEditBlocked)
                }
            }
    }

    private val reciever: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            goToConfirmData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            it.getParcelableArrayList<Owner>(OWNER)?.let { itOwners -> owners = itOwners }
            showAlert = it.getBoolean(ARGS_SHOW_ALERT)
            isEditBlocked = it.getBoolean(EDIT_BLOCK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = McnFragmentDadosProprietarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(reciever, IntentFilter(DATA_UPDATE))

        val ownersAdapter = OwnersAdapter(requireContext(), owners).also {
            it.setAction {
                showInformationDialog()
            }
        }

        binding.apply {
            rvOwner.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = ownersAdapter

                setPadding(
                    PADDING_HORIZONTAL_32,
                    PADDING_VERTICAL_0,
                    PADDING_HORIZONTAL_32,
                    PADDING_VERTICAL_0
                )

                if (onFlingListener == null) PagerSnapHelper().attachToRecyclerView(this)

                addItemDecoration(
                    MarginItemDecoration(
                        resources.getDimensionPixelSize(R.dimen.dimen_16dp),
                        DividerItemDecoration.HORIZONTAL
                    )
                )
            }

            constraintViewTitlePro.setOnClickListener {
                if (constraintViewDetailsOwner.visibility == View.VISIBLE) {
                    gaSendInteraction(textTitle.text.toString())

                    chevronIcon.animate()?.rotation(VALUE_ROTATION_0f)?.start()
                    constraintViewDetailsOwner.gone()

                } else {
                    chevronIcon.animate()?.rotation(VALUE_ROTATION_90f)?.start()
                    constraintViewDetailsOwner.visible()
                }
            }

            if (owners.isEmpty().not()) {
                contentMsgErrorDp.gone()
                rvOwner.visible()

                if (showAlert) {
                    alertIcon.visible()
                    alertButton.visible()

                    alertButton.setOnClickListener {
                        showConfirmDialog()
                    }
                }
            } else {
                contentMsgErrorDp.visible()
                rvOwner.gone()
            }
        }

    }

    private fun showInformationDialog() {
        val dialog = CieloDialog.create(
            title = getString(R.string.title_owner_update_dialog),
            message = getString(R.string.text_update_dialog))
            .setTitleTextAppearance(R.style.bold_montserrat_16)
            .setMessageTextAppearance(R.style.regular_montserrat_14_cloud_500)
            .setPrimaryButton(getString(R.string.entendi))
        activity?.supportFragmentManager?.let {
            dialog.show(it, null)
        }
    }

    private fun showConfirmDialog() {
        val mAlertDialog = CieloAskQuestionDialogFragment.Builder()
            .title(getString(R.string.text_title_dialog_no_protocol))
            .message(getString(R.string.dialog_confirm_data_message))
            .positiveTextButton(getString(R.string.text_yes_label))
            .cancelTextButton(getString(R.string.text_no_label))
            .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
            .onPositiveButtonClickListener {
                goToConfirmData()
            }.build().also { itDialog ->
                itDialog.onCancelButtonClickListener = View.OnClickListener {
                    itDialog.dismiss()
                }
            }

        activity?.supportFragmentManager?.let {
            mAlertDialog.show(it, null)
        }
    }

    private fun goToConfirmData() {
        owners?.firstOrNull()?.let {
            validationTokenWrapper.generateOtp(
                onResult = { otpCode ->
                    presenter.submitOwnerData(otpCode, it)
                }
            )
        }
    }

    private fun gaSendInteraction(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MY_REGISTER),
                action = listOf(ESTABLISHMENT_ANALYTICS),
                label = listOf(Label.INTERACAO, labelButton)
            )
        }
    }

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun removeAlertMessage() {}

    override fun addAlertMessage() {}

    override fun showError(error: ErrorMessage?) {
        if (isAttached())
            bottomSheetGenericFlui(
                EMPTY,
                R.drawable.ic_07,
                getString(R.string.text_title_generic_error),
                getString(R.string.business_error),
                getString(R.string.ok),
                getString(R.string.ok),
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
                        dialog.dismiss()
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

    override fun showContainer() {
        binding.apply {
            constraintViewDetailsOwner.visible()
            chevronIcon.animate().rotation(VALUE_ROTATION_90f).start()
        }
    }

    override fun closeContainer() {
        binding.apply {
            constraintViewDetailsOwner.gone()
            chevronIcon.animate()?.rotation(VALUE_ROTATION_0f)?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(reciever)
    }
}