package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import android.view.View
import android.widget.Toast
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.showCustomToast
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionKeysBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.ui.keys.onboarding.PixKeysOnboardingActivity
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.MasterKeyUiState
import org.jetbrains.anko.startActivity

class PixMyKeysViewSection(
    fragment: PixHomeFragment,
    viewModel: PixHomeViewModel,
    private val binding: IncludePixHomeSectionKeysBinding
) : PixHomeViewSection(fragment, viewModel), ViewModelResultHandler<MasterKeyUiState> {

    init {
        binding.apply {
            btnManageKeys.setOnClickListener(::onManageKeysClick)
            containerKeyCopy.setOnClickListener(::onCopyToClipboardClick)
            btnReload.setOnClickListener(::onTryAgainClick)
        }
        configureTagsStyle()
    }

    override fun handleObservableResult(value: MasterKeyUiState) {
        when (value) {
            is MasterKeyUiState.Loading -> showMasterKeyLoading()
            is MasterKeyUiState.Error -> showMasterKeyError()
            is MasterKeyUiState.MasterKeyFound -> configureMasterKey(value)
            is MasterKeyUiState.MasterKeyNotFound -> configureWithoutMasterKey(value)
            else -> showMasterKeyError()
        }
    }

    private fun onManageKeysClick(v: View) {
        if (viewModel.wasOnboardingPixKeysViewed) {
            navigateToMyKeys()
        } else {
            navigateToKeysOnboarding()
        }
    }

    private fun navigateToKeysOnboarding() {
        activity.startActivity<PixKeysOnboardingActivity>()
    }

    private fun onCopyToClipboardClick(v: View) {
        keysStore.masterKey?.key?.let { key ->
            Utils.copyToClipboard(activity, key, showMessage = false)

            Toast(activity).showCustomToast(
                activity = activity,
                message = getString(R.string.pix_home_key_copied),
                leadingIcon = R.drawable.ic_symbol_check_round_filled_24dp,
                leadingIconColor = R.color.color_01E17B,
                backgroundColor = R.color.cloud_900,
                cornerRadius = R.dimen.dimen_12dp
            )
        }
    }

    private fun onTryAgainClick(v: View) {
        viewModel.loadMasterKey()
    }

    private fun showMasterKeyLoading() {
        binding.apply {
            content.gone()
            error.gone()
            shimmerLoading.visible()
        }
    }

    private fun configureMasterKey(result: MasterKeyUiState.MasterKeyFound) {
        binding.apply {
            shimmerLoading.gone()
            content.visible()
            containerNoMainKeyMessage.gone()
            containerKey.visible()
            tvKey.text = context
                .getString(R.string.pix_home_random_key, result.masterKey?.key)
                .fromHtml()
        }
    }

    private fun configureWithoutMasterKey(result: MasterKeyUiState.MasterKeyNotFound) {
        binding.apply {
            shimmerLoading.gone()
            content.visible()
            containerKey.gone()
            containerNoMainKeyMessage.visible()
        }
    }

    private fun showMasterKeyError() {
        binding.apply {
            shimmerLoading.gone()
            error.visible()
        }
    }

    private fun configureTagsStyle() {
        binding.apply {
            listOf(tagMainKey, tagActive).forEach {
                it.setCustomDrawable {
                    solidColor = R.color.purple_alpha_16
                    radius = R.dimen.dimen_8dp
                }
            }
        }
    }

}