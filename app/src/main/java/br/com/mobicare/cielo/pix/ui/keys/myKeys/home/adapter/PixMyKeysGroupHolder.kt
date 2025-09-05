package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.adapter

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum.*
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.PixMyKeysContract
import kotlinx.android.synthetic.main.layout_pix_my_keys_item.view.*

class PixMyKeysGroupHolder(private val view: View, private val mContext: Context) :
    RecyclerView.ViewHolder(view) {

    fun bind(myKey: MyKey, listener: PixMyKeysContract.View, isVerificationKey: Boolean) {
        view.tv_title_my_keys_item?.text = myKey.key?.let { getFormattedKey(it, myKey.keyType, isMask = false) }
        view.keyContainer?.setOnClickListener {
            if (isVerificationKey)
                listener.onShowClaimKeysDetails(myKey)
            else
                if(myKey.claimType == PixClaimTypeEnum.PORTABILITY.name || myKey.claimType == PixClaimTypeEnum.OWNERSHIP.name)
                    listener.onShowReceiveClaimKeysDetails(myKey)
                else
                    listener.onShowKeyDetails(myKey)
        }

        if (myKey.main == true)
            setupMainKey()
        else {
            if (isVerificationKey)
                setupVerificationKeyStatus(myKey.claimType)
            else
                setupKeyStatus(myKey.claimType)
        }

        setupKeyType(myKey.keyType)
    }

    private fun setupMainKey() {
        setupStyle(
            statusText = R.string.text_pix_my_keys_main_key,
            background = R.drawable.background_status_my_keys_pix_success_100,
            textColor = R.color.success_500,
            icon = R.drawable.ic_money_sign
        )

        view.tv_status_my_keys_item2?.visible()
    }

    private fun setupKeyType(type: String?) {
        when (type) {
            CNPJ.name, CPF.name -> {
                setCardIcon(R.drawable.ic_document_key_pix)
            }
            PHONE.name -> {
                setCardIcon(R.drawable.ic_phone_key_pix)
            }
            EMAIL.name -> {
                setCardIcon(R.drawable.ic_email_key_pix)
            }
            EVP.name -> {
                setCardIcon(R.drawable.ic_random_key_pix)
            }
            else -> setCardIcon(R.drawable.ic_random_key_pix)
        }
    }

    private fun setupKeyStatus(status: String?) {
        when (status) {
            PixClaimTypeEnum.PORTABILITY.name -> {
                setupStyle(
                    statusText = R.string.text_pix_my_keys_portability_request,
                    background = R.drawable.background_status_my_keys_pix_alert_100,
                    textColor = R.color.alert_600,
                    icon = R.drawable.ic_alert_my_keys_item
                )
            }
            PixClaimTypeEnum.OWNERSHIP.name -> {
                setupStyle(
                    statusText = R.string.text_pix_my_keys_claim_request,
                    background = R.drawable.background_status_my_keys_pix_alert_100,
                    textColor = R.color.alert_600,
                    icon = R.drawable.ic_alert_my_keys_item
                )
            }
            else -> activeKey()
        }
    }

    private fun setupVerificationKeyStatus(status: String?) {
        when (status) {
            PixClaimTypeEnum.PORTABILITY.name -> {
                setupStyle(
                    statusText = R.string.text_pix_my_keys_portability_request_send,
                    background = R.drawable.background_status_my_keys_pix_storm_100,
                    textColor = R.color.storm_500
                )
            }
            PixClaimTypeEnum.OWNERSHIP.name -> {
                setupStyle(
                    statusText = R.string.text_pix_my_keys_claim_request_sent,
                    background = R.drawable.background_status_my_keys_pix_storm_100,
                    textColor = R.color.storm_500
                )
            }
            else -> activeKey()
        }
    }

    private fun activeKey() {
        setupStyle(
            statusText = R.string.text_pix_my_keys_active_key,
            background = R.drawable.background_status_my_keys_pix_success_100,
            textColor = R.color.success_500
        )
    }

    private fun setupStyle(
        @StringRes statusText: Int,
        @DrawableRes background: Int,
        @ColorRes textColor: Int,
        @DrawableRes icon: Int? = null
    ) {
        view.tv_status_my_keys_item?.apply {
            text = mContext.getString(statusText)
            setBackgroundResource(background)
            setTextColor(ContextCompat.getColor(mContext, textColor))

            icon?.let {
                setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
            }
        }
    }

    private fun setCardIcon(@DrawableRes image: Int) {
        view.iv_my_key_icon?.apply {
            setImageResource(image)
            setColorFilter(ContextCompat.getColor(context, R.color.display_300))
        }
    }
}