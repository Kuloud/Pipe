package com.kuloud.android.pipe.io

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.nio.ByteBuffer

class ByteBufferSerializer : Serializer<ByteBuffer>() {
    override fun write(kryo: Kryo?, output: Output?, buffer: ByteBuffer?) {
        buffer?.apply {
            output?.writeInt(buffer.capacity())
            output?.write(buffer.array())
        }
    }

    override fun read(kryo: Kryo?, input: Input?, type: Class<ByteBuffer>?): ByteBuffer {
        input?.apply {
            val length = input.readInt()
            val buffer = ByteArray(length)
            //noinspection ResultOfMethodCallIgnored
            input.read(buffer, 0, length)

            return ByteBuffer.wrap(buffer, 0, length)
        }

        return ByteBuffer.allocate(0)

    }
}