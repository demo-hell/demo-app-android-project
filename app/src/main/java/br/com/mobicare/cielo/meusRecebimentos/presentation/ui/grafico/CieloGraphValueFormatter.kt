package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.grafico

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.NumberFormat

/**
 * Created by benhur.souza on 23/06/2017.
 */
class CieloGraphValueFormatter: IValueFormatter{

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        if (entry.x == 0f || entry.x == 6f) {
            return ""
        }
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 0

        var label = "R$ " + formatter.format(value).replace(",",".")

        if(entry.x.toInt() == 5){
            label = "$label*"
        }

        return label
    }
}