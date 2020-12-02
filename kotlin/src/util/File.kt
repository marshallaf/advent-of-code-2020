package util

import java.io.File

fun <R> File.readLines(transform: (String) -> R): Sequence<R> {
    return bufferedReader().lineSequence().map(transform)
}
