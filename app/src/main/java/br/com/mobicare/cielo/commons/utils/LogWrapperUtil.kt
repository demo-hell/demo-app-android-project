package br.com.mobicare.cielo.commons.utils

import timber.log.Timber

object LogWrapperUtil : OnLogWrapper {
    override fun debug(value: String) {
        Timber.tag(OnLogWrapper.APPNAME).d(value)
    }

    override fun info(value: String) {
        Timber.tag(OnLogWrapper.APPNAME).i(value)
    }

    override fun warn(value: String) {
        Timber.tag(OnLogWrapper.APPNAME).w(value)
    }

    override fun verbose(value: String) {
        Timber.tag(OnLogWrapper.APPNAME).v(value)
    }

    override fun error(value: String) {
        Timber.tag(OnLogWrapper.APPNAME).e(value)
    }
}