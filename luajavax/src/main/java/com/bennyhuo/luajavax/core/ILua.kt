package com.bennyhuo.luajavax.core

import java.io.Closeable
import java.io.File
import java.io.InputStream

interface ILua : Closeable {

    val isClosed: Boolean

    fun redirectStdoutToFile(outputFile: File): Boolean
    fun redirectStdioToLogcat(): Boolean

    operator fun set(name: String, value: Any?): Boolean
    operator fun <T> get(name: String): T?
    fun getInt(name: String): Int?
    fun getDouble(name: String): Double?
    fun getString(name: String): String?
    fun getBoolean(name: String): Boolean?

    fun runText(luaScriptText: String): Boolean
    fun runFile(luaScriptFile: File): Boolean
    fun runStream(luaScriptStream: InputStream): Boolean
    fun runScriptInAssets(scriptPath: String): Boolean

    override fun close()
    fun finalize()
}
