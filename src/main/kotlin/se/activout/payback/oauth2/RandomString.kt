package se.activout.payback.oauth2

import java.util.concurrent.ThreadLocalRandom

private val symbols: CharArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()

fun randomString(length: Int): String {
    val random = ThreadLocalRandom.current()
    val buf = CharArray(length)
    for (idx in 0 until buf.size)
        buf[idx] = symbols[random.nextInt(symbols.size)]
    return String(buf)
}
