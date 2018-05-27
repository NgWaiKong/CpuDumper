package com.ngwaikong.cpudumper.sampler

import com.ngwaikong.cpudumper.CpuDumper
import java.io.RandomAccessFile

/**
 * Created by ngwaikong on 2018/5/27.
 * sample proc/stat file
 * calc system cpu jiffies
 *
 *
 * file field
 * user (1) Time spent in user mode. 用户态时间
nice (2) Time spent in user mode with low priority (nice).
system (3) Time spent in system mode. 系统态时间
idle (4) Time spent in the idle task. 除IO等待之外的其他等待时间
iowait (since Linux 2.5.41) (5) Time waiting for I/O to complete. This value is not reliable, for the following reasons: 1. The CPU will not wait for I/O to complete; iowait is the time that a task is waiting for I/O to complete. When a CPU goes into idle state for outstanding task I/O, another task will be scheduled on this CPU. 2. On a multi-core CPU, the task waiting for I/O to complete is not running on any CPU, so the iowait of each CPU is difficult to calculate. 3. The value in this field may decrease in certain conditions. IO等待时间
irq (since Linux 2.6.0-test4) (6) Time servicing interrupts. 硬中断时间
softirq (since Linux 2.6.0-test4) (7) Time servicing softirqs. 软中断时间
 */
class ProcStateSampler() {
    companion object {
        private const val PROC_STATE_FILE = "/proc/stat"
        private const val MODE = "r"
    }

    var file: RandomAccessFile? = null
    fun dump(): CpuDumper.CpuInfo {
        if (file == null) {
            file = RandomAccessFile(PROC_STATE_FILE, MODE)
        } else {
            file?.seek(0)
        }
        file.use {
            val stringArray = (file as RandomAccessFile).readLine()
            val procStateInfo = parse(stringArray)
            return CpuDumper.CpuInfo(procStateInfo.getCpuTime(), procStateInfo.toString())
        }
    }

    private fun parse(stringArray: String?): ProcStateInfo {
        val split = stringArray?.split(" ")
        val user = split?.get(2)?.toLong() ?: 0
        val nice = split?.get(3)?.toLong() ?: 0
        val system = split?.get(4)?.toLong() ?: 0
        val idle = split?.get(5)?.toLong() ?: 0
        val iowait = split?.get(6)?.toLong() ?: 0
        val irq = split?.get(7)?.toLong() ?: 0
        val softirq = split?.get(8)?.toLong() ?: 0
        return ProcStateInfo(user, nice, system, idle, iowait, irq, softirq)
    }

    data class ProcStateInfo(
            val user: Long,
            val nice: Long,
            val system: Long,
            val idle: Long,
            val iowait: Long,
            val irq: Long,
            val softirq: Long
    ) {
        fun getCpuTime() = user + nice + system + idle + iowait + irq + softirq
    }
}