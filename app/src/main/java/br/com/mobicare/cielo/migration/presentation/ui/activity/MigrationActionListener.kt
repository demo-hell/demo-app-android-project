package br.com.mobicare.cielo.migration.presentation.ui.activity

interface MigrationActionListener {
    fun onNextStep(isFinish: Boolean){}
    fun setTitle(title: String){}
    fun hideProgress()
    fun showProgress()
    fun bannerDimmiss()
}