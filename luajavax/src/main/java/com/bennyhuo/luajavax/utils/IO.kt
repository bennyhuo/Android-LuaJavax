package com.bennyhuo.luajavax.utils

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyToAndClose(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    return copyTo(out, bufferSize).also {
        this.close()
        out.close()
    }
}
