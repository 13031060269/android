package com.lwp.lib.mvp.mapper

import androidx.databinding.ViewDataBinding
import com.lwp.lib.mvp.interfaces.Factory
import com.lwp.lib.utils.cast

abstract class VariableFactory : VariableMapper<ViewDataBinding> {
    override fun attachToDataBinding(container: ViewDataBinding, factory: Factory) {
        getVariableMapper(container)?.attachToDataBinding(container, factory)
    }

    private inline fun <reified T : Any> getVariableMapper(container: T): VariableMapper<T>? {
        return cast(maps[container::class.java])
    }

    companion object {
        @JvmStatic
        val variableMapper by lazy { VariableFactoryImpl() }

        @JvmStatic
        val maps = HashMap<Class<*>, VariableMapper<*>>()
    }

    inline fun <reified T : Any> addMapper(mapper: VariableMapper<T>) {
        addMapper(T::class.java, mapper)
    }

    fun <T : Any> addMapper(clazz: Class<T>, mapper: VariableMapper<T>) {
        maps[clazz] = mapper
    }
}
