package com.kuloud.android.pipe

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.didi.aoe.library.logging.LoggerFactory


class PipeServiceConnection @JvmOverloads constructor(
    private val context: Context,
    private val callback: Runnable?
) {
    private val mLogger = LoggerFactory.getLogger("PipeServiceConnection")

    private var pipeService: IPipeService? = null

    private val deathRecipient = IBinder.DeathRecipient {
        setPipeService(null)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                service?.linkToDeath(deathRecipient, 0)
            } catch (e: RemoteException) {
                mLogger.warn("Failed to bind a death recipient.", e)
            }
            setPipeService(IPipeService.Stub.asInterface(service))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mLogger.info("Disconnected from the service.")
            setPipeService(null)
        }

    }

    fun unbind() {
        try {
            context.unbindService(serviceConnection)
        } catch (e: IllegalArgumentException) {
            // Means not bound to the service. OK to ignore.
        }
        setPipeService(null)
    }

    fun getServiceInBound(): IPipeService? {
        if (pipeService?.asBinder()?.isBinderAlive != true) {
            setPipeService(null)
            return null
        }
        return pipeService

    }

    fun bindService() {
        if (pipeService != null) {
            return
        }

        val intent = Intent("com.kuloud.android.pipe.PipeService")
        val eintent = createExplicitFromImplicitIntent(context, intent)
        if (eintent != null) {
            context.bindService(
                Intent(eintent),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun createExplicitFromImplicitIntent(
        context: Context,
        implicitIntent: Intent
    ): Intent? {
        val pm = context.packageManager
        val resolveInfo = pm.queryIntentServices(implicitIntent, 0)
        mLogger.warn("Found services: ${resolveInfo.toTypedArray().contentToString()}")
        if (resolveInfo.size != 1) {
            return null
        }
        val serviceInfo = resolveInfo[0]
        val packageName = serviceInfo.serviceInfo.packageName
        val className = serviceInfo.serviceInfo.name
        val component = ComponentName(packageName, className)
        val explicitIntent = Intent(implicitIntent)
        explicitIntent.component = component
        return explicitIntent
    }

    private fun setPipeService(service: IPipeService?) {
        pipeService = service
        callback?.run()
    }

    private fun isServiceRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (serviceInfo in services) {
            val componentName = serviceInfo.service
            val serviceName = componentName.className
            if ("com.kuloud.android.pipe.PipeService" == serviceName) {
                return true
            }
        }
        return false
    }
}