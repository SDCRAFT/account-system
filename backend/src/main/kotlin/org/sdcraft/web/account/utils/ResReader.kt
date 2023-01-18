package org.sdcraft.web.account.utils

import java.io.InputStream

object ResReader{
    fun getResourceAsInputStream(path: String): InputStream? {
        return object {}::class.java.getResourceAsStream(path)
    }
}