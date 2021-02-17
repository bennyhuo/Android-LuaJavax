package com.bennyhuo.luajavax.sample

import android.app.Activity
import android.os.Bundle
import com.bennyhuo.luajavax.LuaFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("LuaJava")

class MainActivity : Activity() {

    val sharedPreferences by lazy {
        getSharedPreferences("Android-LuaJava", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runLua.setOnClickListener {
            println("Run lua...")
            LuaFactory.createPlainLua(this@MainActivity).use { lua ->
                lua["logger"] = logger
                lua["BuildConfig"] = BuildConfig::class.java // set global value
                // run script, access to Java class
                lua.runText("print(BuildConfig.APPLICATION_ID); print(BuildConfig.VERSION_CODE)")
                lua.runScriptInAssets("lua/setup.lua")
                lua.runText("print(BuildConfig.APPLICATION_ID); print(BuildConfig.VERSION_CODE)")
            }
        }
    }
}
