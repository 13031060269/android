package com.lwp.lib.host

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import com.android.dx.*
import java.io.File
import java.lang.reflect.Modifier

internal object ClassGenerator {
    private const val FIELD_ASSERT_MANAGER = "mAssertManager"
    private const val FIELD_RESOURCES = "mResources"
    private const val FIELD_mOnCreated = "mOnCreated"

    fun <S, D : S?> createActivityDex(
        superClassName: String,
        id: String, packageName: String, dexPath: String
    ): String {
        val activityPath = File(dexPath)
        val dexMaker = DexMaker()
        val generatedType: TypeId<D> = TypeId.get(
            'L'.toString() + pluginActivity.replace(
                '.',
                '/'
            ) + ';'
        )
        val superType: TypeId<S> =
            TypeId.get('L'.toString() + superClassName.replace('.', '/') + ';')
        dexMaker.declare(generatedType, "", Modifier.PUBLIC or Modifier.FINAL, superType)
        declareFields(dexMaker, generatedType, id, packageName)
        declare_constructor(dexMaker, generatedType, superType)
        declareMethod_onCreate(dexMaker, generatedType, superType)
        declareMethod_getAssets(dexMaker, generatedType, superType)
        declareMethod_getResources(dexMaker, generatedType, superType)
        declareMethod_startActivityForResult(dexMaker, generatedType, superType)
        declareMethod_onBackPressed(dexMaker, generatedType, superType)
//        declareMethod_startService(dexMaker, generatedType)
//        declareMethod_bindService(dexMaker, generatedType)
//        declareMethod_unbindService(dexMaker, generatedType)
//        declareMethod_stopService(dexMaker, generatedType)
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onStart")
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onRestart")
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onResume")
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onPause")
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onStop")
        declareLifeCyleMethod(dexMaker, generatedType, superType, "onDestroy")
        declareMethod_attachBaseContext(dexMaker, generatedType, superType)
        declareMethod_getComponentName(dexMaker, generatedType, superClassName)
        declareMethod_getPackageName(dexMaker, generatedType, packageName)
        declareMethod_getIntent(dexMaker, generatedType, superType)
        declareMethod_setTheme(dexMaker, generatedType, superType)
        HostUtils.saveToFile(dexMaker.generate(), activityPath)
        return activityPath.absolutePath
    }

    private fun <S, D : S?> declareFields(
        dexMaker: DexMaker, generatedType: TypeId<D>,
        id: String, pkgName: String
    ) {
        val _id = generatedType.getField(
            TypeId.STRING,
            "_id"
        )
        dexMaker.declare(_id, Modifier.PRIVATE or Modifier.STATIC or Modifier.FINAL, id)
        val _pkg = generatedType.getField(
            TypeId.STRING,
            "_pkg"
        )
        dexMaker.declare(_pkg, Modifier.PRIVATE or Modifier.STATIC or Modifier.FINAL, pkgName)
        val AssetManager = TypeId.get(AssetManager::class.java)
        val Resources = TypeId.get(
            Resources::class.java
        )
        val asm = generatedType.getField(
            AssetManager,
            FIELD_ASSERT_MANAGER
        )
        dexMaker.declare(asm, Modifier.PRIVATE, null)
        val res = generatedType.getField(Resources, FIELD_RESOURCES)
        dexMaker.declare(res, Modifier.PRIVATE, null)
        val beforeOnCreate = generatedType.getField(TypeId.BOOLEAN, FIELD_mOnCreated)
        dexMaker.declare(beforeOnCreate, Modifier.PRIVATE, null)
    }

    private fun <D> get_id(
        generatedType: TypeId<D>,
        methodCode: Code
    ): Local<String> {
        val id = methodCode.newLocal(TypeId.STRING)
        val fieldId = generatedType.getField(
            TypeId.STRING,
            "_id"
        )
        methodCode.sget(fieldId, id)
        return id
    }

