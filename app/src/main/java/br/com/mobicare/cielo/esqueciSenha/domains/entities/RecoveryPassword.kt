package br.com.mobicare.cielo.esqueciSenha.domains.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by benhur.souza on 11/04/2017.
 */

@Parcelize
data class RecoveryPassword(
        var login: Login? = null,
        var pid: Pid? = null
) : Parcelable
