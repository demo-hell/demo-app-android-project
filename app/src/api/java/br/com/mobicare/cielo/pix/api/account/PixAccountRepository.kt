package br.com.mobicare.cielo.pix.api.account

import br.com.mobicare.cielo.pix.domain.PixProfileRequest

class PixAccountRepository(private val dataSource: PixAccountDataSource) :
    PixAccountRepositoryContract {

    override fun getMerchant() = dataSource.getMerchant()

    override fun getProfile() = dataSource.getProfile()

    override fun updateProfile(
        otpCode: String,
        body: PixProfileRequest?
    ) = dataSource.updateProfile(otpCode, body)
}