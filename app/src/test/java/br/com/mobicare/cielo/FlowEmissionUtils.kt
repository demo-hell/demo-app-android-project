package br.com.mobicare.cielo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class FlowEmissionHandler<T>(
    private val flow: Flow<T>,
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) {
    private var job: Job? = null

    fun captureEmissions(scope: CoroutineScope): List<T> {
        val emissions = mutableListOf<T>()
        job = scope.launch(dispatcher) { flow.toList(emissions) }
        return emissions
    }

    fun cancelJob() = job?.cancel()
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> runTestWithFlowEmission(
    flowEmissionHandler: FlowEmissionHandler<T>,
    block: (List<T>) -> Unit
) = runTest {
    val emissions = flowEmissionHandler.captureEmissions(this)
    block(emissions)
    flowEmissionHandler.cancelJob()
}