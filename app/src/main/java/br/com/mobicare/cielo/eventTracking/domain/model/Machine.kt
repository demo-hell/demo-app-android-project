package br.com.mobicare.cielo.eventTracking.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Machine(
    val id: String = SIMPLE_LINE,
    val name: String = SIMPLE_LINE,
    val logicalID: String = SIMPLE_LINE,
    val modality: String = SIMPLE_LINE,
) : Parcelable {
    @DrawableRes
    val photo: Int = when (name) {
        MachineNames.ICT_250.apiName -> MachineNames.ICT_250.drawableRes
        MachineNames.FLASH.apiName -> MachineNames.FLASH.drawableRes
        MachineNames.ZIP_2500.apiName -> MachineNames.ZIP_2500.drawableRes
        MachineNames.ZIP_D175.apiName -> MachineNames.ZIP_D175.drawableRes
        MachineNames.ZIP_D195.apiName -> MachineNames.ZIP_D195.drawableRes
        MachineNames.ZIP_D200.apiName -> MachineNames.ZIP_D200.drawableRes
        MachineNames.FLASH_MOVE5000.apiName -> MachineNames.FLASH_MOVE5000.drawableRes
        MachineNames.IWL_251.apiName -> MachineNames.IWL_251.drawableRes
        MachineNames.VX_680.apiName -> MachineNames.VX_680.drawableRes
        MachineNames.VX_685.apiName -> MachineNames.VX_685.drawableRes
        MachineNames.LIO_V2.apiName -> MachineNames.LIO_V2.drawableRes
        MachineNames.LIO_V1.apiName -> MachineNames.LIO_V1.drawableRes
        MachineNames.LIO_VENDA_LIVRE.apiName -> MachineNames.LIO_VENDA_LIVRE.drawableRes
        MachineNames.LIO_VENDA_CONTROLE.apiName -> MachineNames.LIO_VENDA_CONTROLE.drawableRes
        MachineNames.LIO_V3.apiName -> MachineNames.LIO_V3.drawableRes
        MachineNames.SMARTDOCK.apiName -> MachineNames.SMARTDOCK.drawableRes
        MachineNames.SP930.apiName -> MachineNames.SP930.drawableRes
        MachineNames.VX670.apiName -> MachineNames.VX670.drawableRes
        MachineNames.VX510.apiName -> MachineNames.VX510.drawableRes
        MachineNames.ECOMMERCE.apiName -> MachineNames.ECOMMERCE.drawableRes
        else -> R.drawable.machine_default
    }
}
