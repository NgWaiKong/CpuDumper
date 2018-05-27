package com.ngwaikong.cpudumper.sampler

import android.util.Log
import java.io.DataOutputStream


/**
 * Created by ngwaikong on 2018/5/27.
 * /data/tombstone/
 * need su
 */
class DebuggerdCmd(private val params: String) {
    companion object {
        private const val CMD = "debuggerd"
        private const val TAG = "DebuggerdCmd"
        private const val CMD_ENTER = "\n"
        private const val SU = "su"
    }

    private var su: Process? = null

    fun dump() {
        try {
            su = Runtime.getRuntime().exec(SU)
            val dos = DataOutputStream(su?.getOutputStream())
            dos.writeBytes(CMD + params + CMD_ENTER)
            dos.flush()
            dos.close()
            su?.waitFor()
        } catch (e: Throwable) {
            Log.e(TAG, e.toString())
        }
    }
}