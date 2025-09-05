package br.com.mobicare.cielo.commons.presentation.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.extensions.gone
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object ImageUtils {
    fun loadImage(view: ImageView?, url: String?, dismissOnError: Boolean = false) {
        if (url == null || url.isEmpty() || view == null) {
            return
        }

        Picasso.get().load(url).into(view, object : Callback {
            override fun onSuccess() {

            }

            override fun onError(e: Exception?) {
                if (dismissOnError)
                    view.gone()
            }
        })
    }

    fun loadImage(view: ImageView?, url: String?, @DrawableRes placeHolderId: Int) {
        if (url == null || url.isEmpty() || view == null) {
            view?.setImageResource(placeHolderId)
            return
        }
        Picasso.get().load(url).placeholder(placeHolderId).into(view)
    }

    fun loadSvg(
        targetImageView: ImageView,
        uriImage: Uri,
        imageLoading: Int,
        imageError: Int,
        context: Context
    ) {
        GlideToVectorYou.init().apply {
            with(context)
            withListener(object : GlideToVectorYouListener {
                override fun onLoadFailed() = targetImageView.setImageResource(imageError)
                override fun onResourceReady() {}
            })
            setPlaceHolder(imageLoading, imageError)
            load(uriImage, targetImageView)
        }
    }
}