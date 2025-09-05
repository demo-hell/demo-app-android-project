package br.com.mobicare.cielo.p2m.utils

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

object P2mFactory {

    val p2mKeyCode = "12345"
    val p2mInvalid = ""

    val acceptResponse: P2mAcceptResponseTest get() = P2mAcceptResponseTest().let {
        it.add(
            P2mAcceptResponseItemTest(
                code = 202,
                message = "accepted",
            )
        )
        it
    }

}

class P2mAcceptResponseTest : ArrayList<P2mAcceptResponseItemTest>()

@Keep
@Parcelize
data class P2mAcceptResponseItemTest(
    val code: Int? = null,
    val message: String? = null
) : Parcelable