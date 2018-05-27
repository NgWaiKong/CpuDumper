#CpuDumper
CpuDumper is a tool for third-party applications to analyze CPU usage

##Feature
- Get the CPU Usage of the  process, thread
- Get the Thread Stack of Java and Native (Native requires Root permission)

##Usage
1. CpuDump 


        //pid for file Proc/<pid>/stat and so on
        CpuDumper cpuDumper = new CpuDumper(String.valueOf(android.os.Process.myPid()));


2. DumpToString


        //Return the logs collected by sampler
        String log = cpuDumper.dumpToString();



3. OtherDumpAPI


        //Return the logs collected by sampler
        cpuDumper.dumpSystemRate(); //proc/stat
        cpuDumper.dumpAppRate();//proc/[pid]>/stat
        cpuDumper.dumpAppThreadCpu();//proc/pid/task/stat
        cpuDumper.dumpTop(5000, stream); //adb shell top cmd
        cpuDumper.dumpJavaStack();//kill -3 pid(need root)  and java api
        cpuDumper.dumpNativeStack();//debuggerd pid cmd(need root)


