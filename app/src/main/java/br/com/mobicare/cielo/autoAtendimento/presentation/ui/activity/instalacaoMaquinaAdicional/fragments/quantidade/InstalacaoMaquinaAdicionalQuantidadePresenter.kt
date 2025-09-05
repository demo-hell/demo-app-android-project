package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade

import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse

class InstalacaoMaquinaAdicionalQuantidadePresenter(private val view: InstalacaoMaquinaAdicionalQuantidadeContract.View)
    : InstalacaoMaquinaAdicionalQuantidadeContract.Presenter{

    private var data: MachineItemOfferResponse? = null
    private var amount: Int = 0

    override fun setData(data: MachineItemOfferResponse) {
        this.data = data
        this.view.loadImage(data.imageUrl)
        this.view.setEnableMinusButton(false)
        this.view.setAmount(0)
        this.view.setTitle(data.title)
        this.view.setRentalAmount(data.rentalAmount)
        this.view.setNotification(data.notification)
        this.view.isEnabledNextButton(false)
    }

    override fun minusButtonClicked() {
        if (this.amount > 0) {
            this.amount -= 1
            this.view.setEnablePlusButton(true)
        }

        if (this.amount == 0) {
            this.view.setEnableMinusButton(false)
            this.view.isEnabledNextButton(false)
        }

        this.view.setAmount(this.amount)
    }

    override fun plusButtonClicked() {
        this.data?.let {
            if (this.amount+1 <= it.allowedQuantity) {
                this.amount += 1
            }
            if (this.amount >= it.allowedQuantity) {
                this.view.setEnablePlusButton(false)
            }
            this.view.isEnabledNextButton(true)
            this.view.setEnableMinusButton(true)
            this.view.setAmount(this.amount)
        }
    }

    override fun onNextButtonClicked() {
        if (this.amount > 0) {
            this.view.goToNextScreen(data?.title, data?.rentalAmount, this.amount)
        }
    }

}