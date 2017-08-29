package com.wattpad.tap.util.rx

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Utilities for working with [Disposable]s
 *
 * @author rashad
 */

fun Disposable.autoDispose(disposeSignal: Observable<Unit>) {
    disposeSignal.take(1).subscribe { dispose() }
}
