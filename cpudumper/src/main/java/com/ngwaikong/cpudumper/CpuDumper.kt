package com.ngwaikong.cpudumper

import android.util.Log
import com.ngwaikong.cpudumper.sampler.*
import java.io.BufferedOutputStream
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Created by ngwaikong on 2018/5/27.
 * main api
 */
class CpuDumper(private val pid: String) {
    companion object {
        private const val TAG = "CpuDumper"
    }

    private val pidStateSampler = ProcPidStateSampler(pid)
    private val procStateSampler = ProcStateSampler()
    private val procPidTaskStatSampler = ProcPidTaskStatSampler(pid)
    private val javaStacksSampler = JavaStacksSampler()
    private val kill3Cmd = Kill3Cmd(pid)
    private var topCmd: TopCmd? = null

    fun dumpToString(): String {
        val sb = StringBuilder()
        sb.append("dumpSystemRate:").append(dumpSystemRate().toString()).append("\n")
                .append("dumpAppRate:").append(dumpAppRate().toString()).append("\n")
                .append("dumpAppThread:").append(procPidTaskStatSampler.dumpToString()).append("\n")
                .append("dumpJavaStack:").append(javaStacksSampler.dumpToString()).append("\n")
        return sb.toString()
    }

    fun dumpSystemRate(): CpuInfo {
        return procStateSampler.dump()
    }

    fun dumpAppRate(): CpuInfo {
        return pidStateSampler.dump()
    }

    fun dumpAppThreadCpu(): ArrayList<ProcPidTaskStatSampler.ThreadCpuInfo?> {
        return procPidTaskStatSampler.dump()
    }

    fun dumpTop(endTime: Long, outputStream: BufferedOutputStream?) {
        topCmd = TopCmd("-s cpu -m 10 -t", outputStream)
        object : Thread() {
            override fun run() {
                try {
                    topCmd?.dump()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }.start()
        Timer().schedule(timerTask { topCmd?.stop() }, endTime)
    }

    fun dumpJavaStack(): TreeMap<Thread, Array<StackTraceElement>> {
        kill3Cmd.dump() // to /data/anr/traces.txt
        return javaStacksSampler.dump()
    }

    fun dumpNativeStack(){

    }

    data class CpuInfo(val cpuTime: Long, val log: String)
}