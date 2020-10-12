package io.github.noahzu.videoedit.base

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object DisplayUtils {
    /**
     * 将px值转换为dp值
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将dp值转换为px值
     */
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dp2pxf(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 将px值转换为sp值
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidthPixels(context: Context): Int {
        val metric = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE)
        (windowManager as WindowManager).defaultDisplay.getRealMetrics(metric)
        return metric.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeightPixels(context: Context): Int {
        val metric = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(metric)
        return metric.heightPixels
    }

    /**
     * 获取标题栏高度
     */
    fun getStatusBarHeight(appContext: Context): Int {
        var height = 0
        val resourceId = appContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = appContext.resources.getDimensionPixelSize(resourceId)
        }
        return height
    }


    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    fun getNavigationHeight(context: Context): Int {
        var result = 0
        var resourceId = 0
        var rid = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        if (rid != 0) {
            resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return context.resources.getDimensionPixelSize(resourceId);
        } else
            return 0
    }

    fun  getContentHeightOutStatusAndStatus(context: Context):Int{
        return getScreenHeightPixels(context)- getStatusBarHeight(context)- getNavigationHeight(context)
    }
}
