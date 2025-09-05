package br.com.mobicare.cielo.selfRegistration.passwordpolitic

import android.os.Bundle
import android.text.SpannableString
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.activity_password_politic.*

class PasswordPoliticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_politic)
        init()
    }

    private fun init() {
        imageViewCross.setOnClickListener { onBackPressed() }

        val text = SpannableString(HtmlCompat
                .fromHtml(getString(R.string.password_politic_requirementes_content),
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        textViewRequirementsContent.setText(text, TextView.BufferType.SPANNABLE)
    }
}
