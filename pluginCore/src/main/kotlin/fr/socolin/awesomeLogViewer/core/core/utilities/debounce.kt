package fr.socolin.awesomeLogViewer.core.core.utilities

import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.IPropertyView
import com.jetbrains.rd.util.reactive.Signal
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicReference

/**
 * Extension function to debounce a Signal
 * @param debounceTime The duration to debounce the signal
 * @param lifetime The parent lifetime
 * @param scope The coroutine scope to use for the debounce
 * @param onEmit The callback to invoke after debounce time has passed
 */
fun <T> Signal<T>.debounce(
    debounceTimeMs: Long,
    lifetime: Lifetime,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    onEmit: (T) -> Unit
) {
    val job = AtomicReference<Job?>(null)
    this.advise(lifetime) { value ->
        job.getAndSet(
            scope.launch {
                delay(debounceTimeMs)
                onEmit(value)
            }
        )?.cancel()
    }
}

fun <T> IPropertyView<T>.debounce(
    debounceTimeMs: Long,
    lifetime: Lifetime,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    onEmit: (T) -> Unit
) {
    val job = AtomicReference<Job?>(null)
    this.advise(lifetime) { value ->
        job.getAndSet(
            scope.launch {
                delay(debounceTimeMs)
                onEmit(value)
            }
        )?.cancel()
    }
}

