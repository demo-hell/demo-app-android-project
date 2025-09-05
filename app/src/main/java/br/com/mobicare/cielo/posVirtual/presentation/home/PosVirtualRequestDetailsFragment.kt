package br.com.mobicare.cielo.posVirtual.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.databinding.FragmentPosVirtualRequestDetailsBinding
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.SCREEN_VIEW_REQUEST_DETAILS
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct
import br.com.mobicare.cielo.posVirtual.presentation.home.enum.PosVirtualProductUiContent
import br.com.mobicare.cielo.posVirtual.presentation.home.enum.PosVirtualRequestDetailsUiContent
import org.koin.android.ext.android.inject

class PosVirtualRequestDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPosVirtualRequestDetailsBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: PosVirtualRequestDetailsFragmentArgs by navArgs()

    private val product: PosVirtualProduct by lazy { args.posvirtualproduct }
    private val merchantId: String by lazy { args.merchantid }

    private val productContent
        get() =
            product.id?.let { PosVirtualProductUiContent.find(it) }

    private val requestDetailsContent
        get() =
            product.status?.let { PosVirtualRequestDetailsUiContent.find(it) }

    private val isPending get() = product.status == PosVirtualStatus.PENDING

    private val ga4: PosVirtualAnalytics by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPosVirtualRequestDetailsBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupDescription()
        setupServiceLabel()
        setupFieldValues()
        setupBottomSection()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation)
            navigation?.apply {
                setNavigationListener(this@PosVirtualRequestDetailsFragment)
                showButton(false)
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        toolbarTitle = getString(R.string.pos_virtual_request_details_title)
                    )
                )
            }
        }
    }

    private fun setupDescription() {
        binding?.apply {
            if (isPending)
                tvDescription.apply {
                    append(ONE_SPACE)
                    append(getString(R.string.pos_virtual_request_details_description_notify_by_email))
                }
        }
    }

    private fun setupServiceLabel() {
        binding?.apply {
            tvLabelService.text = getString(
                if (isPending) R.string.pos_virtual_request_details_label_service
                else R.string.service
            )
        }
    }

    private fun setupFieldValues() {
        binding?.apply {
            productContent?.let {
                tvService.setText(it.title)
                ivIconService.setImageResource(it.icon)
            }
            requestDetailsContent?.let {
                tvStatus.setText(it.statusText)
                ivIconStatus.setImageResource(it.statusIcon)
            }
        }
    }

    private fun setupBottomSection() {
        binding?.apply {
            if (isPending)
                tvMerchantNumber.text = merchantId
            else
                btnCallCenter.setOnClickListener(::onMakeCall)

            sectionGeneralInformation.visible(isPending)
            sectionRehabilitateService.visible(isPending.not())
        }
    }

    private fun onMakeCall(view: View) =
        CallHelpCenterBottomSheet.newInstance().show(childFragmentManager, tag)

    private fun logScreenView() {
        ga4.logScreenView(
            String.format(
                SCREEN_VIEW_REQUEST_DETAILS,
                productContent?.title?.let { getString(it).normalizeToLowerSnakeCase() } ?: EMPTY
            )
        )
    }

}