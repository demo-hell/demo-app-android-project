package br.com.mobicare.cielo.commons.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.fragment_cielo_info_with_action.*

class CieloInfoWithActionFragment : Fragment() {


    lateinit var title: String
    lateinit var description: String
    lateinit var buttonLabel: String

    companion object {

        fun create(title: String, description: String, buttonLabel: String):
                CieloInfoWithActionFragment {
            return CieloInfoWithActionFragment().apply {
                this.title = title
                this.description = description
                this.buttonLabel = buttonLabel
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (this::title.isInitialized) {
            tv_aguardando_aprovacao_title.text = SpannableStringBuilder.valueOf(this.title)
        }

        if (this::description.isInitialized) {
            tv_aguardando_aprovacao_subtitle.text = SpannableStringBuilder.valueOf(this.description)
        }

        if (this::buttonLabel.isInitialized) {
            buttonInfoAction.setText(this.buttonLabel)
        }

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cielo_info_with_action,
                container,
                false)
    }
}
