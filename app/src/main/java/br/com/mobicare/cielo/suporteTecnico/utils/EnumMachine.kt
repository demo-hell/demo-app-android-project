package br.com.mobicare.cielo.suporteTecnico.utils

import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.R

enum class EnumMachine(val model: String, @DrawableRes val image: Int) {

    ZIP_150("ZIP - D150", R.drawable.zip_d150),
    ZIP_D175("ZIP - D175", R.drawable.zip_d175),
    ZIP_D195("ZIP - D195", R.drawable.zip_d195),
    ZIP_D200("ZIP - D200", R.drawable.zip_d200),
    ZIP_2500("ZIP - 2500", R.drawable.zip_link2500),
    FLASH("Flash", R.drawable.flash_s920),
    LIO("LIO ON", R.drawable.lio_on),
    LIO_V2("LIO V2", R.drawable.lio_v2),
    FLASH_MOVE_5000("Flash - MOVE 5000", R.drawable.flash_move5000),
    ICT_250("ICT 250", R.drawable.ict_250),
    VX_680("VX 680", R.drawable.vx680),
    VX_580("VX 510 6MB PCI", R.drawable.machine_default);

    companion object {
        fun findImageByItemName(name: String?): Int {
            if (name != null) {
                for (enumValue in EnumMachine.values()) {
                    if (enumValue.model == name) {
                        return enumValue.image
                    }
                }
            }
            return R.drawable.machine_default
        }
    }
}