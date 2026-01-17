import java.util.*;

/**
 * Priority Scheduler implementation
 * Processes with higher priority (lower number) are scheduled first
 */
public class PriorityScheduler extends Scheduler {
    private List<Process> allProcesses;
    private int processIndex;
    
    public PriorityScheduler(int timeQuantum) {
        super("Priority Scheduler", timeQuantum);
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
        
        // Priority scheduling: select process with highest priority (lowest priority number)
        Process highestPriority = null;
        Iterator<Process> iterator = readyQueue.iterator();
        
        while (iterator.hasNext()) {
            Process p = iterator.next();
            if (highestPriority == null || p.getPriority() < highestPriority.getPriority()) {
                highestPriority = p;
            }
        }
        
        // Remove selected process from queue
        readyQueue.remove(highestPriority);
        return highestPriority;
    }
    
    @Override
    protected boolean shouldPreempt() {
        // Check if a higher priority process has arrived
        if (!readyQueue.isEmpty()) {
            Process highestInQueue = readyQueue.stream()
                    .min(Comparator.comparingInt(Process::getPriority))
                    .orElse(null);
            
            if (highestInQueue != null && highestInQueue.getPriority() < currentProcess.getPriority()) {
                System.out.println(String.format("[%s] Time %d: Higher priority process %d detected, preempting current process",
                        schedulerName, currentTime, highestInQueue.getProcessId()));
                return true;
            }
        }
        
        // Otherwise, preempt after time quantum
        return true;
    }
    
    @Override
    protected void checkNewArrivals() {
        // Add processes that have arrived at current time
        while (processIndex < allProcesses.size()) {
            Process p = allProcesses.get(processIndex);
            if (p.getArrivalTime() <= currentTime) {
                addProcess(p);
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
        currentTime = 0;
        currentProcess = null;
        completedProcesses.clear();
        readyQueue.clear();
        processIndex = 0;
        
        super.schedule();
    }
}

