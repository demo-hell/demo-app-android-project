package br.com.mobicare.cielo.login.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent
import kotlinx.android.synthetic.main.item_view_pager_fullscreen_dialog.*

open class ContentClosableModalFragment : BaseFragment() {

    private var closableModalContentObj = CieloInfoDialogContent.PageContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        closableModalContentObj = arguments?.getSerializable(CLOSABLEMODALFRAGMENT) as
                CieloInfoDialogContent.PageContent
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_view_pager_fullscreen_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instrucao_1.text = closableModalContentObj.title
        instrucao_2.text = closableModalContentObj.subTitle

        if (closableModalContentObj.imageDrawable != -1) {
            imageCieloDialogContent.visibility = View.VISIBLE
            imageCieloDialogContent.setImageDrawable(ContextCompat
                    .getDrawable(requireContext(), closableModalContentObj.imageDrawable))
        } else {
            imageCieloDialogContent.visibility = View.GONE
        }
    }

    companion object {
        private const val CLOSABLEMODALFRAGMENT = "closableModalFragment"

        fun newInstance(closableDialogFragment: CieloInfoDialogContent.PageContent):
                ContentClosableModalFragment {
            val fragmentFirst = ContentClosableModalFragment()
            val args = Bundle()
            args.putSerializable(CLOSABLEMODALFRAGMENT, closableDialogFragment)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}