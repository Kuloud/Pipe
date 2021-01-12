package com.kuloud.android.pipe

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PipeService : Service() {
    private val mPumpTaps = HashMap<String, PumpTap>()
    private val mBinder = object : IPipeService.Stub() {
        override fun subscribe(id: String): Int {
            mPumpTaps[id] = PumpTap(id)
            if (Pool.getPumpPipe<Any, Any>(id) != null) {
                return 0
            } else {
                return -1
            }
        }

        override fun unsubscribe(id: String): Int {
            mPumpTaps.remove(id)
            return 0
        }

        override fun process(pumpId: String, input: Flow): Flow? {
            val pumpTap = mPumpTaps[pumpId]
            if (pumpTap != null) {
                val ins = pumpTap.paser.parse(input)
                if (ins.isNotEmpty()) {
                    val inData = pumpTap.parceler.byte2Obj(ins)
                    if (inData != null) {
                        val outData = Pool.getPumpPipe<Any, Any>(pumpId)?.run(inData)
                        if (outData != null) {
                            val outs = pumpTap.parceler.obj2Byte(outData)
                            return Flow(1, 0, outs)
                        }
                    }
                }
            }
            return null
        }

    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}