package com.bennyhuo.luajavax.sample

import android.app.Activity
import android.os.Bundle
import com.bennyhuo.luajavax.LuaFactory
import com.bennyhuo.luajavax.sample.cases.basic
import com.bennyhuo.luajavax.sample.cases.testErrorMessageForJavaMethodCall
import com.bennyhuo.luajavax.sample.cases.testNameConflictForFieldAndMethod
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
            // basic()
            // testErrorMessageForJavaMethodCall()
            testNameConflictForFieldAndMethod()
        }
    }
}
