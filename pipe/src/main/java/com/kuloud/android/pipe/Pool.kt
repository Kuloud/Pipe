package com.kuloud.android.pipe

object Pool {
    private val mPumpPipes = HashMap<String, PumpPipe<*, *>>()

    @JvmStatic
    fun <I, O> registerPumpPipe(id: String, pipe: PumpPipe<I, O>) {
        mPumpPipes[id] = pipe
    }

    fun <I, O> getPumpPipe(id: String): PumpPipe<I, O>? {
        return mPumpPipes[id] as PumpPipe<I, O>?
    }
}