/**
 * Dispatcher class handles the context switching and dispatching of processes
 * It transitions processes from READY to RUNNING state
 */
public class Dispatcher {
    private static int contextSwitchTime = 1; // Time overhead for context switching
    
    /**
     * Dispatch a process to the CPU
     * @param process The process to be dispatched
     * @param currentTime Current system time
     * @return Time taken for context switching
     */
    public static int dispatch(Process process, int currentTime) {
        if (process == null) {
            return 0;
        }
        
        // Transition from READY to RUNNING
        if (process.getState() == Process.ProcessState.READY) {
            process.setState(Process.ProcessState.RUNNING);
            
            // Set start time if not already set
            if (process.getStartTime() == -1) {
                process.setStartTime(currentTime);
            }
            
            System.out.println(String.format("[Dispatcher] Time %d: Dispatching Process %d (%s) to CPU",
                    currentTime, process.getProcessId(), process.getProcessName()));
            
            return contextSwitchTime;
        }
        
        return 0;
    }
    
    /**
     * Preempt current process and save its context
     * @param process The process to be preempted
     * @param currentTime Current system time
     */
    public static void preempt(Process process, int currentTime) {
        if (process != null && process.getState() == Process.ProcessState.RUNNING) {
            process.setState(Process.ProcessState.READY);
            System.out.println(String.format("[Dispatcher] Time %d: Preempting Process %d (%s) - Context saved",
                    currentTime, process.getProcessId(), process.getProcessName()));
        }
    }
    
    /**
     * Complete a process execution
     * @param process The process that completed
     * @param currentTime Current system time
     */
    public static void complete(Process process, int currentTime) {
        if (process != null) {
            process.setState(Process.ProcessState.TERMINATED);
            process.setCompletionTime(currentTime);
            process.setTurnaroundTime(currentTime - process.getArrivalTime());
            process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
            
            System.out.println(String.format("[Dispatcher] Time %d: Process %d (%s) completed execution",
                    currentTime, process.getProcessId(), process.getProcessName()));
        }
    }
    
    public static int getContextSwitchTime() {
        return contextSwitchTime;
    }
}

