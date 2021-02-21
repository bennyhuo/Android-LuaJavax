package com.bennyhuo.luajavax.core

import android.content.Context
import com.bennyhuo.luajavax.log.AndroidLog
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
            runText("print_error = print")
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

    override fun redirectStdoutToFile(outputFile: File) = tryWithState {
        set("path", outputFile.path) and runText("""
            print = function(...)
                io.write(table.concat({ os.date("[%Y-%m-%d %H:%M:%S]"), ... }, "\t"))
                io.flush()
            end
            print_error = print
            io.output(path)
        """.trimIndent())
    }.also {
        this@Lua.outputFile = outputFile
    }

    override fun redirectStdioToLogcat(): Boolean = tryWithState {
        set("luajava_stdout", AndroidLog.stdout) and
                set("luajava_stderr", AndroidLog.stderr) and
                runText("""
                    print = function(...)
                        luajava_stdout:println(table.concat({ ... }, " "))
                    end
                    
                    print_error = function(...)
                        luajava_stderr:println(table.concat({ ... }, " "))
                    end
                """.trimIndent())
    }

    /**
     * 添加 Lua 全局变量
     */
    override operator fun set(name: String, value: Any?) = tryWithState {
        logger.debug("setGlobal: $name=$value")
        pushObjectValue(value)
        setGlobal(name)
        true
    }

    override fun <T> get(name: String): T? {
        return try {
            luaState?.run {
                getGlobal(name)
                when {
                    isNoneOrNil(-1) -> null
                    isBoolean(-1) -> toBoolean(-1)
                    isNumber(-1) -> toNumber(-1)
                    // Lua treats number as string too. So test number before string.
                    isString(-1) -> toString(-1)
                    isObject(-1) -> toJavaObject(-1)
                    else -> throw LuaException("Unsupported type of key: $name")
                }
            }
        } catch (e: Exception) {
            logger.warn("lua error", e)
            outputFile?.writer()?.let(::PrintWriter)?.use {
                it.write("[${Date()}] Error when run lua.\n")
                e.printStackTrace(it)
            }
            null
        } as T?
    }

    override fun getInt(name: String): Int? {
        return get<Number>(name)?.toInt()
    }

    override fun getDouble(name: String): Double? {
        return get<Double>(name)
    }

    override fun getString(name: String): String? {
        return get<String>(name)
    }

    override fun getBoolean(name: String): Boolean? {
        return get<Boolean>(name)
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
