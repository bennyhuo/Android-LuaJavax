package com.bennyhuo.luajavax.sample.cases

import android.content.Context
import com.bennyhuo.luajavax.LuaFactory
import com.bennyhuo.luajavax.sample.BuildConfig
import com.bennyhuo.luajavax.sample.logger


fun Context.basic() {
    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["BuildConfig"] = BuildConfig::class.java // set global value
        // run script, access to Java class
        lua.runText("print(BuildConfig.APPLICATION_ID); print(BuildConfig.VERSION_CODE)")
        lua.runScriptInAssets("lua/setup.lua")
        lua.runText("print(BuildConfig.APPLICATION_ID); print(BuildConfig.VERSION_CODE)")
    }
}

fun Context.testErrorMessageForJavaMethodCall() {
    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["BuildConfig"] = BuildConfig::class.java // set global value
        // 'logger.debug' is an invalid function call in lua.
        // You will get 'org.keplerproject.luajava.LuaException: Not a valid OO function call.'
        // without any useful line info.
        lua.runText("logger.debug(BuildConfig.APPLICATION_ID)")
        // Now, we can have this in the logcat:
        // 'org.keplerproject.luajava.LuaException:
        // [string "logger.debug(BuildConfig.APPLICATION_ID)"]:1: Not a valid OO function call.'

        lua.runScriptInAssets("lua/TestErrorMessageForJavaMethodCall.lua")

    }
}

fun Context.testNameConflictForFieldAndMethod() {
    class NameConflict {
        @JvmField
        var a = "NameConflict.a"

        fun a() {
            logger.debug("NameConflict.a() called.")
        }
    }

    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["nameConflict"] = NameConflict() // set global value
        // 'nameConflict.a' will be treated as a function call before.
        // This is fixed by checking the Lua instruction for OP_GET_TABLE.
        lua.runText("""
            logger:debug(nameConflict.a)
            nameConflict:a()
        """.trimIndent())
    }
}

fun Context.testNestedJavaMethodCall() {
    class NestedMethodCall {

        fun a() = "NestedMethodCall.a()"

        fun b(value: String) {
            logger.debug(value)
        }
    }

    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["nestedMethodCall"] = NestedMethodCall() // set global value
        // Use a table to hold the nested called method names instead of a string to fix this.
        // This would lead to a 'Invalid method call. No such method.' error before.
        lua.runText("""
            nestedMethodCall:b(nestedMethodCall:a())
        """.trimIndent())
    }
}

fun Context.testBindClass() {
    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["StringClass"] = String::class.java
        // Use a table to hold the nested called method names instead of a string to fix this.
        // This would lead to a 'Invalid method call. No such method.' error before.
        lua.runText("""
            logger:debug("{}", StringClass)
            StringClass1 = luajava.bindClass("java.lang.String")
            logger:debug("{}", StringClass1)
            
            logger:debug(StringClass:format("%d.%d.%d.%d",1, 2, 3, 4))
            logger:debug(StringClass1:format("%d.%d.%d.%d",4, 3, 2, 1))
        """.trimIndent())
    }
}

fun Context.testJavaNew() {
    class TestJavaNew {
        fun sayHello() {
            logger.debug("Hello.")
        }
    }

    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["TestJavaNew"] = TestJavaNew::class.java
        // Use a table to hold the nested called method names instead of a string to fix this.
        // This would lead to a 'Invalid method call. No such method.' error before.
        lua.runText("""
            testJavaNew = luajava.new(TestJavaNew)
            testJavaNew:sayHello()
        """.trimIndent())
    }
}

fun Context.testJavaMethodNotFound() {
    class JavaMethodNotFound {
        fun sayHello() {
            logger.debug("Hello.")
        }
    }

    LuaFactory.createLua(this).use { lua ->
        lua["logger"] = logger
        lua["javaMethodNotFound"] = JavaMethodNotFound()
        // Use a table to hold the nested called method names instead of a string to fix this.
        // This would lead to a 'Invalid method call. No such method.' error before.
        lua.runText("""
            javaMethodNotFound:sayHello()
            javaMethodNotFound:sayHell()
        """.trimIndent())
    }
}

fun Context.testLuaStdio() {
    LuaFactory.createLua(this).use { lua ->
        // redirect 'print' and 'print_error' to logcat.
        // It take no effects on 'io.write' or 'io.stdout.write' or 'io.stderr.write'.
        lua.redirectStdioToLogcat()
        lua.runText("""
            print("see this in logcat info")
            print_error("see this in logcat warn")
            io.write("this won't work")
            io.stderr:write("this won't work")
        """.trimIndent())
    }
}