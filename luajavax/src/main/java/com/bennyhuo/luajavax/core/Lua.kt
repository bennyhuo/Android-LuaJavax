package com.bennyhuo.luajavax.core

import android.content.Context
import org.keplerproject.luajava.LuaException
import org.keplerproject.luajava.LuaState
import org.keplerproject.luajava.LuaStateFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import java.util.*

internal class Lua(context: Context) : ILua {

    private val logger = LoggerFactory.getLogger(Lua::class.java)

    private var luaState: LuaState? = null
    private var outputFile: File? = null

    init {
        var state: LuaState? = null
        try {
            state = LuaStateFactory.newLuaState()
            state!!.openLibs()
        } catch (e: Exception) {
            logger.warn("Init Lua with Error", e)
        }

        luaState = state
        if (luaState != null) {
            this["applicationContext"] = context.applicationContext
        }
    }

    private inline fun tryWithState(block: LuaState.() -> Boolean) = try {
        if (!luaState!!.run(block)) {
            throw LuaException(luaState?.toString(-1))
        }
        true
    } catch (e: Exception) {
        logger.warn("lua error", e)
        outputFile?.writer()?.let(::PrintWriter)?.use {
            it.write("[${Date()}] Error when run lua.\n")
            e.printStackTrace(it)
        }
        false
    }.also {
        logger.debug("run lua, result: $it")
    }

    override fun setOutput(outputFile: File) = tryWithState {
        set("path", outputFile.path) and runText("newOutput(path)")
    }.also {
        this@Lua.outputFile = outputFile
    }

    /**
     * 添加 Lua 全局变量
     */
    override operator fun set(name: String, value: Any) = tryWithState {
        logger.debug("setGlobal: $name=$value")
        pushObjectValue(value)
        setGlobal(name)
        true
    }

    override fun runText(luaScriptText: String) = tryWithState {
        logger.debug(luaScriptText)
        LdoString(luaScriptText) == 0
    }

    override fun runFile(luaScriptFile: File) = tryWithState {
        logger.debug("file: " + luaScriptFile.absolutePath)
        LdoFile(luaScriptFile.absolutePath) == 0
    }

    override fun runStream(luaScriptStream: InputStream) = tryWithState {
        val luaScript = luaScriptStream.reader().readText()
        logger.debug("run script: \n $luaScript")
        LdoString(luaScript) == 0
    }

    override fun runScriptInAssets(scriptPath: String) =
            runStream(Lua::class.java.classLoader.getResourceAsStream("assets/$scriptPath"))

    override fun close() {
        try {
            if (luaState?.isClosed == false) {
                luaState?.close()
            }
        } catch (e: Exception) {
            logger.warn("Destroy Lua with Error", e)
        }
    }

    override fun finalize() {
        close()
    }
}
