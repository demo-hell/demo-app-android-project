package br.com.mobicare.cielo.pagamentoLink.delivery

import android.os.Bundle
import android.text.SpannableString
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MESSAGE_INFO_ACTIVITY
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_TITLE_INFO_ACTIVITY
import kotlinx.android.synthetic.main.activity_acceptance_term.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class InfoActivity : AppCompatActivity() {

    @StringRes
    var titleResId: Int = R.string.acceptance_title

    @StringRes
    var messageResId: Int = R.string.acceptance_term

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceptance_term)
        loadParams()
        init()
    }

    private fun loadParams() {
        this.intent?.extras?.let {
            it.getInt(ARG_PARAM_TITLE_INFO_ACTIVITY, R.string.acceptance_title).let {
                this.titleResId = it
            }
            it.getInt(ARG_PARAM_MESSAGE_INFO_ACTIVITY, R.string.acceptance_term).let {
                this.messageResId = it
            }
        }
    }

    private fun init() {
        val sourceMessage = if (this.messageResId == R.string.acceptance_term) {
            getMessageFromFile()
        }
        else {
            getString(this.messageResId)
        }

        textViewTitle?.text = getString(this.titleResId)
        textViewCrossClose.setOnClickListener { finish() }
        var text = SpannableString(HtmlCompat
                .fromHtml(sourceMessage,
                        HtmlCompat.FROM_HTML_MODE_LEGACY))
        textViewTerm.setText(text, TextView.BufferType.SPANNABLE)
    }

    private fun getMessageFromFile() : String {
        val termsString = StringBuilder()
        val reader: BufferedReader
        try {
            reader = BufferedReader(
                    InputStreamReader(assets.open("texts/terms.txt")))
            var str: String?
            while (reader.readLine().also { str = it } != null) {
                termsString.append(str)
            }
            reader.close()
            return termsString.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onBackPressed() = Unit
}
