package com.chaos.ekinomy.util.interop

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)