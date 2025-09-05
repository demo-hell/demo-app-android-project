package br.com.mobicare.cielo.accessManager.customProfile.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileDetailResponse
import br.com.mobicare.cielo.commons.constants.SEPARATOR_POINT
import br.com.mobicare.cielo.commons.constants.WRITE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentAccessManagerCustomProfileDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccessManagerCustomProfileDetailFragment : BottomSheetDialogFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private val binding: FragmentAccessManagerCustomProfileDetailBinding? by viewBinding()

    lateinit var userDetail: AccessManagerCustomProfileDetailResponse
    private var listRead: String = EMPTY
    private var listWrite: String = EMPTY

    companion object {
        fun onCreate(
            userDetail: AccessManagerCustomProfileDetailResponse
        ) = AccessManagerCustomProfileDetailFragment().apply {
            this.userDetail = userDetail
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() },
            isFullScreen = true
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupClickListeners()
        setupView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupClickListeners() {
        binding?.btBackArrow?.setOnClickListener {
            dismiss()
        }
    }

    private fun setupView() {
        binding?.apply {
            userDetail.resources?.forEach {

                if (it.accessTypes.contains(WRITE)) {
                    listWrite +=  it.resourceName + SEPARATOR_POINT
                }else {
                    listRead += it.resourceName + SEPARATOR_POINT
                }
            }

            tvTitleProfile.text = userDetail.name
            tvReadAccessDesc.text = listRead.dropLast(SEPARATOR_POINT.length)
            tvReadWriteAccessDesc.text = listWrite.dropLast(SEPARATOR_POINT.length)
        }
    }
}