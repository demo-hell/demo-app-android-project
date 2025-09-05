package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.IdOnboardingCpfAlreadyUsedBsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IDOnboardingCpfAlreadyUsedBS : BottomSheetDialogFragment() {

    private var _binding: IdOnboardingCpfAlreadyUsedBsBinding? = null
    private val binding get() = _binding

    private var cpf: String? = null
    private var listener: IDOnboardingNewCpfFragment? = null

    companion object {
        fun onCreate(cpf: String, listener: IDOnboardingNewCpfFragment) =
            IDOnboardingCpfAlreadyUsedBS().apply {
                this.cpf = cpf
                this.listener = listener
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        _binding = IdOnboardingCpfAlreadyUsedBsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    fun setupView() {
        cpf?.let {
            binding?.tvTitle?.text = getString(
                R.string.id_onboarding_cpf_already_used_title,
                it.addMaskCPForCNPJ(resources.getString(R.string.mask_cpf_step4))
            )
        }
    }

    fun setupListeners() {
        binding?.tvLogoutAction?.setOnClickListener {
            dismiss()
            requireActivity().finish()
            listener?.baseLogout()
        }

        binding?.tvHelpCenterAction?.setOnClickListener {
            CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)
        }

        binding?.tvReturnAction?.setOnClickListener {
            listener?.clearCpf()
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}