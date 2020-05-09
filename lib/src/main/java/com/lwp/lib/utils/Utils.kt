package com.lwp.lib.utils

import com.lwp.lib.mvp.BaseModel
import java.lang.reflect.ParameterizedType

fun <T : BaseModel> getGenericType(obj: Any): Class<T> {
    val childClazz: Class<*> = obj.javaClass //子类字节码对象
    val genericSuperclass =
        childClazz.genericSuperclass as ParameterizedType?
    return genericSuperclass!!.actualTypeArguments[0] as Class<T>
}

//@SuppressLint("SoonBlockedPrivateApi", "PrivateApi")
//fun showSystemToast(msg: String) = try {
//    val getServiceMethod = Toast::class.java.getDeclaredMethod("getService");
//    getServiceMethod.isAccessible = true;
//
//    val iNotificationManager = getServiceMethod.invoke(null);
//    val iNotificationManagerProxy = Proxy.newProxyInstance(
//        Toast::class.java.classLoader,
//        arrayOf(forName("android.app.INotificationManager"))
//    ) { _, method, args ->
//
//        if ("enqueueToast" == method.name
//            || "enqueueToastEx" == method.name
//        ) {
//            args[0] = "android";
//        }
//        method.invoke(iNotificationManager, args);
//
//    }
//    val sServiceFiled = Toast::class.java.getDeclaredField("sService");
//    sServiceFiled.isAccessible = true;
//    sServiceFiled.set(null, iNotificationManagerProxy);
//} catch (e: Exception) {
//    e.printStackTrace();
//}

fun <T> cast(obj: Any): T {
    return obj as T
}