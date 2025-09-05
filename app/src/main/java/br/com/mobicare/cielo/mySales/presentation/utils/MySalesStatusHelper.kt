package br.com.mobicare.cielo.mySales.presentation.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef

class MySalesStatusHelper {
    companion object {
        fun setSalesStatus(context: Context, statusCode: Int?, circle: ImageView?, statusText: TextView) {
            statusCode?.let {
                when(statusCode) {
                    ExtratoStatusDef.APROVADA -> setAproveStatus(context, circle, statusText)
                    ExtratoStatusDef.NEGADA -> setDeniedStatus(context, circle, statusText)
                    ExtratoStatusDef.ATUALIZAR -> setUpdateStatus(context, circle, statusText)
                    ExtratoStatusDef.CANCELADA -> setDeniedStatus(context, circle, statusText)
                    else -> setCanceledStatus(context, circle, statusText)
                }
            }?: setNullableStatus(context, circle, statusText)
        }

        fun setNullableStatus(context: Context, circle: ImageView?, statusText: TextView) {
            circle?.setBackgroundResource(R.drawable.circle_gray)
            statusText.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
            statusText.text = ""
        }

        fun setAproveStatus(context: Context, circle: ImageView?, statusText: TextView) {
            circle?.setBackgroundResource(R.drawable.circle_green)
            statusText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        fun setUpdateStatus(context: Context, circle: ImageView?, statusText: TextView) {
            circle?.setBackgroundResource(R.drawable.circle_purple)
            statusText.setTextColor(ContextCompat.getColor(context, R.color.purple))
        }

        fun setDeniedStatus(context: Context, circle: ImageView?, statusText: TextView) {
            circle?.setBackgroundResource(R.drawable.circle_red)
            statusText.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        fun setCanceledStatus(context: Context, circle: ImageView?, statusText: TextView) {
            circle?.setBackgroundResource(R.drawable.circle_gray)
            statusText.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
        }
    }
}