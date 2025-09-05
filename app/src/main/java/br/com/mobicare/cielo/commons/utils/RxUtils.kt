package br.com.mobicare.cielo.commons.utils

import android.text.Editable
import android.text.TextUtils
import android.widget.EditText
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val TIMEOUT: Long = 100

fun TypefaceEditTextView.afterTextChangesNotEmptySubscribe(block: (editable: Editable) -> Unit):
        Disposable {

    return RxTextView.afterTextChangeEvents(this)
        .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it).not() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })

}

fun EditText.afterTextChangesNotEmptySubscribe(
    timeout: Long = 500,
    block: (editable: Editable) -> Unit
):
        Disposable {

    return RxTextView.afterTextChangeEvents(this)
        .debounce(timeout, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it).not() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })
}


fun TypefaceEditTextView.afterTextChangesEmptySubscribe(block: (editable: Editable) -> Unit):
        Disposable {

    return RxTextView.afterTextChangeEvents(this)
        .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })
}

fun EditText.afterTextChangesEmptySubscribe(block: (editable: Editable) -> Unit):
        Disposable {

    return RxTextView.afterTextChangeEvents(this)
        .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })
}

fun CieloInputText.afterTextChangesNotEmptySubscribe(block: (editable: Editable) -> Unit):
        Disposable {

    return RxTextView.afterTextChangeEvents(this.findViewById(R.id.edit_text))
        .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it).not() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })

}

fun CieloInputText.afterTextChangesEmptySubscribe(block: (editable: Editable) -> Unit):
        Disposable {

    return RxTextView.afterTextChangeEvents(this.findViewById(R.id.edit_text))
        .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
        .map { it.editable() }
        .filter { TextUtils.isEmpty(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            it?.let { editable -> block(editable) }
        }, {
        })

}

interface DisposableHandler {
    fun start()
    fun destroy()
}

open class CompositeDisposableHandler : DisposableHandler {

    var compositeDisposable = CompositeDisposable()

    override fun start() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun destroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
