package com.kuloud.android.pipe.io

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * 基于Kryo的数据序列化/反序列化数据包装器
 */
class Parceler {
    fun obj2Byte(obj: Any): ByteArray {
        var bo: ByteArrayOutputStream? = null
        var output: Output? = null
        try {
            // object to bytearray
            bo = ByteArrayOutputStream()
            output = Output(bo)
            kryo.get().writeClassAndObject(output, obj)
            output.flush()
            return bo.toByteArray()
        } catch (e: Exception) {
        } finally {
            if (bo != null) {
                try {
                    bo.close()
                } catch (e: IOException) {

                }
            }
            if (output != null) {
                try {
                    output.close()
                } catch (e: Exception) {

                }
            }
        }
        return ByteArray(0)
    }

    fun byte2Obj(bytes: ByteArray): Any? {
        var bi: ByteArrayInputStream? = null
        val obj: Any
        var input: Input? = null
        try {
            // bytearray to object
            bi = ByteArrayInputStream(bytes)
            input = Input(bi)
            obj = kryo.get().readClassAndObject(input)
            return obj
        } catch (e: java.lang.Exception) {

        } finally {
            if (bi != null) {
                try {
                    bi.close()
                } catch (e: IOException) {

                }
            }
            if (input != null) {
                try {
                    input.close()
                } catch (e: java.lang.Exception) {

                }
            }
        }

        return null
    }

    companion object {
        private val kryo: ThreadLocal<Kryo> = object : ThreadLocal<Kryo>() {
            override fun initialValue(): Kryo? {
                val kryo = Kryo()
                kryo.register(ByteBuffer.allocate(0)::class.java, ByteBufferSerializer())
                return kryo
            }
        }
    }
}