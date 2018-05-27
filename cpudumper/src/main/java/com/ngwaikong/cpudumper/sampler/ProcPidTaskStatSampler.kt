package com.ngwaikong.cpudumper.sampler

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.RandomAccessFile


/**
 * Created by ngwaikong on 2018/5/27.
 * file proc/pid/task/stat
 *
 */
class ProcPidTaskStatSampler(private val pid: String) {

    fun dump(): ArrayList<ThreadCpuInfo?> {
        val dir = File("/proc/$pid/task")
        val list = ArrayList<ThreadCpuInfo?>()
        if (!dir.exists()) {
            return list
        }
        val files = dir.listFiles()
        (0 until files.size).mapTo(list) { parse(files[it]) }
        return list
    }

    private fun parse(file: File): ThreadCpuInfo? {
        val tid = Integer.valueOf(file.name).toLong()
        val statFile = File(file.absolutePath + "/stat")

        val input = BufferedReader(FileReader(statFile))
        var threadCpuInfo: ThreadCpuInfo? = null
        input.use {
            val readLine = (input as BufferedReader).readLine() ?: return null
            threadCpuInfo = parseStat(readLine)
            threadCpuInfo?.tid = tid
            if (tid == pid.toLong()) {
                threadCpuInfo?.name = "(main)"
            }
        }
        return threadCpuInfo
    }

    private fun parseStat(line: String): ThreadCpuInfo {
        var threadName = ""
        val div = line.trim().split(" ")
        var j = 1
        while (j < div.size) {
            threadName += div[j]
            if (threadName.endsWith(")")) {
                break
            } else {
                threadName += " "
            }
            j++
        }
        val cpuTimeIndex1 = 13 + j - 1
        val cpuTimeIndex2 = 14 + j - 1
        val threadInfo = ThreadCpuInfo()
        threadInfo.time = (div[cpuTimeIndex1]).toLong() + (div[cpuTimeIndex2]).toLong()
        threadInfo.name = threadName
        return threadInfo
    }

    fun dumpToString(): String {
        val result = dump()
        val sb = StringBuffer()
        for (i in 0 until result.size) {
            val info = result[i]
            if (info != null) {
                sb.append("index = ").append(i)
                        .append(",name = ").append(info.name)
                        .append(",tid = ").append(info.tid)
                        .append(",time = ").append(info.time)
                        .append(",\n")
            }
        }
        return sb.toString()
    }

    class ThreadCpuInfo {
        var tid: Long = 0
        var name: String = ""
        var time: Long = 0
    }
}