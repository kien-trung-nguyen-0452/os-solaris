/**
 * ProcessDispatcher handles context switching and process dispatching
 * Manages transitions from READY to RUNNING state
 */
public class ProcessDispatcher {
    private static int contextSwitchOverhead = 1; // Time overhead for context switching
    
    /**
     * Dispatch a process to the CPU
     * @param process The process to be dispatched
     * @param systemTime Current system time
     * @return Time taken for context switching
     */
    public static int dispatch(Process process, int systemTime) {
        if (process == null) {
            return 0;
        }
        
        // Transition from READY to RUNNING
        if (process.getState() == Process.ProcessState.READY) {
            process.setState(Process.ProcessState.RUNNING);
            
            // Set start time if not already set
            if (process.getStartTime() == -1) {
                process.setStartTime(systemTime);
            }
            
            System.out.println(String.format("  [CPU] Time %3d -> Process #%d (%s) dispatched to CPU core",
                    systemTime, process.getProcessId(), process.getProcessName()));
            
            return contextSwitchOverhead;
        }
        
        return 0;
    }
    
    /**
     * Preempt current process and save its context
     * @param process The process to be preempted
     * @param systemTime Current system time
     */
    public static void preempt(Process process, int systemTime) {
        if (process != null && process.getState() == Process.ProcessState.RUNNING) {
            process.setState(Process.ProcessState.READY);
            System.out.println(String.format("  [CPU] Time %3d -> Process #%d (%s) preempted, context saved",
                    systemTime, process.getProcessId(), process.getProcessName()));
        }
    }
    
    /**
     * Complete a process execution
     * @param process The process that completed
     * @param systemTime Current system time
     */
    public static void complete(Process process, int systemTime) {
        if (process != null) {
            process.setState(Process.ProcessState.TERMINATED);
            process.setCompletionTime(systemTime);
            process.setTurnaroundTime(systemTime - process.getArrivalTime());
            process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
            
            System.out.println(String.format("  [CPU] Time %3d -> Process #%d (%s) finished execution",
                    systemTime, process.getProcessId(), process.getProcessName()));
        }
    }
    
    public static int getContextSwitchOverhead() {
        return contextSwitchOverhead;
    }
}

