package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas


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
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
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
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentCentralAjudaPerguntasBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.faleConosco.FaleConoscoActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_central_ajuda_perguntas.*
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.item_list_text_line.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CentralAjudaPerguntasFragment : BaseFragment(), CentralAjudaPerguntasContract.View {

    val presenter: CentralAjudaPerguntasPresenter by inject {
        parametersOf(this)
    }

    private var actionListener: ActivityStepCoordinatorListener? = null
    private var _binding: FragmentCentralAjudaPerguntasBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCentralAjudaPerguntasBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        configureListeners()
        this.presenter.setView(this)
        this.arguments?.let {
            it.getString(ARG_PARAM_CATEGORY_ID)?.let { itCategoryId ->
                it.getString(ARG_PARAM_SUBCATEGORY_ID)?.let { itSubCategoryId ->
                    it.getString(ARG_PARAM_SUBCATEGORY_NAME)?.let { itSubCategoryName ->
                        this.actionListener?.setTitle(itSubCategoryName)
                        this.presenter.loadQuestions(itCategoryId, itSubCategoryId)
                    }
                }
            }
            it.getString(ARG_PARAM_SUBCATEGORY_NAME)?.let {
                this.actionListener?.setTitle(it)
            }
            it.getString(ConfigurationDef.TAG_KEY_HELP_CENTER)?.let {
                this.presenter.loadQuestionsByName(it)
            }
        }

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
        this.recycler_view_itens_perguntas?.layoutManager = LinearLayoutManager(this.context)
    }

    private fun configureListeners() {
        this.buttonUpdate?.setOnClickListener {
            this.presenter.resubmit()
        }
    }

    private fun visibilityContactUS() {
        arguments?.let {
            it.getString(ARG_PARAM_SUBCATEGORY_NAME)?.let { category ->
                val pixCategory = getString(R.string.cielo_facilita_central_de_ajuda_pix)
                val isShow = pixCategory == category
                binding?.includeContactUs?.root?.visible(isShow)
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            this.include_error?.visibility = View.VISIBLE
            this.card_view?.visibility = View.GONE
            this.progress?.visibility = View.GONE
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            actionListener?.onLogout()
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            this.card_view?.visibility = View.GONE
            this.include_error?.visibility = View.GONE
            this.progress?.visibility = View.VISIBLE
            this.include_contact_us?.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            this.include_error?.visibility = View.GONE
            this.card_view?.visibility = View.VISIBLE
            this.progress?.visibility = View.GONE
            visibilityContactUS()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            this.actionListener = this.context as ActivityStepCoordinatorListener
        }
    }

    override fun showQuestions(questions: List<QuestionDataResponse>) {
        if (isAttached()) {
            val adapter = DefaultViewListAdapter(questions)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<QuestionDataResponse> {
                override fun onBind(item: QuestionDataResponse, holder: DefaultViewHolderKotlin) {
                    holder.mView.textItemList.text = item.question
                    holder.mView.setOnClickListener {
                        this@CentralAjudaPerguntasFragment.presenter.onQuestionSelected(item)
                    }
                }
            })
            this.recycler_view_itens_perguntas?.adapter = adapter
        }
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
            false,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            false,
            TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            ButtonBottomStyle.BNT_BOTTOM_WHITE,
            ButtonBottomStyle.BNT_BOTTOM_BLUE,
            true
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
                    startActivity(Intent(activity, WebviewActivity::class.java)
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

    override fun showQuestionAnswerDetail(obj: QuestionRequestModelView) {
        this.actionListener?.onNextStep(false, Bundle().apply {
            putParcelable(ARG_PARAM_QUESTION_REQUEST, obj)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle?) =
            CentralAjudaPerguntasFragment().apply {
                arguments = bundle
            }
    }
}
