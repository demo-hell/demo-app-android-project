package br.com.mobicare.cielo.commons.listener

interface LogoutListener {
    fun onLogout() {}
}

class DefaultLogoutListener(val block: () -> Unit) : LogoutListener {

    override fun onLogout() {
        block()
    }

}