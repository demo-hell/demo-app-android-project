package br.com.mobicare.cielo.deeplink

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.TimeUtils
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.newLogin.NewLoginActivity
import kotlinx.android.synthetic.main.deep_link.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * create by Enzo Teles
 * */
class DeepLinkActivity : BaseLoggedActivity(), DeepLinkContract.View, View.OnClickListener {

    val presenter: DeepLinkPresenter by inject {
        parametersOf(this)
    }

    var token: String? = null

    companion object {
        const val DEEP_LINK_TOKEN = "DEEP_LINK_TOKEN"
        fun newIntent(context: Context, token: String) = Intent(context, DeepLinkActivity::class.java).apply {
            putExtra(DEEP_LINK_TOKEN, token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deep_link)
        intent?.extras?.let {
            token = it.getString(DEEP_LINK_TOKEN)
            presenter.verificationEmailConfirmation(token)
        }
        btn_dp_close.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_dp_close -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    /**
     * método que valida a resposta da api no envio de email
     * @param code
     * */
    override fun getResponseSendEmail(errorMessage: ErrorMessage) {
        vf_deep_link.displayedChild = 1
        when (errorMessage.httpStatus) {
            200 -> {
                sendEmailSucess()
            }
            401 -> {
                SessionExpiredHandler.userSessionExpires(this, true)
            }
            403 -> {
                sendExpiredEmailError()
            }
            else -> {
                sendEmailError()
            }
        }
    }

    private fun sendExpiredEmailError() {
        dc_iv_brand.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dp_send_error))
        dp_title_sucess.text = getString(R.string.dp_title_expirado)
        dp_subtitle_sucess.text = getString(R.string.dp_subtitle_expirado)
        ft_btn_ok.setText(getString(R.string.dp_btn_send_expirado))
        ft_btn_ok.setOnClickListener {
            presenter.resendEmail(token)
        }
    }


    /**
     * método que mostra quando o envio de email deu error na api
     * */
    private fun sendEmailError() {
        dc_iv_brand.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dp_send_error))
        dp_title_sucess.text = getString(R.string.dp_title_error)
        dp_subtitle_sucess.text = getString(R.string.dp_subtitle_error)
        ft_btn_ok.setText(getString(R.string.dp_btn_send_error))
        ft_btn_ok.setOnClickListener { finish() }
    }

    /**
     * método que mostra quando o email foi enviado com sucesso
     * */
    override fun sendEmailSucess() {
        vf_deep_link.displayedChild = 1
        dc_iv_brand.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dp_send_email))
        dp_title_sucess.text = getString(R.string.dp_title_sucess)
        dp_subtitle_sucess.text = getTextSendEmailSucess()
        ft_btn_ok.setText(getString(R.string.dp_btn_send))
        ft_btn_ok.setOnClickListener {
            val startIntent = Intent(this, NewLoginActivity::class.java)
            startIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(startIntent)
            finish()
        }
    }

    /**
     * método que mostra a msg de sucesso quando o email foi enviado
     * */
    private fun getTextSendEmailSucess(): CharSequence? {

        val textFinal = SpannableStringBuilder()

        textFinal.append(getString(R.string.dp_subtitle_sucess_01))
        textFinal.append(" ")
        val start = textFinal.length

        textFinal.append(getString(R.string.dp_subtitle_sucess_02))
        textFinal.setSpan(StyleSpan(Typeface.BOLD), start, textFinal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textFinal.append(" ")

        return textFinal
    }

    /**
     * método que valida a resposta da api no reenvio de email
     * @param code
     * */
    override fun getResponseResendEmail(code: Int) {
        vf_deep_link.displayedChild = 1
        when (code) {
            400, 404, in 500..509 -> {
                modalResendEmail(getString(R.string.dp_modal_error_title),
                        getString(R.string.dp_modal_error_msg),
                        null)
            }
            401 -> {
                SessionExpiredHandler.userSessionExpires(this, true)
            }
        }
    }

    /**
     * método que valida a resposta da api no reenvio de email e mostra o modal
     * @param title, msg
     * */
    override fun modalResendEmail(title: String, msg: String, response: MultichannelUserTokenResponse?) {
        vf_deep_link.displayedChild = 1
        response?.tokenExpirationInMinutes?.let {
            val hour = TimeUtils.convertMinutesToHours(it)
            val m = getTextResendEmailSucess(hour)
            showMessage(m, title)
        } ?: run {
            showMessage(msg, title)
        }


    }

    /**
     * método que mostra a msg de sucesso quando o email foi enviado
     * */
    private fun getTextResendEmailSucess(hour: String): String {

        val textFinal = SpannableStringBuilder()

        textFinal.append(getString(R.string.dp_modal_sucess_msg_01))
        textFinal.append(".")
        return textFinal.toString()
    }


}