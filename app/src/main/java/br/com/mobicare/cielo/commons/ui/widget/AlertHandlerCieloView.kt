package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.button.CieloRegularBlueButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter.Companion.CONTRACT_IN_PROGRESS
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter.Companion.CONTRACT_NOT_EFFECTED
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter.Companion.CREDIT_NOT_ALLOWED
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter.Companion.EMPTY
import kotlinx.android.synthetic.main.layout_generic_error.view.*
import java.net.HttpURLConnection

class AlertHandlerCieloView @JvmOverloads constructor(context: Context,
                                                      attrs: AttributeSet? = null,
                                                      defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {


    companion object {
        private const val ERROR_NOT_FOUND = "404"

        fun AlertHandlerCieloView.setupInternalServerErrorHandlerCieloView(actionOnClick: () -> Unit) {

            post {
                this.cieloErrorMessage = context.getString(R.string.text_message_generic_error)
                this.cieloErrorTitle = context.getString(R.string.text_title_generic_error)
                this.errorHandlerCieloViewImageDrawable =
                        R.drawable.ic_generic_error_image
            }

            this.errorButton?.setText(context.getString(R.string.text_button_try_again))

            this.errorButton?.setOnClickListener {
                actionOnClick()
            }
        }
    }


    private var errorInflatedView: View? = null

    var errorButton: CieloRegularBlueButton? = null
    private var isActionOnClicked = false

    var cieloErrorMessage: String? = null
        set(errorMessage) {
            setupFieldMessage(errorMessage, R.id.tvMessage)
        }

    var showTitle : Boolean = true
        set(value) {
        field = value
            this.findViewById<TextView>(R.id.tvTitle).visible(field)
        }

    var cieloErrorTitle: String? = null
        set(errorTitle) {
            setupFieldMessage(errorTitle, R.id.tvTitle)
        }

    @DrawableRes
    var errorHandlerCieloViewImageDrawable: Int? = null
        set(errorImageDrawable) {
            setupImageDrawable(errorImageDrawable)
        }

    private fun setupImageDrawable(errorImageDrawable: Int?) {
        errorInflatedView?.run {
            val contentImage = this.findViewById<ImageView>(R.id.imageView14)

            errorImageDrawable?.run {
                contentImage.setImageDrawable(ContextCompat.getDrawable(context, this))
            }
            invalidate()
        }

    }

    private fun setupFieldMessage(fieldMessage: String?, @IdRes fieldId: Int) {
        errorInflatedView?.run {
            val errorMessageView = this.findViewById<TextView>(fieldId)
            errorMessageView.text = SpannableStringBuilder.valueOf(fieldMessage)
            invalidate()
        }
    }

    init {
        errorInflatedView = LayoutInflater.from(context)
                .inflate(R.layout.layout_generic_error, this, true)


        errorInflatedView?.run {
            val retryButton = this
                    .findViewById<CieloRegularBlueButton>(R.id.retryButton)

            errorButton = retryButton

            val contentImage = this.findViewById<ImageView>(R.id.imageView14)
            val errorTitle = this.findViewById<TextView>(R.id.tvTitle)
            val errorMessage = this.findViewById<TextView>(R.id.tvMessage)

            val typedArr = context
                    .obtainStyledAttributes(attrs,
                            R.styleable.AlertHandlerCieloView, 0, 0)

            retryButton.setText(typedArr
                    .getString(R.styleable.AlertHandlerCieloView_cieloErrorButtonLabel) ?: "")

            contentImage.setImageDrawable(typedArr
                    .getDrawable(R.styleable.AlertHandlerCieloView_cieloErrorContentImage))

            errorTitle.text = SpannableStringBuilder.valueOf(typedArr
                    .getString(R.styleable.AlertHandlerCieloView_cieloErrorTitle))

            errorMessage.text = SpannableStringBuilder.valueOf(typedArr
                    .getString(R.styleable.AlertHandlerCieloView_cieloErrorMessage))

            showTitle = typedArr.getBoolean(R.styleable.AlertHandlerCieloView_cieloShowTitle, true)

            typedArr.recycle()
        }

    }

    fun configureActionClickListener(clickListener: OnClickListener) {
        errorButton?.setOnClickListener(clickListener)
    }

    fun hasClickListener() = isActionOnClicked

    fun configureErrorHandlerByErrorMessage(errorMessage: ErrorMessage, actionOnClick: () -> Unit) {

        when (errorMessage.errorCode) {
            HttpURLConnection.HTTP_FORBIDDEN.toString() -> {
                cieloErrorMessage = resources.getString(R.string.error_maintenance_content)
                cieloErrorTitle = resources.getString(R.string.error_maintenance_title)
                errorHandlerCieloViewImageDrawable = R.drawable.img_maintenance_mfa
                errorButton?.setText(resources.getString(R.string.ok))
                configureActionClickListener(OnClickListener {
                    isActionOnClicked = true
                    actionOnClick()
                })
            }
            else -> {
                setMessageError(errorMessage.errorCode, errorMessage.errorMessage)
            }
        }

    }


    fun setMessageError(code: String, errorMessage: String) {
        val codesErrorFirst = resources.getStringArray(R.array.codes_error_first)
        val codesErrorSecond = resources.getStringArray(R.array.codes_error_second)

        post {
            when (code) {
                CREDIT_NOT_ALLOWED -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_do_not_contract_possible)
                    cieloErrorMessage = errorMessage
                }
                CONTRACT_NOT_EFFECTED -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_do_not_contract_possible)
                    cieloErrorMessage = errorMessage
                    errorButton?.setText(resources.getString(R.string.rm_cc_credit_contract_button))
                }
                CONTRACT_IN_PROGRESS -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_contract_in_progress)
                    cieloErrorMessage = resources.getString(R.string.text_message_contract_in_progress)
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_02
                }
                EMPTY -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_generic_error)
                    cieloErrorMessage = resources.getString(R.string.text_message_generic_error)
                    errorButton?.setText(resources.getString(R.string.text_button_try_again))
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                }
                ERROR_NOT_FOUND -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_generic_error)
                    cieloErrorMessage = resources.getString(R.string.business_error)
                    errorButton?.setText(resources.getString(R.string.ok))
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                }
                Text.INVALID_OTP_CODE -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_generic_error_token)
                    cieloErrorMessage = resources.getString(R.string.text_subtitle_generic_error_token)
                    errorButton?.setText(resources.getString(R.string.text_button_try_again))
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_token_invalido
                }
                Text.OTP_NOT_REGISTERED, Text.OTP_TEMPORARILY_BLOCKED, Text.OTP_REQUIRED -> {
                    cieloErrorTitle = resources.getString(R.string.text_title_generic_error_token)
                    cieloErrorMessage = resources.getString(R.string.text_subtitle_generic_error_token)
                    errorButton?.setText(resources.getString(R.string.text_button_try_again))
                    errorHandlerCieloViewImageDrawable = R.drawable.ic_token_invalido
                }
                in codesErrorFirst -> cieloErrorMessage = resources.getString(R.string.text_message_generic_error)
                in codesErrorSecond -> {
                    cieloErrorMessage = resources.getString(R.string.text_message_call_cancellation_center_error)
                    tvPhone.visible()
                }
                else -> cieloErrorMessage = errorMessage
            }
        }
    }

    fun configureButtonLabel(buttonLabel: String) {
        errorButton?.setText(buttonLabel)
    }

    fun configureButtonVisible(isVisible: Boolean) = if (isVisible) errorButton?.visible() else errorButton?.gone()

}