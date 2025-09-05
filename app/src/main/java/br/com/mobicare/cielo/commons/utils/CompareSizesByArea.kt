package br.com.mobicare.cielo.commons.utils

import android.annotation.TargetApi
import android.os.Build
import android.util.Size
import java.lang.Long.signum


internal class CompareSizesByArea : Comparator<Size> {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun compare(lhs: Size, rhs: Size) =
            signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)

}
