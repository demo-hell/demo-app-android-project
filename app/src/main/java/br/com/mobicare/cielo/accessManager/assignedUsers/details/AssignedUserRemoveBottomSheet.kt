package br.com.mobicare.cielo.accessManager.assignedUsers.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.UnlinkUserReason
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersFragment
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.widget.FullscreenBottomSheetDialog
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.AssignedUserRemoveBottomSheetBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import com.google.android.material.bottomsheet.BottomSheetDialog

class AssignedUserRemoveBottomSheet : FullscreenBottomSheetDialog() {

    lateinit var listener: AccessManagerAssignedUsersContract.View
    lateinit var user: AccessManagerUser
    lateinit var binding: AssignedUserRemoveBottomSheetBinding
    private var selectedReason: UnlinkUserReason? = null

    companion object {
        fun onCreate(
            user: AccessManagerUser,
            listener: AccessManagerAssignedUsersFragment,
        ) = AssignedUserRemoveBottomSheet().apply {
            this.user = user
            this.listener = listener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = AssignedUserRemoveBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.isDraggable = false
        setupView()
    }

    private fun setupView() {
        setMessageText()
        setupRemoveBtnStyle(selectedReason != null)
        binding.btnRemoveProceed.setOnClickListener {
            selectedReason?.let { reason ->
                listener.onRemoveConfirmed(user.id.toString(), reason)
                dismiss()
            }
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
        binding.btnReturn.setOnClickListener {
            dismiss()
        }
        binding.reasonsList.radioGroupAccountType.setOnCheckedChangeListener { radioGroup, i ->
            val selectedView = radioGroup.findViewById<AppCompatRadioButton>(i)
            onReasonSelected(radioGroup.indexOfChild(selectedView))
        }
    }

    private fun setMessageText() {
        val document = UserPreferences.getInstance().userInformation?.activeMerchant?.cnpj?.number
        val isCnpj = document?.let {
            it.length > CPF_MASK.length
        } == true
        binding.tvMessage.text = getString(
            if (isCnpj) R.string.assigned_user_remove_message_cnpj else R.string.assigned_user_remove_message_cpf,
            user.name.capitalizeWords(),
            document
        )
    }

    private fun onReasonSelected(selectedIndex: Int) {
        selectedReason = when (selectedIndex) {
            ZERO -> UnlinkUserReason.IS_NOT_EMPLOYEE_ANYMORE
            ONE -> UnlinkUserReason.ACCESS_NOT_REQUIRED_ANYMORE
            TWO -> UnlinkUserReason.UNKNOWN_USER
            else -> null
        }
        val isReasonSelected = selectedReason != null
        if (isReasonSelected.not())
            Toast.makeText(
                context,
                getString(R.string.assigned_user_bs_remove_select_a_reason),
                TOAST_MS
            )
        setupRemoveBtnStyle(isReasonSelected)
    }

    private fun setupRemoveBtnStyle(isReasonSelected: Boolean) {
        binding.btnRemoveProceed.isEnabled = isReasonSelected
        binding.btnRemoveProceed.alpha = if (isReasonSelected) ENABLED_ALPHA else DISABLED_ALPHA
    }
}