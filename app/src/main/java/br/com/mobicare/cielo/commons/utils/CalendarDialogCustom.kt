package br.com.mobicare.cielo.commons.utils

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import br.com.mobicare.cielo.R
import java.util.*

class CalendarDialogCustom {

    private var context: Context? = null
    private var dialog: DatePickerDialog? = null

    constructor(minDay: Int?, maxDay: Int?, selectedDay: Int, day: Int, month: Int, year: Int, label: String, context: Context, onDateSetListener: OnDateSetListener, style: Int = R.style.DialogTheme, endCalendar: Calendar? = null, startCalendar: Calendar? = null) {
        this.context = context

        var setMinDay = false
        var setMaxDay = false

        val cSelectDay = Calendar.getInstance(TimeZone.getDefault())
        var cMinDay = Calendar.getInstance(TimeZone.getDefault())
        var cMaxDay = Calendar.getInstance(TimeZone.getDefault())

        minDay?.let {
            setMinDay = true
            cMinDay.add(Calendar.DAY_OF_MONTH, it)
        } ?: run {
            startCalendar?.let {
                setMinDay = true
                cMinDay = startCalendar
            }
        }

        maxDay?.let {
            setMaxDay = true
            cMaxDay.add(Calendar.DAY_OF_MONTH, it)
        } ?: run {
            endCalendar?.let {
                setMaxDay = true
                cMaxDay = endCalendar
            }
        }

        if (day > -1 && month > -1 && year > -1)
            cSelectDay.set(year, month, day)
        else
            cSelectDay.add(Calendar.DAY_OF_MONTH, selectedDay)

        dialog = object : DatePickerDialog(context,
                style,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth -> onDateSetListener.onDateSet(view, year, monthOfYear + 1, dayOfMonth) },
                cSelectDay.get(Calendar.YEAR),
                cSelectDay.get(Calendar.MONTH),
                cSelectDay.get(Calendar.DAY_OF_MONTH)) {

        }

        if (setMinDay)
            dialog?.datePicker?.minDate = cMinDay.timeInMillis

        if (setMaxDay)
            dialog?.datePicker?.maxDate = cMaxDay.timeInMillis

        if (label.isNullOrBlank().not())
            dialog?.setTitle(label)

        dialog?.setOnCancelListener {
            dismiss()
        }
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}