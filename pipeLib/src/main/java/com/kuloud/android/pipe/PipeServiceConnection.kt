package com.kuloud.android.pipe

import android.os.IBinder

class PipeServiceConnection {
    private val deathRecipient = object :IBinder.DeathRecipient {
        override fun binderDied() {

        }

    }
}