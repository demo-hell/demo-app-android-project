package br.com.mobicare.cielo.pix.api.keys

import br.com.mobicare.cielo.pix.domain.*
import io.reactivex.Observable

interface PixKeysRepositoryContract {
    fun getKeys(): Observable<PixKeysResponse>
    fun createKey(otpCode: String?, body: CreateKeyRequest): Observable<CreateKeyResponse>
    fun validateKey(key: String?, type: String?): Observable<ValidateKeyResponse>
    fun deleteKey(otpCode: String?, body: PixKeyDeleteRequest): Observable<PixKeyDeleteRequest>
    fun requestValidateCode(body: ValidateCode): Observable<ValidateCode>
}