    private fun <S, D : S?> declareMethod_setTheme(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        // Types
        val methodName = "setTheme"
        val method = generatedType.getMethod(
            TypeId.VOID,
            methodName, TypeId.INT
        )
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val methodOverride = activityOverriderTypeId
            .getMethod(
                TypeId.INT, "getPlugActivityTheme", TypeId.get(Activity::class.java),
                TypeId.STRING
            )
        // locals
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        val localThis = methodCode.getThis(generatedType)
        val resId = methodCode.getParameter(0, TypeId.INT)
        val int0 = methodCode.newLocal(TypeId.INT)
        val lcoalonCreate = methodCode.newLocal(TypeId.BOOLEAN)
        val localFalse = methodCode.newLocal(TypeId.BOOLEAN)
        val id = get_id(generatedType, methodCode)
        val onCreated = generatedType.getField(TypeId.BOOLEAN, FIELD_mOnCreated)
        methodCode.iget(onCreated, lcoalonCreate, localThis)
        val ifBeforeOnCreate = Label()
        methodCode.loadConstant(localFalse, false)
        methodCode.compare(Comparison.NE, ifBeforeOnCreate, lcoalonCreate, localFalse)
        methodCode.invokeStatic(methodOverride, resId, localThis, id)
        methodCode.mark(ifBeforeOnCreate)
        //
        val if_resId = Label()
        methodCode.loadConstant(int0, 0)
        methodCode.compare(Comparison.EQ, if_resId, resId, int0)
        val superMethod = superType.getMethod(TypeId.VOID, methodName, TypeId.INT)
        methodCode.invokeSuper(superMethod, null, localThis, resId)
        methodCode.mark(if_resId)
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_attachBaseContext(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        // Types
        val Context = TypeId.get(Context::class.java)
        val AssetManager = TypeId.get(AssetManager::class.java)
        val Resources = TypeId.get(
            Resources::class.java
        )
        val assertManager = generatedType.getField(
            AssetManager,
            FIELD_ASSERT_MANAGER
        )
        val resources = generatedType.getField(Resources, FIELD_RESOURCES)
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val DisplayMetrics = TypeId.get(DisplayMetrics::class.java)
        val Configuration = TypeId.get(
            Configuration::class.java
        )
        val method = generatedType.getMethod(
            TypeId.VOID,
            "attachBaseContext", Context
        )
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        val ObjArr = TypeId.get(
            Array<Any>::class.java
        )
        val localThis = methodCode.getThis(generatedType)
        val rsArr = methodCode.newLocal(ObjArr)
        val rsArr0 = methodCode.newLocal(TypeId.OBJECT)
        val rsArr1 = methodCode.newLocal(TypeId.OBJECT)
        val base = methodCode.getParameter(0, Context)
        val newbase = methodCode.newLocal(Context)
        val index0 = methodCode.newLocal(TypeId.INT)
        val index1 = methodCode.newLocal(TypeId.INT)
        val localAsm = methodCode.newLocal(AssetManager)
        val superRes = methodCode.newLocal(Resources)
        val mtrc = methodCode.newLocal(DisplayMetrics)
        val cfg = methodCode.newLocal(Configuration)
        val resLocal = methodCode.newLocal(Resources)
        val id = get_id(generatedType, methodCode)
        methodCode.loadConstant(index0, 0)
        methodCode.loadConstant(index1, 1)
        val methodOverride = activityOverriderTypeId.getMethod(
            ObjArr,
            "overrideAttachBaseContext", TypeId.STRING, TypeId.get(Activity::class.java), Context
        )
        methodCode.invokeStatic(methodOverride, rsArr, id, localThis, base)
        methodCode.aget(rsArr0, rsArr, index0)
        methodCode.aget(rsArr1, rsArr, index1)
        methodCode.cast(newbase, rsArr0)
        methodCode.cast(localAsm, rsArr1)
        methodCode.iput(assertManager, localThis, localAsm)
        // superRes = base.getResources();
        val methodGetResources = Context.getMethod(
            Resources,
            "getResources"
        )
        methodCode.invokeVirtual(methodGetResources, superRes, base)
        val getDisplayMetrics = Resources
            .getMethod(DisplayMetrics, "getDisplayMetrics")
        methodCode.invokeVirtual(getDisplayMetrics, mtrc, superRes)
        val getConfiguration = Resources
            .getMethod(Configuration, "getConfiguration")
        methodCode.invokeVirtual(getConfiguration, cfg, superRes)
        val res_constructor = Resources.getConstructor(
            AssetManager, DisplayMetrics, Configuration
        )
        methodCode.newInstance(resLocal, res_constructor, localAsm, mtrc, cfg)
        methodCode.iput(resources, localThis, resLocal)
        val superMethod = superType.getMethod(
            TypeId.VOID,
            "attachBaseContext", Context
        )
        methodCode.invokeSuper(superMethod, null, localThis, newbase)
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_getIntent(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val Intent = TypeId.get(Intent::class.java)
        val ComponentName = TypeId.get(ComponentName::class.java)
        val methodName = "getIntent"
        val method = generatedType
            .getMethod(Intent, methodName)
        val superMethod = superType
            .getMethod(Intent, methodName)
        val code = dexMaker.declare(method, Modifier.PUBLIC)
        val localThis = code.getThis(generatedType)
        val i = code.newLocal(Intent)
        val localComp = code.newLocal(ComponentName)
        val getComponent = generatedType
            .getMethod(ComponentName, "getComponentName")
        code.invokeVirtual(getComponent, localComp, localThis)
        val setComponent = Intent
            .getMethod(Intent, "setComponent", ComponentName)
        code.invokeSuper(superMethod, i, localThis)
        code.invokeVirtual(setComponent, i, i, localComp)
        code.returnValue(i)
    }

    private fun <S, D : S?> declareMethod_getPackageName(
        dexMaker: DexMaker,
        generatedType: TypeId<D>,
        pkgName: String
    ) {
        val method = generatedType.getMethod(
            TypeId.STRING,
            "getPackageName"
        )
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        val pkg = methodCode.newLocal(TypeId.STRING)
        methodCode.loadConstant(pkg, pkgName)
        methodCode.returnValue(pkg)
    }

    private fun <S, D : S?> declareMethod_getComponentName(
        dexMaker: DexMaker, generatedType: TypeId<D>, superClassName: String
    ) {
        val ComponentName = TypeId.get(ComponentName::class.java)
        val method = generatedType.getMethod(
            ComponentName,
            "getComponentName"
        )
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        val pkg = methodCode.newLocal(TypeId.STRING)
        val cls = methodCode.newLocal(TypeId.STRING)
        val localComp = methodCode.newLocal(ComponentName)
        run {
            val fieldPkg = generatedType.getField(
                TypeId.STRING,
                "_pkg"
            )
            methodCode.sget(fieldPkg, pkg)
        }
        methodCode.loadConstant(cls, superClassName)
        val comp_constructor = ComponentName.getConstructor(
            TypeId.STRING, TypeId.STRING
        )
        methodCode.newInstance(localComp, comp_constructor, pkg, cls)
        methodCode.returnValue(localComp)
    }

    private fun <S, D : S?> declareMethod_onCreate(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        //
        val Bundle = TypeId.get(Bundle::class.java)
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val method = generatedType.getMethod(
            TypeId.VOID,
            "onCreate", Bundle
        )
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        val localThis = methodCode.getThis(generatedType)
        val localBundle = methodCode.getParameter(0, Bundle)
        val localCreated = methodCode.newLocal(TypeId.BOOLEAN)
        val id = get_id(generatedType, methodCode)
        val beforeOnCreate = generatedType.getField(TypeId.BOOLEAN, FIELD_mOnCreated)
        methodCode.loadConstant(localCreated, true)
        methodCode.iput(beforeOnCreate, localThis, localCreated)
        val method_call_onCreate = activityOverriderTypeId
            .getMethod(
                TypeId.VOID, "onCreate", TypeId.STRING,
                TypeId.get(Activity::class.java)
            )
        methodCode.invokeStatic(method_call_onCreate, null, id, localThis)
        val superMethod = superType.getMethod(
            TypeId.VOID, "onCreate",
            Bundle
        )
        methodCode.invokeSuper(superMethod, null, localThis, localBundle)
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_getResources(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val Resources = TypeId.get(
            Resources::class.java
        )
        val getResources = generatedType.getMethod(
            Resources, "getResources"
        )
        val code = dexMaker.declare(getResources, Modifier.PUBLIC)
        val localThis = code.getThis(generatedType)
        val localRes = code.newLocal(Resources)
        val nullV = code.newLocal(Resources)
        code.loadConstant(nullV, null)
        val res = generatedType.getField(Resources, FIELD_RESOURCES)
        code.iget(res, localRes, localThis)
        val localResIsNull = Label()
        code.compare(Comparison.NE, localResIsNull, localRes, nullV)
        val superGetResources = superType.getMethod(
            Resources, "getResources"
        )
        code.invokeSuper(superGetResources, localRes, localThis)
        code.mark(localResIsNull)
        code.returnValue(localRes)
    }

    private fun <S, D : S?> declareMethod_getAssets(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val assetManager = TypeId.get(AssetManager::class.java)
        val getAssets = generatedType.getMethod(
            assetManager, "getAssets"
        )
        val code = dexMaker.declare(getAssets, Modifier.PUBLIC)
        val localThis = code.getThis(generatedType)
        val localAsm = code.newLocal(assetManager)
        val nullV = code.newLocal(assetManager)
        code.loadConstant(nullV, null)
        val res = generatedType.getField(
            assetManager,
            FIELD_ASSERT_MANAGER
        )
        code.iget(res, localAsm, localThis)
        val localAsmIsNull = Label()
        code.compare(Comparison.NE, localAsmIsNull, localAsm, nullV)
        val superGetAssetManager = superType.getMethod(
            assetManager, "getAssets"
        )
        code.invokeSuper(superGetAssetManager, localAsm, localThis)
        code.mark(localAsmIsNull)
        code.returnValue(localAsm)
    }

    private fun <S, D : S?> declare_constructor(
        dexMaker: DexMaker,
        generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val method = generatedType.getConstructor()
        val constructorCode = dexMaker.declare(method, Modifier.PUBLIC)
        val localThis = constructorCode.getThis(generatedType)
        val superConstructor = superType.getConstructor()
        constructorCode.invokeDirect(superConstructor, null, localThis)
        constructorCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_startActivityForResult(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val intent = TypeId.get(Intent::class.java)
        val requestCode = TypeId.INT
        val bundle = TypeId.get(Bundle::class.java)
        val params: Array<TypeId<*>>
        val methodName = "startActivityForResult"
        params = arrayOf(intent, requestCode, bundle)
        val method = generatedType.getMethod(
            TypeId.VOID,
            methodName, *params
        )
        val superMethod = superType.getMethod(
            TypeId.VOID,
            methodName, *params
        )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val methodOverride = activityOverriderTypeId
            .getMethod(
                intent, "overrideStartActivityForResult",
                TypeId.get(Activity::class.java), TypeId.STRING,
                intent
            )
        val localThis = methodCode.getThis(generatedType)
        val newIntent = methodCode.newLocal(intent)
//        val nullParamBundle = methodCode.newLocal(bundle)
        val id = get_id(generatedType, methodCode)
//        methodCode.loadConstant(nullParamBundle, null)
        val args: Array<Local<*>>
        args = arrayOf(
            localThis, id, methodCode.getParameter(0, intent) //
        )
        methodCode.invokeStatic(methodOverride, newIntent, *args)
        methodCode.invokeSuper(
            superMethod, null,
            localThis //
            , newIntent //
            , methodCode.getParameter(1, requestCode) //
            , methodCode.getParameter(2, bundle) //
        )
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_onBackPressed(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>
    ) {
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val method = generatedType.getMethod(
            TypeId.VOID,
            "onBackPressed"
        )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        val localThis = methodCode.getThis(generatedType)
        val localBool = methodCode.newLocal(TypeId.BOOLEAN)
        val localFalse = methodCode.newLocal(TypeId.BOOLEAN)
        val id = get_id(generatedType, methodCode)
        methodCode.loadConstant(localFalse, false)
        val methodOverride = activityOverriderTypeId
            .getMethod(
                TypeId.BOOLEAN,
                "overrideOnBackPressed",
                TypeId.get(Activity::class.java),
                TypeId.STRING
            )
        methodCode.invokeStatic(methodOverride, localBool, localThis, id)
        // codeBlock: if start
        val localBool_isInvokeSuper = Label()
        methodCode.compare(
            Comparison.EQ, localBool_isInvokeSuper, localBool,
            localFalse
        )
        val superMethod = superType.getMethod(
            TypeId.VOID,
            "onBackPressed"
        )
        methodCode.invokeSuper(superMethod, null, localThis)
        methodCode.mark(localBool_isInvokeSuper)
        // codeBlock: if end
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_startService(
        dexMaker: DexMaker, generatedType: TypeId<D>
    ) {
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val returnType = TypeId.get(ComponentName::class.java)
        val Intent = TypeId.get(Intent::class.java)
        val method = generatedType.getMethod(
            returnType,
            "startService", Intent
        )
        val methodOverride = activityOverriderTypeId
            .getMethod(
                returnType,
                "overrideStartService",
                TypeId.get(Activity::class.java),
                TypeId.STRING,
                Intent
            )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        val localThis = methodCode.getThis(generatedType)
        val localComponentName = methodCode.newLocal(returnType)
        val id = get_id(generatedType, methodCode)
        methodCode.invokeStatic(
            methodOverride,
            localComponentName //
            , localThis, id, methodCode.getParameter(0, Intent)
        )
        methodCode.returnValue(localComponentName)
    }

    private fun <S, D : S?> declareMethod_bindService(
        dexMaker: DexMaker, generatedType: TypeId<D>
    ) {
        //boolean bindService(intent, conn, flags);
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val returnType = TypeId.BOOLEAN
        val Intent = TypeId.get(Intent::class.java)
        val Conn = TypeId.get(ServiceConnection::class.java)
        val method = generatedType.getMethod(
            returnType,
            "bindService", Intent, Conn, TypeId.INT
        )
        val methodOveride = activityOverriderTypeId
            .getMethod(
                returnType,
                "overrideBindService",
                TypeId.get(Activity::class.java),
                TypeId.STRING,
                Intent,
                Conn,
                TypeId.INT
            )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        // locals
        val localThis = methodCode.getThis(generatedType)
        val localBool = methodCode.newLocal(returnType)
        val id = get_id(generatedType, methodCode)
        methodCode.invokeStatic(
            methodOveride,
            localBool //
            ,
            localThis,
            id,
            methodCode.getParameter(0, Intent),
            methodCode.getParameter(1, Conn),
            methodCode.getParameter(2, TypeId.INT)
        )
        methodCode.returnValue(localBool)
    }

    private fun <S, D : S?> declareMethod_unbindService(
        dexMaker: DexMaker, generatedType: TypeId<D>
    ) {
        //void unbindService( conn);
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val Conn = TypeId.get(ServiceConnection::class.java)
        val method = generatedType.getMethod(
            TypeId.VOID,
            "unbindService", Conn
        )
        val methodOverride = activityOverriderTypeId
            .getMethod(
                TypeId.VOID,
                "overrideUnbindService",
                TypeId.get(Activity::class.java),
                TypeId.STRING,
                Conn
            )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        val localThis = methodCode.getThis(generatedType)
        val id = get_id(generatedType, methodCode)
        methodCode.invokeStatic(
            methodOverride,
            null //
            , localThis, id, methodCode.getParameter(0, Conn)
        )
        methodCode.returnVoid()
    }

    private fun <S, D : S?> declareMethod_stopService(
        dexMaker: DexMaker, generatedType: TypeId<D>
    ) {
        //boolean stopService(intent);
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val returnType = TypeId.BOOLEAN
        val Intent = TypeId.get(Intent::class.java)
        //
        val method = generatedType.getMethod(
            returnType,
            "stopService", Intent
        )
        val methodOveride = activityOverriderTypeId
            .getMethod(
                returnType,
                "overrideStopService",
                TypeId.get(Activity::class.java),
                TypeId.STRING,
                Intent
            )
        val methodCode = dexMaker.declare(method, Modifier.PUBLIC)
        // locals
        val localThis = methodCode.getThis(generatedType)
        val localBool = methodCode.newLocal(returnType)
        val id = get_id(generatedType, methodCode)
        methodCode.invokeStatic(
            methodOveride,
            localBool //
            , localThis, id, methodCode.getParameter(0, Intent)
        )
        methodCode.returnValue(localBool)
    }

    private fun <S, D : S?> declareLifeCyleMethod(
        dexMaker: DexMaker, generatedType: TypeId<D>, superType: TypeId<S>,
        methodName: String
    ) {
        val activityOverriderTypeId = TypeId.get(
            ActivityOverrider::class.java
        )
        val method = generatedType.getMethod(
            TypeId.VOID,
            methodName
        )
        val methodCode = dexMaker.declare(method, Modifier.PROTECTED)
        // locals
        val localThis = methodCode.getThis(generatedType)
        val id = get_id(generatedType, methodCode)
        val superMethod = superType.getMethod(
            TypeId.VOID,
            methodName
        )
        methodCode.invokeSuper(superMethod, null, localThis)
        val methodOverride = activityOverriderTypeId
            .getMethod(
                TypeId.VOID, methodName,
                TypeId.STRING, TypeId.get(Activity::class.java)
            )
        methodCode.invokeStatic(methodOverride, null, id, localThis)
        methodCode.returnVoid()
    }
}

internal class ActivityOverrider {
    companion object {
        @JvmStatic
        fun overrideStartActivityForResult(
            fromAct: Activity, id: String, intent: Intent
        ): Intent {
            return HostManager.startActivityForResult(fromAct, id, intent)
        }

        @JvmStatic
        fun overrideAttachBaseContext(
            apkId: String,
            fromAct: Activity,
            base: Context
        ): Array<Any?>? {
            return findApkInfo(apkId)?.attachBaseContext(fromAct)
        }

        @JvmStatic
        private fun changeActivityInfo(id: ApkControl?, activity: Context) {
            if (activity.javaClass.name != pluginActivity) {
                return
            }
            try {
                val actName = activity.javaClass.superclass!!.name
                val fieldActivityInfo = Activity::class.java.getDeclaredField("mActivityInfo")
                fieldActivityInfo.isAccessible = true
                fieldActivityInfo[activity] = id?.findActivityByClassName(actName)
            } catch (e: Exception) {
                return
            }
        }

        @JvmStatic
        fun getPlugActivityTheme(fromAct: Activity, id: String): Int {
            val plugin = findApkInfo(id)
            if (plugin == null) {
                fromAct.finish()
                return 0
            }
            val actName = fromAct.javaClass.superclass!!.name
            val actInfo = plugin.findActivityByClassName(actName)
            val rs = actInfo!!.themeResource
            changeActivityInfo(plugin, fromAct)
            return rs
        }

        @JvmStatic
        fun overrideOnBackPressed(fromAct: Activity, id: String): Boolean {
//            val plInfo = findApkInfo(id)
//            val actName = fromAct.javaClass.superclass!!.name
//            val actInfo = plInfo?.findActivityByClassName(actName)
            return true
        }

        @JvmStatic
        fun onCreate(id: String, fromAct: Activity) {
            val info = findApkInfo(id)
            if (info == null) {
                fromAct.finish()
                return
            }
            HostWM(fromAct, info)
            try {
                val applicationField = Activity::class.java
                    .getDeclaredField("mApplication")
                applicationField.isAccessible = true
                applicationField[fromAct] = info?.application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val actName = fromAct.javaClass.superclass!!.name
            val actInfo = info?.findActivityByClassName(actName)
            val resTheme = actInfo!!.themeResource
            if (resTheme != 0) {
                var hasNotSetTheme = true
                try {
                    val mTheme = ContextThemeWrapper::class.java
                        .getDeclaredField("mTheme")
                    mTheme.isAccessible = true
                    hasNotSetTheme = mTheme[fromAct] == null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (hasNotSetTheme) {
                    changeActivityInfo(info, fromAct)
                    fromAct.setTheme(resTheme)
                }
            }
            info.lifeCycle.onCreate(fromAct)
        }

        @JvmStatic
        fun onResume(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onResume(fromAct)
        }

        @JvmStatic
        fun onStart(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onStart(fromAct)
        }

        @JvmStatic
        fun onRestart(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onRestart(fromAct)
        }

        @JvmStatic
        fun onPause(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onPause(fromAct)
        }

        @JvmStatic
        fun onStop(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onStop(fromAct)
        }

        @JvmStatic
        fun onDestroy(id: String, fromAct: Activity) {
            findApkInfo(id)?.lifeCycle?.onDestroy(fromAct)
        }
    }
}