package android.content.res

import android.util.DisplayMetrics
import com.lwp.lib.host.printLog

class PluginResource(assets: AssetManager, metrics: DisplayMetrics, config: Configuration) :
    Resources(assets, metrics, config) {
    override fun getText(id: Int): CharSequence {
        printLog("=============$id")
        return super.getText(id)
    }
}