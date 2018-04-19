package se.activout.payback

import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Form
import javax.ws.rs.core.Response

inline fun <reified T> Response.readEntity(): T = this.readEntity(T::class.java)

fun formOf(vararg pairs: Pair<String, String>) = Form().apply {
    for ((key, value) in pairs) {
        param(key, value)
    }
}

operator fun WebTarget.div(path: String): WebTarget = path(path)
