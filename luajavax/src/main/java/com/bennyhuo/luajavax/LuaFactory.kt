package com.bennyhuo.luajavax

import android.content.Context
import com.bennyhuo.luajavax.core.ILua
import com.bennyhuo.luajavax.core.Lua

object LuaFactory {

    @JvmStatic
    fun createPlainLua(context: Context): ILua = Lua(context)

}