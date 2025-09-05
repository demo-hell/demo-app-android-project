package br.com.mobicare.cielo.arv.presentation.home.utils

interface ArvNavigation {
    fun showArvWhatsAppButton(show: Boolean = false)

    fun setHighlightOnArvWhatsAppButton()

    fun setArvNavigationListener(listener: Listener)

    interface Listener {
        fun onArvWhatsAppButtonClicked()
    }
}
