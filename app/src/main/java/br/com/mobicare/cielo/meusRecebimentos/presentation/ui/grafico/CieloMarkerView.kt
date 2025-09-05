package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.grafico

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.utils.MPPointF

/**
 * Created by benhur.souza on 23/06/2017.
 */
class CieloMarkerView(context: Context, layoutResource: Int): MarkerView(context, layoutResource){

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), -(height / 1.4f))
    }

}