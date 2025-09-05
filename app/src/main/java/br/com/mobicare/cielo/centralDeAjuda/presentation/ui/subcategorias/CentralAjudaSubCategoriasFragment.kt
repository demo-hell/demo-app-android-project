package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_QUESTIONS
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.buildScreenViewPath
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.SubCategorie
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.WebviewActivity
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentSubCartegoriesBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.faleConosco.FaleConoscoActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.item_list_text_line.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class CentralAjudaSubCategoriasFragment : BaseFragment(), CentralAjudaSubCatregoriasContract.View {

    private var actionListener: ActivityStepCoordinatorListener? = null
    private var categoryName: String = EMPTY
    private var binding: FragmentSubCartegoriesBinding? = null

    val presenter: CentralAjudaSubCategoriasPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSubCartegoriesBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        configureContact()
        configureListeners()
        configureRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
    }

    private fun getData() {
        arguments?.let {
            it.getString(ARG_PARAM_CATEGORY_ID)?.let { itCategoryId ->
                it.getString(ARG_PARAM_CATEGORY_NAME)?.let { itCategoryName ->
                    categoryName = itCategoryName
                    actionListener?.setTitle(itCategoryName)
                    presenter.loadSubCategories(itCategoryId, itCategoryName)
                }
            }
        }
    }

    private fun configureContact() {
        binding?.includeContactUs?.btnContactUs?.setOnClickListener {
            configBottomSheetContactUs(
                R.drawable.img_did_not_find,
                R.string.title_bottomsheet_have_questions_pix,
                R.string.label_bottomsheet_have_questions_pix
            )

            val spannableString =
                SpannableString(getString(R.string.label_bottomsheet_have_questions_pix))

            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(requireContext(), R.color.brand_400)
                    ds.typeface = Typeface.DEFAULT_BOLD
                }
            }
            spannableString.setSpan(
                clickableSpan,
                TEN,
                THIRTY,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun configureRecyclerView() {
        binding?.recyclerViewItensSubcategorias?.layoutManager = LinearLayoutManager(this.context)
    }

    private fun configureListeners() {
        buttonUpdate?.setOnClickListener {
            presenter.resubmit()
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached())
            trackError(
                errorCode = error?.code.orEmpty(),
                errorMessage = error?.message.orEmpty(),
            )
            binding?.apply {
                includeError.root.visible()
                cardView.gone()
                progress.root.gone()
            }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext(), true)
    }

    override fun showLoading() {
        if (isAttached())
            binding?.apply {
                cardView.gone()
                includeContactUs.root.gone()
                includeError.root.gone()
                progress.root.visible()
            }
    }

    override fun hideLoading() {
        if (isAttached())
            binding?.apply {
                includeError.root.gone()
                includeContactUs.root.visible(isCategoryPix())
                cardView.visible()
                progress.root.gone()
            }
    }

    private fun isCategoryPix(): Boolean {
        return categoryName == getString(R.string.cielo_facilita_central_de_ajuda_pix)
    }

    override fun showSubCategories(subcategories: List<SubCategorie>) {
        if (isAttached()) {
            val adapter = DefaultViewListAdapter(subcategories)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<SubCategorie> {
                override fun onBind(item: SubCategorie, holder: DefaultViewHolderKotlin) {
                    holder.mView.textItemList.text = item.name
                    holder.mView.setOnClickListener {
                        presenter.onSubCategorySelected(item)
                    }
                }
            })
            binding?.recyclerViewItensSubcategorias?.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener)
            actionListener = this.context as ActivityStepCoordinatorListener
    }

    override fun onDetach() {
        actionListener = null
        super.onDetach()
    }

    override fun goToQuestionSelect(
        faqId: String,
        subCategorieId: String,
        subCategorieName: String
    ) {
        actionListener?.onNextStep(false, Bundle().apply {
            putString(ARG_PARAM_CATEGORY_ID, faqId)
            putString(ARG_PARAM_SUBCATEGORY_ID, subCategorieId)
            putString(ARG_PARAM_SUBCATEGORY_NAME, subCategorieName)
        })
    }

    private fun configBottomSheetContactUs(
        @DrawableRes drawableId: Int,
        @StringRes title: Int,
        @StringRes subtitle: Int
    ) {
        bottomSheetGenericFlui(
            EMPTY,
            drawableId,
            getString(title),
            getString(subtitle),
            getString(R.string.back),
            getString(R.string.text_contact_us),
            statusNameTopBar = false,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            this.onClick = object :
                BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    dismiss()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    requireActivity().startActivity<FaleConoscoActivity>()
                }

                override fun onCancel() {
                    requireActivity().finish()
                }

                override fun onLinkClick(dialog: Dialog) {
                    startActivity(
                        Intent(activity, WebviewActivity::class.java)
                            .apply {
                                putExtra(WebviewActivity.URL, HelpCenter.URL_BACEN)
                                putExtra(WebviewActivity.SCREEN_NAME, HelpCenter.HELP_CENTER)
                                putExtra(WebviewActivity.TITLE, HelpCenter.HELP_CENTER)
                            })
                }
            }
        }.show(
            requireActivity().supportFragmentManager,
            getString(R.string.bottom_sheet_generic)
        )
    }

    private fun trackScreenView()  {
        if (isAttached()) {
            GA4.logScreenView(
                buildScreenViewPath(
                    screenName = HELP_CENTER_QUESTIONS,
                    suffix = categoryName,
                ),
            )
        }
    }

    private fun trackError(
        errorCode: String,
        errorMessage: String,
    ) {
        if (isAttached()) {
            GA4.logException(
                buildScreenViewPath(
                    screenName = HELP_CENTER_QUESTIONS,
                    suffix = categoryName,
                ),
                errorCode,
                errorMessage,
            )
        }
    }

    companion object {
        fun newInstance(bundle: Bundle) =
            CentralAjudaSubCategoriasFragment().apply {
                this.arguments = bundle
            }
    }
}