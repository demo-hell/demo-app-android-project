package br.com.mobicare.cielo.commons.helpers

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.MainConstants
import br.com.stoneage.identify.enums.StageEnum
import br.com.stoneage.identify.sdk.STAColor
import br.com.stoneage.identify.sdk.STATheme

fun getStoneAgeEnvironment(): StageEnum {
    return if (BuildConfig.FLAVOR == MainConstants.FLAVOR_STORE && BuildConfig.BUILD_TYPE == MainConstants.BUILD_TYPE_RELEASE)
        StageEnum.PROD
    else
        StageEnum.HMG
}
fun getStoneAgeTheme(context: Context): STATheme {
    return STATheme(
        primaryColor = STAColor(context.getColor(R.color.brand_400)),
        successColor = STAColor(context.getColor(R.color.success_500)),
        neutralColor = STAColor(context.getColor(R.color.cloud_400))
    )
}