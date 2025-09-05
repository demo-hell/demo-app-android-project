package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THOUSAND
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionHeaderBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult

class PixHeaderViewSection(
    fragment: PixHomeFragment,
    viewModel: PixHomeViewModel,
    private val binding: IncludePixHomeSectionHeaderBinding,
    private val onBankDomicileTap: (() -> Unit)? = null,
) : PixHomeViewSection(fragment, viewModel), ViewModelResultHandler<UserDataUiResult> {

    init {
        configureBankDomicileButton()
        setLoadingIndicator(true)
    }

    private fun configureBankDomicileButton() {
        onBankDomicileTap?.let {
            binding.ibBankDomicile.setOnClickListener { it() }
        }.ifNull {
            binding.ibBankDomicile.gone()
        }
    }

    override fun handleObservableResult(value: UserDataUiResult) {
        when (value) {
            is UserDataUiResult.WithMerchant -> configureMerchant(value)
            is UserDataUiResult.WithDocument -> configureDocument(value)
            is UserDataUiResult.WithMerchantAndDocument -> configureMerchantAndDocument(value)
            is UserDataUiResult.WithOnlyOptionalUserName -> configureUserAndHeaderVisibility(value.username, show = false)
        }
    }

    private fun configureMerchantAndDocument(result: UserDataUiResult.WithMerchantAndDocument) {
        setLoadingIndicator(false, delayInMillis = THOUSAND)
        binding.tvDocument.text =
            context.getString(
                R.string.pix_home_client_document,
                result.document,
                context.getString(R.string.pix_home_client_ec, result.merchant)
            )
        configureUserAndHeaderVisibility(result.username)
    }

    private fun configureMerchant(result: UserDataUiResult.WithMerchant) {
        setLoadingIndicator(false, delayInMillis = THOUSAND)
        binding.tvDocument.text =
            context.getString(R.string.pix_home_client_ec, result.merchant)
        configureUserAndHeaderVisibility(result.username)
    }

    private fun configureDocument(result: UserDataUiResult.WithDocument) {
        setLoadingIndicator(false, delayInMillis = THOUSAND)
        binding.tvDocument.text = result.document
        configureUserAndHeaderVisibility(result.username)
    }

    private fun configureUserAndHeaderVisibility(username: String?, show: Boolean = true) {
        binding.apply {
            ivMerchant.visible(show)
            tvDocument.visible(show)
            tvMerchantName.text = username
        }
    }

    private fun setLoadingIndicator(isLoading: Boolean, delayInMillis: Long = ZERO.toLong()) {
        binding.apply {
            root.postDelayed({
                content.visible(isLoading.not())
                shimmerLoading.visible(isLoading)
            }, delayInMillis)
        }
    }

}