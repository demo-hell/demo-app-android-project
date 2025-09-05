package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.updatePadding
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult

class PixExtractHeaderViewSection(
    private val fragment: PixHomeExtractFragment,
    private val viewModel: PixHomeExtractViewModel,
    headerView: View,
    private val onAccountManagementTap: () -> Unit,
    private val onBankDomicileTap: (() -> Unit)? = null,
) {
    private val ibAccountManagement = headerView.findViewById<ImageButton>(R.id.ibAccountManagement)
    private val ibBankDomicile = headerView.findViewById<ImageButton>(R.id.ibBankDomicile)
    private val tvMerchantNumber = headerView.findViewById<TextView>(R.id.tvMerchantNumber)
    private val tvMerchantName = headerView.findViewById<TextView>(R.id.tvMerchantName)
    private val tvDocument = headerView.findViewById<TextView>(R.id.tvDocument)
    private val shimmerLoading = headerView.findViewById<ViewGroup>(R.id.shimmerLoading)

    init {
        hideShimmerLoading()
        initializeUserDataObserver()
        configureAccountManagementButton()
        configureBankDomicileButton()
        loadUserData()
    }

    private fun loadUserData() {
        viewModel.loadUserData()
    }

    private fun configureAccountManagementButton() {
        ibAccountManagement.setOnClickListener { onAccountManagementTap() }
    }

    private fun configureBankDomicileButton() {
        ibBankDomicile.apply {
            onBankDomicileTap?.let {
                setOnClickListener { it() }
            }.ifNull {
                gone()
            }
        }
    }

    private fun initializeUserDataObserver() {
        viewModel.userDataUiResult.observe(fragment.viewLifecycleOwner) { result ->
            when (result) {
                is UserDataUiResult.WithMerchant -> configureMerchant(result)
                is UserDataUiResult.WithDocument -> configureDocument(result)
                is UserDataUiResult.WithMerchantAndDocument -> configureMerchantAndDocument(result)
                is UserDataUiResult.WithOnlyOptionalUserName -> configureMerchantName(result.username)
            }
        }
    }

    private fun configureMerchantAndDocument(result: UserDataUiResult.WithMerchantAndDocument) {
        configureMerchantName(result.username)
        tvMerchantNumber.text = getMerchantNumber(result.merchant)
        tvDocument.text = result.document
    }

    private fun configureMerchant(result: UserDataUiResult.WithMerchant) {
        configureMerchantName(result.username)
        tvMerchantNumber.apply {
            text = getMerchantNumber(result.merchant)
            updatePadding(top = fragment.resources.getDimensionPixelOffset(R.dimen.dimen_8dp))
        }
        tvDocument.gone()
    }

    private fun configureDocument(result: UserDataUiResult.WithDocument) {
        configureMerchantName(result.username)
        tvDocument.text = result.document
        tvMerchantNumber.gone()
    }

    private fun configureMerchantName(userName: String?) {
        tvMerchantName.text = userName
    }

    private fun hideShimmerLoading() {
        shimmerLoading.gone()
    }

    private fun getMerchantNumber(value: String) = fragment.getString(R.string.pix_extract_merchant_number, value)

}