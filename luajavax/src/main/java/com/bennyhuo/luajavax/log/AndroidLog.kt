package com.bennyhuo.luajavax.log

import android.util.Log

object AndroidLog {
    private val androidLogConstructor by lazy {
        try {
            Class.forName("com.android.internal.os.AndroidPrintStream")
                    .getConstructor(Int::class.javaPrimitiveType, String::class.java)
                    ?.also {
                        it.isAccessible = true
                    }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val stdout by lazy {
        try {
            androidLogConstructor?.newInstance(Log.INFO, "luajavax")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val stderr by lazy {
        try {
            androidLogConstructor?.newInstance(Log.WARN, "luajavax")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}