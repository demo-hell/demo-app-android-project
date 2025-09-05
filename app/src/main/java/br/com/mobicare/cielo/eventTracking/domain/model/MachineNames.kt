package br.com.mobicare.cielo.eventTracking.domain.model

import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.pix.constants.EMPTY

enum class MachineNames(val apiName: String = EMPTY.ifNullSimpleLine(), @DrawableRes val drawableRes: Int = R.drawable.machine_default) {
    ICT_250("ICT - 250", R.drawable.ict_250),
    FLASH("Flash", R.drawable.flash_s920),
    ZIP_2500("ZIP - 2500", R.drawable.zip_link2500),
    ZIP_D175("ZIP - D175", R.drawable.zip_d175),
    ZIP_D195("ZIP - D195", R.drawable.zip_d195),
    ZIP_D200("ZIP - D200", R.drawable.zip_d200),
    FLASH_MOVE5000("Flash - Move 5000", R.drawable.flash_move5000),
    IWL_251("IWL - 251"),
    VX_680("VX 680", R.drawable.vx680),
    VX_685("VX - 685"),
    LIO_V2("LIO V2", R.drawable.lio_v2),
    LIO_V1("LIO V1"),
    LIO_VENDA_LIVRE("LIO +  MOBILE VENDA CIELO LIVRE"),
    LIO_VENDA_CONTROLE("LIO + MOBILE VENDA CIELO CONTROLE"),
    LIO_V3("LIO ON (V3)", R.drawable.lio_on),
    SMARTDOCK("SMARTDOCK - LIO POS"),
    SP930("SP930", R.drawable.flash_sp930),
    VX670("VX670 GPRS"),
    VX510("VX510"),
    ECOMMERCE("E-COMMERCE")
}