package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details


interface PixMyKeysDetails {

    interface View {
        fun copyKey(keyValue: String)
        fun shareKey(keyValue: String)
        fun deleteMainKey(keyValue: String)
        fun deleteNormalKey(keyValue: String)
        fun showWhatIsMainKeyFAQ()
    }
}