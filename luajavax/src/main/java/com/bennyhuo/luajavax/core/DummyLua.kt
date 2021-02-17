package com.bennyhuo.luajavax.core

import java.io.File
import java.io.InputStream

/**
 * Created by benny on 21/11/2018.
 */
class DummyLua : ILua {

    private var outputFile: File? = null

    private fun printTips(): Boolean {
        outputFile?.writeText("Lua is disabled.\n")
        return false
    }

    override fun setOutput(outputFile: File): Boolean {
        this.outputFile = outputFile
        return true
    }

    override fun set(name: String, value: Any): Boolean = printTips()

    override fun runText(luaScriptText: String): Boolean = printTips()

    override fun runFile(luaScriptFile: File): Boolean = printTips()

    override fun runStream(luaScriptStream: InputStream): Boolean = printTips()

    override fun runScriptInAssets(scriptPath: String): Boolean = printTips()

    override fun close() {
    }

    override fun finalize() {
    }
}