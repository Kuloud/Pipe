package com.kuloud.android.pipe

/**
 * 管道连接监听器
 */
const val PIPE_STATUS_OK = 0
const val PIPE_STATUS_ALREADY_EXIST = 1
const val PIPE_STATUS_SERVICE_NOT_FOUND = -1

interface OnPipeConnectedListener {
    fun onPipeConnected(status: Int)
}