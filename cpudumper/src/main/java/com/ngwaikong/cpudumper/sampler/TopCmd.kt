package com.ngwaikong.cpudumper.sampler

import java.io.BufferedOutputStream

/**
 * Created by ngwaikong on 2018/5/27.
 */
class TopCmd(private val params: String, private val outputStream: BufferedOutputStream?) {
    companion object {
        private const val TOP_CMD = "top"
    }

    private var cmdProcess: Process? = null

    fun dump() {
        cmdProcess = Runtime.getRuntime().exec(TOP_CMD + " " + params)
        val inputStream = cmdProcess?.inputStream
        val buffer = ByteArray(1024)
        var len: Int? = inputStream?.read(buffer)
        while (len != null && len > 0) {
            outputStream?.write(buffer, 0, len)
            outputStream?.flush()
            len = inputStream?.read(buffer)
        }
        outputStream?.flush()
    }

    fun stop() {
        cmdProcess?.destroy()
    }
}