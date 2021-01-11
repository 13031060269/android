package com.lwp.compiler

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

open class BaseProcess : AbstractProcessor() {
    override fun process(
        set: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
//        val sb = StringBuilder()

//        roundEnvironment.rootElements.forEach {
//            sb.append(it::class.java)
//            sb.append("\n")
//            if (it is Symbol.ClassSymbol) {
//                sb.append(it.className())
//                sb.append("\n")
//            }
//        }
//        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, sb)
        return true
    }
}