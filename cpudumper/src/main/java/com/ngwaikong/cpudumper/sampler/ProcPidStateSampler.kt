package com.ngwaikong.cpudumper.sampler

import com.ngwaikong.cpudumper.CpuDumper
import java.io.RandomAccessFile

/**
 * Created by ngwaikong on 2018/5/27.
 * file proc/[pid]>/stat
 *(14) utime %lu 用户态时间Amount of time that this process has been scheduled in user mode, measured in clock ticks (divide by sysconf(_SC_CLK_TCK)). This includes guest time, guest_time (time spent running a virtual CPU, see below), so that applications that are not aware of the guest time field do not lose that time from their calculations. 70
(15) stime %lu 系统态时间 Amount of time that this process has been scheduled in kernel mode, measured in clock ticks (divide by sysconf(_SC_CLK_TCK)). 18
 */
class ProcPidStateSampler(private val pid: String) {
    companion object {
        private const val MODE = "r"
    }

    var file: RandomAccessFile? = null
    fun dump(): CpuDumper.CpuInfo {
        val proPidStateFile = "/proc/" + pid + "/stat"
        if (file == null) {
            file = RandomAccessFile(proPidStateFile, MODE)
        } else {
            file?.seek(0)
        }
        file.use {
            val stringArray = (file as RandomAccessFile).readLine()
            val procStateInfo = parse(stringArray)
            return CpuDumper.CpuInfo(procStateInfo.getCpuTime(), procStateInfo.toString())
        }
    }

    private fun parse(stringArray: String?): ProcPidStateInfo {
        val split = stringArray?.split(" ")
        val utime = split?.get(13)?.toLong() ?: 0
        val stime = split?.get(14)?.toLong() ?: 0
        return ProcPidStateInfo(utime, stime)
    }

    data class ProcPidStateInfo(
            val utime: Long,
            val stime: Long
    ) {
        fun getCpuTime() = utime + stime
    }
}