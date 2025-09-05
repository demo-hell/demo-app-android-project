package br.com.mobicare.cielo.pixMVVM.presentation.router.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.deeplink.MEU_CADASTRO_TITLE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.spannable.LinkSpan
import br.com.mobicare.cielo.commons.utils.spannable.addLinksToText
import br.com.mobicare.cielo.databinding.FragmentPixBlockPennyDropBinding
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity.NavArgs
import org.jetbrains.anko.startActivity

class PixBlockPennyDropFragment :
    BaseFragment(),
    CieloNavigationListener {
    private var binding: FragmentPixBlockPennyDropBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixBlockPennyDropBinding
        .inflate(
            inflater,
            container,
            false,
        ).also { binding = it }
        .root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        setupListeners()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation =
                (requireActivity() as CieloNavigation).apply {
                    setNavigationListener(this@PixBlockPennyDropFragment)
                    showToolbar(false)
                    showButton(false)
                }
        }
    }

    private fun setupView() {
        binding?.apply {
            tvLinkMyCadastre.apply {
                text =
                    getString(R.string.pix_block_penny_drop_link_my_cadastre).addLinksToText(
                        requireContext(),
                        listOf(
                            LinkSpan(
                                text = ACCESS_THE_MY_REGISTRATION_PAGE,
                                onClick = ::onClickLinkMyCadastre,
                                textColor = R.color.neutral_main,
                                typeFace = R.font.montserrat_bold,
                            ),
                        ),
                    )
                movementMethod = LinkMovementMethod.getInstance()
            }

            tvLinkChangeTypeAccount.apply {
                text =
                    getString(R.string.pix_block_penny_drop_link_change_type_account).addLinksToText(
                        requireContext(),
                        listOf(
                            LinkSpan(
                                text = ACCESS_THE_RECEIPTS_AREA,
                                onClick = ::onClickLinkChangeTypeAccount,
                                textColor = R.color.neutral_main,
                                typeFace = R.font.montserrat_bold,
                            ),
                        ),
                    )
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            ivBtnClose.setOnClickListener(::onClose)
            btnClose.setOnClickListener(::onClose)
        }
    }

    private fun onClickLinkMyCadastre() {
        requireActivity().apply {
            startActivity<RouterFragmentInActivity>(
                FRAGMENT_TO_ROUTER to MeuCadastroFragmentAtualNovo::class.java.canonicalName,
                TITLE_ROUTER_FRAGMENT to MEU_CADASTRO_TITLE,
            )
            finish()
        }
    }

    private fun onClickLinkChangeTypeAccount() {
        requireActivity().apply {
            startActivity<PixAccountNavigationFlowActivity>(NavArgs.ACCESSED_THROUGH_PENNY_DROP_ARGS to true)
            finish()
        }
    }

    private fun onClose(view: View) {
        requireActivity().finish()
    }

    companion object {
        const val ACCESS_THE_MY_REGISTRATION_PAGE = "acesse a página de meu cadastro"
        const val ACCESS_THE_RECEIPTS_AREA = "acesse a área de recebimentos"
    }
}
