package com.kuloud.android.pipe

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.didi.aoe.library.logging.LoggerFactory
import com.kuloud.android.pipe.io.Parceler

/**
 * 数据泵，负责建立数据连接，跨进程传输数据流。
 */
class Pump(private val context: Context, private val id: String) {
    private val mLogger = LoggerFactory.getLogger("Pump")

    private var mPipeServiceProxy: IPipeService? = null
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mPipeServiceProxy = IPipeService.Stub.asInterface(service)
            mPipeServiceProxy?.subscribe(id)
            mPipeConnectedListener?.onPipeConnected(PIPE_STATUS_OK)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mPipeServiceProxy?.unsubscribe(id)
            mPipeServiceProxy = null
        }

    }

    private val mParceler = Parceler()
    private var mPipeConnectedListener: OnPipeConnectedListener? = null

    fun connect(listener: OnPipeConnectedListener) {
        mPipeConnectedListener = listener
        if (!isBinderAlive()) {
            bindService()
            return
        }
        mPipeConnectedListener?.onPipeConnected(PIPE_STATUS_ALREADY_EXIST)
    }

    fun pumps(inlet: Any): Any? {
        if (isBinderAlive()) {
            val ins = mParceler.obj2Byte(inlet)
            if (ins.isNotEmpty()) {
                try {
                    val outs: ByteArray = paserToExecute(ins)
                    if (outs.isNotEmpty()) {
                        return mParceler.byte2Obj(outs)
                    }
                } catch (e: RemoteException) {
                    mLogger.error("process RemoteException: ", e)
                } catch (e: Exception) {
                    mLogger.error("process remote throw exception: ", e)
                }
            }
        } else {
            bindService()
        }
        return null
    }

    fun disconnect() {
        mPipeConnectedListener = null
        if (isBinderAlive()) {
            try {
                context.unbindService(mServiceConnection)
            } catch (e: java.lang.Exception) {
                mLogger.error("disconnect", e)
            }
        }

    }

    private fun isBinderAlive(): Boolean {
        return mPipeServiceProxy?.asBinder()?.isBinderAlive ?: false
    }

    private fun bindService() {
        val intent = Intent("com.kuloud.android.pipe.PipeService")
        val eintent = createExplicitFromImplicitIntent(context, intent)
        if (eintent == null) {
            mPipeConnectedListener?.onPipeConnected(PIPE_STATUS_SERVICE_NOT_FOUND)
        } else {
            context.bindService(
                Intent(eintent),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }

    }

    @Throws(RemoteException::class)
    private fun paserToExecute(ins: ByteArray): ByteArray {
        val msgs: List<Flow> = Paser.split(ins)
        for (msg in msgs) {
            // 数据拆包给remote service，数据完整接收后返回非空数据
            val processResult: Flow? = mPipeServiceProxy?.process(id, msg)
            if (processResult != null) {
                return processResult.data
            }
        }
        return ByteArray(0)
    }

    private fun createExplicitFromImplicitIntent(
        context: Context,
        implicitIntent: Intent
    ): Intent? {
        val pm = context.packageManager
        val resolveInfo = pm.queryIntentServices(implicitIntent, 0)
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
}