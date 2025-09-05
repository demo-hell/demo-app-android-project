package br.com.mobicare.cielo.commons.utils


interface OnLogWrapper{

    fun debug(value:String)
    fun info(value:String)
    fun warn(value:String)
    fun verbose(value:String)
    fun error(value:String)

    companion object {
        const val APPNAME = "CIELON"
    }


}