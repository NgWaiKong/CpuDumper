package com.ngwaikong.cpudumper.sampler

import android.util.Log


/**
 * Created by ngwaikong on 2018/5/27.
 */
class Kill3Cmd(private val params: String) {
    companion object {
        private const val CMD = "kill -3"
        private const val TAG = "Kill3Cmd"
    }

    private var cmdProcess: Process? = null

    fun dump() {
        try {
            cmdProcess = Runtime.getRuntime().exec(CMD + " " + params)
        } catch (e: Throwable) {
            Log.e(TAG, e.toString())
        }
    }
}