package com.kuloud.android.pipe

interface PumpPipe<I, O> {
    fun run(input: I): O
}