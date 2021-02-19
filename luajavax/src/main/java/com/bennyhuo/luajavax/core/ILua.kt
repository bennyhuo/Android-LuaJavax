package com.bennyhuo.luajavax.core

import java.io.Closeable
import java.io.File
import java.io.InputStream

interface ILua : Closeable {
    fun redirectStdoutToFile(outputFile: File): Boolean
    fun redirectStdioToLogcat(): Boolean
    operator fun set(name: String, value: Any?): Boolean
    fun runText(luaScriptText: String): Boolean
    fun runFile(luaScriptFile: File): Boolean
    fun runStream(luaScriptStream: InputStream): Boolean
    fun runScriptInAssets(scriptPath: String): Boolean
    override fun close()
    fun finalize()
}