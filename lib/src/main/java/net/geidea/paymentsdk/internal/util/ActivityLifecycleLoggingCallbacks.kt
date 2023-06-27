package net.geidea.paymentsdk.internal.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal open class ActivityLifecycleLoggingCallbacks(
        private val activityClass: Class<out Activity>
) : Application.ActivityLifecycleCallbacks {

    private val activityName: String = activityClass.simpleName

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logLifecycleEvent(activity, "onCreate")
    }

    override fun onActivityStarted(activity: Activity) {
        logLifecycleEvent(activity, "onStart")
    }

    override fun onActivityResumed(activity: Activity) {
        logLifecycleEvent(activity, "onResume")
    }

    override fun onActivityPaused(activity: Activity) {
        logLifecycleEvent(activity, "onPause")
    }

    override fun onActivityStopped(activity: Activity) {
        logLifecycleEvent(activity, "onStop")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logLifecycleEvent(activity, "onSaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logLifecycleEvent(activity, "onDestroy")
    }

    protected fun shouldLog(activity: Activity): Boolean {
        return activityClass.isAssignableFrom(activity::class.java)
    }

    protected fun logLifecycleEvent(activity: Activity, event: String) {
        if (shouldLog(activity)) {
            Logger.logi("$activityName.$event")
        }
    }
}