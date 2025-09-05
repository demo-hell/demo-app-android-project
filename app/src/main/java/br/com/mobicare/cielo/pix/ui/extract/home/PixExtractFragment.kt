package br.com.mobicare.cielo.pix.ui.extract.home

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.spannable.SpannableLink
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.validateMessageErrorPix
import br.com.mobicare.cielo.databinding.FragmentPixExtractBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.IS_POSSIBLE_CHANGE_PIX_ARGS
import br.com.mobicare.cielo.pix.ui.extract.adapter.PixExtractViewPageAdapter
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.android.material.tabs.TabLayoutMediator
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixExtractFragment : BaseFragment(), CieloNavigationListener, PixExtractContract.View {

    private val presenter: PixExtractPresenter by inject {
        parametersOf(this)
    }

    private var isShowBalanceValue = true
    private var _binding: FragmentPixExtractBinding? = null
    private val binding get() = _binding

    var balance: String? = null
    private var navigation: CieloNavigation? = null
    private var isChangeAccount = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixExtractBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        setupNavigation()
        loadInformation()
    }

    private fun isHome() = navigation?.getData() as? Boolean ?: false

    private fun loadInformation() {
        if (isAdded) {
            getBalance()
            setupHeader()
            setupView()
        }
    }

    private fun setupHeader() {
        if (isHome()) {
            presenter.onGetUserData()
            binding?.containerHeader?.visible()
        } else
            binding?.containerHeader?.gone()

        isChangeAccount =
            navigation?.getDataIntent()?.getBooleanExtra(IS_POSSIBLE_CHANGE_PIX_ARGS, false)
                ?: false
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            val title = if (isHome()) R.string.text_toolbar_home_pix
            else R.string.screen_toolbar_text_extract

            navigation?.setTextToolbar(getString(title))
            navigation?.showContainerButton()
            navigation?.showHelpButton(isHome())
            navigation?.showContent()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        setupViewPager()
        changeColorTextHeader()
        setupShowBalanceValue(true)
        setupListeners()
    }

    private fun setupListeners() {
        listenerShowBalanceClick()
        listenerChangeAccountClick()
    }

    private fun setupUsername(userName: String?) {
        if (isAdded)
            binding?.tvClientNameHeader?.text = getString(
                R.string.text_client_name_pix,
                userName ?: EMPTY
            )
    }

    private fun userDataVisibility(isShow: Boolean = true) {
        if (isAdded) {
            binding?.ivDocumentHeader?.visible(isShow)
            binding?.tvClientDocumentHeader?.visible(isShow)
        }
    }

    override fun onSuccessGetPixBalance(card: Card, isShow: Boolean) {
        if (isAdded) {
            binding?.includeShowPixBalance?.root?.visible()
            binding?.includeShowPixBalance?.containerPixBalanceValue?.visible()
            isShowBalanceValue = isShow

            setupShowBalanceValue(isShowBalanceValue)
            balance = card.balance.toPtBrRealString(isPrefix = false)
            binding?.includeShowPixBalance?.tvPixBalanceValue?.text = balance
            balance?.let {
                (binding?.viewPagerExtract?.adapter as PixExtractViewPageAdapter).setSelectedFragmentBalance(it)
            }
        }
    }

    override fun onShowLoadingBalance() {
        if (isAdded) {
            binding?.includeErrorBalance?.root?.gone()
            binding?.includeShowPixBalance?.root?.visible()
            binding?.includeShowPixBalance?.progressBarPixBalance?.visible()
            binding?.includeShowPixBalance?.containerPixBalanceValue?.gone()
        }
    }

    override fun onHideLoadingBalance() {
        if (isAdded) {
            binding?.includeShowPixBalance?.root?.gone()
            binding?.includeShowPixBalance?.progressBarPixBalance?.gone()
        }
    }

    override fun onErrorGetPixBalance(errorMessage: ErrorMessage?) {
        if (isAdded) {
            binding?.includeErrorBalance?.root?.visible()
            binding?.includeErrorBalance?.tvErrorLoadingPix?.text = validateMessageErrorPix(
                errorMessage,
                requireActivity(),
                getString(R.string.text_pix_try_again_message)
            )
            binding?.includeErrorBalance?.containerTryAgain?.setOnClickListener {
                getBalance()
            }
        }
    }

    private fun getBalance() {
        presenter.onGetCard()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onUserData(userName: String?, document: String, ec: String) {
        userDataVisibility()
        setupUsername(userName)
        val ecFormatted = getString(R.string.text_client_ec_pix, ec)
        if (isAdded)
            binding?.tvClientDocumentHeader?.text =
                getString(R.string.text_client_document_pix, document, ecFormatted)
    }

    override fun onUserDataHideDocument(userName: String?, ec: String) {
        userDataVisibility()
        setupUsername(userName)
        if (isAdded)
            binding?.tvClientDocumentHeader?.text = getString(R.string.text_client_ec_pix, ec)
    }

    override fun onUserDataHideEC(userName: String?, document: String) {
        userDataVisibility()
        setupUsername(userName)
        if (isAdded)
            binding?.tvClientDocumentHeader?.text = document
    }

    override fun onUserDataHideDocumentAndEC(userName: String?) {
        userDataVisibility(false)
        setupUsername(userName)
    }

    private fun changeColorTextHeader() {
        if (isAdded)
            binding?.tvPixExplanationHeader?.text = SpannableStringBuilder().apply {
                if (isChangeAccount) {
                    append(
                        getString(R.string.text_pix_extract_home_header)
                            .addSpannable(
                                TextAppearanceSpan(
                                    requireContext(),
                                    R.style.Paragraph_200_Bold_12_400_white
                                ),
                                SpannableLink({

                                }, false)
                            ), ZERO, SEVENTY_NINE
                    )

                    append(
                        getString(R.string.text_pix_extract_home_header)
                            .addSpannable(
                                TextAppearanceSpan(
                                    requireContext(),
                                    R.style.Paragraph_200_Bold_12_display_400_pistachio_300
                                ),
                                SpannableLink({
                                }, false)
                            ), EIGHTY, HUNDRED_THIRTYNINETH
                    )
                } else
                    append(
                        getString(R.string.text_pix_extract_home_header_initial)
                            .addSpannable(
                                TextAppearanceSpan(
                                    requireContext(),
                                    R.style.Paragraph_200_Bold_12_400_white
                                ),
                                SpannableLink({

                                }, false)
                            ), ZERO, SEVENTY_NINE
                    )
            }
    }

    private fun setupShowBalanceValue(isShow: Boolean) {
        if (isAdded) {
            if (isShow) {
                binding?.includeShowPixBalance?.ivPixBalanceShow?.setBackgroundResource(R.drawable.ic_eye)
                binding?.includeShowPixBalance?.tvPixBalanceValue?.visible()
                binding?.includeShowPixBalance?.ivPixBalanceValue?.gone()
            } else {

                binding?.includeShowPixBalance?.ivPixBalanceShow?.setBackgroundResource(R.drawable.ic_eye_off)
                binding?.includeShowPixBalance?.tvPixBalanceValue?.gone()
                binding?.includeShowPixBalance?.ivPixBalanceValue?.visible()
            }
        }
    }

    private fun listenerChangeAccountClick() {
        if (isChangeAccount)
            binding?.tvPixExplanationHeader?.setOnClickListener {
                findNavController().navigate(
                    PixExtractFragmentDirections.actionPixExtractFragmentToPixTransitoryAccountManagementFragment()
                )
            }
    }

    private fun listenerShowBalanceClick() {
        binding?.includeShowPixBalance?.ivPixBalanceShow?.setOnClickListener {
            isShowBalanceValue = isShowBalanceValue.not()
            presenter.onSaveShowBalanceValue(isShowBalanceValue)
            setupShowBalanceValue(isShowBalanceValue)
        }
    }

    private fun setupViewPager() {
        val titlesArray = resources.getStringArray(R.array.titles_page_pix_extract).toList()
        binding?.let { itBinding ->
            itBinding.viewPagerExtract.apply {
                if (adapter == null)
                    adapter = PixExtractViewPageAdapter(requireActivity(), titlesArray)
            }
            TabLayoutMediator(itBinding.tabExtract, itBinding.viewPagerExtract) { tab, position ->
                tab.text = titlesArray[position]
            }.attach()
        }
    }

    override fun onHelpButtonClicked() {
        requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
            ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_PIX,
            ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.cielo_facilita_central_de_ajuda_pix),
            CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true
        )
    }

    override fun onFilterButtonClicked() {
        findNavController().navigate(PixExtractFragmentDirections.actionPixExtractFragmentToPixExtractFilterFragment())
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finishAndRemoveTask()
        return super.onBackButtonClicked()
    }
}