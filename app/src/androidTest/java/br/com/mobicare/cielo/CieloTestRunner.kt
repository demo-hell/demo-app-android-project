package br.com.mobicare.cielo

import android.os.Bundle
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.linkedin.android.testbutler.TestButler
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidAssetsFileParser
import io.appflate.restmock.android.AndroidLogger


class CieloTestRunner : AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        RESTMockServerStarter.startSync(AndroidAssetsFileParser(context), AndroidLogger())
    }

    override fun onStart() {
        TestButler.setup(targetContext)
        super.onStart()
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        TestButler.teardown(targetContext)
        super.finish(resultCode, results)
    }
}