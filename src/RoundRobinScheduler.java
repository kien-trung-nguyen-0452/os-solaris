import java.util.*;

/**
 * Round Robin Scheduler implementation
 * Each process receives a fixed time quantum to execute
 */
public class RoundRobinScheduler extends ProcessScheduler {
    private List<Process> allProcesses;
    private int processIndex;
    
    public RoundRobinScheduler(int timeQuantum) {
        super("Round Robin Scheduler", timeQuantum);
        this.allProcesses = new ArrayList<>();
        this.processIndex = 0;
    }
    
    /**
     * Initialize scheduler with a list of processes
     */
    public void initializeProcesses(List<Process> processes) {
        this.allProcesses = new ArrayList<>(processes);
        // Sort by arrival time
        Collections.sort(allProcesses, Comparator.comparingInt(Process::getArrivalTime));
    }
    
    @Override
    public Process selectNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }
        // Round Robin: select from front of queue (FIFO)
        return readyQueue.poll();
    }
    
    @Override
    protected boolean shouldPreempt() {
        // In Round Robin, preempt after time quantum expires
        return true;
    }
    
    @Override
    protected void checkNewArrivals() {
        // Add processes that have arrived at current time
        while (processIndex < allProcesses.size()) {
            Process p = allProcesses.get(processIndex);
            if (p.getArrivalTime() <= systemClock) {
                enqueueProcess(p);
                processIndex++;
            } else {
                break;
            }
        }
    }
    
    @Override
    protected boolean hasMoreProcesses() {
        return processIndex < allProcesses.size();
    }
    
    @Override
    public void schedule() {
        // Reset for scheduling
        systemClock = 0;
        currentProcess = null;
        completedProcesses.clear();
        readyQueue.clear();
        processIndex = 0;
        
        super.schedule();
    }
}

