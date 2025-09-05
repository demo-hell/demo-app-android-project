package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.linkpgtogerado

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R

object SpanHelper {

    @SuppressLint("NewApi")
    fun setSpanOnText(view: AppCompatTextView, context: Context) {

        when (view.id) {
            R.id.textViewPayAtention -> {
                val spannableString = SpannableString(
                        context.getString(R.string.label_pay_atention))
                spannableString
                        .setSpan(StyleSpan(Typeface.BOLD), 34, 48, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                view.text = spannableString
            }
            R.id.textViewNotificationescription -> {
                val spannableStringNotificationDesc = SpannableString(
                        context.getString(R.string.text_view_number_one_notification_description))

                spannableStringNotificationDesc.setSpan(
                        ForegroundColorSpan(
                                getColor(context, R.color.colorAccent)), 81, 102, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                spannableStringNotificationDesc.setSpan(
                        ForegroundColorSpan(
                                getColor(context, R.color.colorAccent)), 106, 120, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                view.text = spannableStringNotificationDesc
            }
            R.id.textViewCallExplanation1 -> {
                val spannableStringExplanation1 = SpannableString(
                        context.getString(R.string.text_view_number_two_call_motoboy_explanation1))
                spannableStringExplanation1.setSpan(
                        ForegroundColorSpan(
                                getColor(context, R.color.colorAccent)), 75, 89, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                view.text = spannableStringExplanation1
            }
            R.id.textViewCallExplanation4 -> {
                val spannableStringExplanation4 = SpannableString(
                        context.getString(R.string.text_view_number_two_call_motoboy_explanation4))
                spannableStringExplanation4.setSpan(
                        ForegroundColorSpan(
                                getColor(context, R.color.colorAccent)), 35, 70, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                view.text = spannableStringExplanation4
            }
            R.id.infoText -> {
                val spannableStringExplanation5 = SpannableString(
                        context.getString(R.string.text_view_number_two_call_motoboy_explanation5))
                spannableStringExplanation5.setSpan(
                        StyleSpan(Typeface.BOLD), 0, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                spannableStringExplanation5.setSpan(
                        URLSpan("https://developercielo.github.io/manual/prevencao-fraudes"),
                        213, 231, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                spannableStringExplanation5.setSpan(
                        StyleSpan(Typeface.BOLD), 213, 231, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                spannableStringExplanation5.setSpan(
                        ForegroundColorSpan(
                                getColor(context, R.color.colorPrimary)), 213, 231, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                view.text = spannableStringExplanation5
                view.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    fun getColor(context: Context, @ColorRes idColorRes: Int) : Int {
        return ContextCompat.getColor(context, idColorRes)
    }
}