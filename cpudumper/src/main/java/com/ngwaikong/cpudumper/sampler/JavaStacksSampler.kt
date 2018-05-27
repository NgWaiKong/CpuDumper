package com.ngwaikong.cpudumper.sampler

import android.os.Looper
import java.util.*


/**
 * Created by ngwaikong on 2018/5/27.
 */
class JavaStacksSampler() {
    private val mComparator = Comparator<Thread> { lhs, rhs ->
        if (lhs === rhs)
            return@Comparator 0
        if (lhs === Looper.getMainLooper().thread)
            return@Comparator -1
        if (rhs === Looper.getMainLooper().thread) 1 else lhs.name.compareTo(rhs.name)
    }

    fun dump(): TreeMap<Thread, Array<StackTraceElement>> {
        val stackTraces = TreeMap<Thread, Array<StackTraceElement>>(mComparator)

        val allThread = Thread.getAllStackTraces().entries
        for ((key, value) in allThread) {
            stackTraces.put(key, value)
        }
        //二次确认保护mainThread
        collectMainLooper(stackTraces)
        return stackTraces
    }

    fun dumpToString(): String {
        val threadTracesMap = dump()
        val sb = StringBuilder()
        for (thread in threadTracesMap.keys) {
            sb.append("thread_name : ").append(thread.name).append("\n")
                    .append("stack_trace:").append(callStack(threadTracesMap[thread]))
                    .append("\n")
        }
        return sb.toString()
    }

    private fun collectMainLooper(stackTraces: TreeMap<Thread, Array<StackTraceElement>>) {
        //二次确认保护mainThread
        val mainThread = Looper.getMainLooper().thread
        if (!stackTraces.containsKey(mainThread)) {
            stackTraces.put(mainThread, mainThread.stackTrace)
        }

    }

    private fun callStack(ste: Array<StackTraceElement>?): String {
        if (ste == null) {
            return ""
        }
        return try {
            var log = "\n"
            for (s in ste) {
                log += s.toString() + "\n"
            }
            log
        } catch (e: Exception) {
            "<callStackException>"
        }
    }
}