package com.kuloud.android.pipe

import com.kuloud.android.pipe.io.Parceler

class PumpTap(val id: String) {
    internal val paser = Paser()
    internal val parceler = Parceler()
}