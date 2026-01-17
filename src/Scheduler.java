import java.util.*;

/**
 * Abstract Scheduler class that defines the interface for different scheduling algorithms
 */
public abstract class Scheduler {
    protected String schedulerName;
    protected Queue<Process> readyQueue;
    protected Process currentProcess;
    protected int currentTime;
    protected List<Process> completedProcesses;
    protected int timeQuantum; // For Round Robin
    
    public Scheduler(String schedulerName, int timeQuantum) {
        this.schedulerName = schedulerName;
        this.readyQueue = new LinkedList<>();
        this.currentProcess = null;
        this.currentTime = 0;
        this.completedProcesses = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }
    
    /**
     * Add a process to the ready queue
     */
    public void addProcess(Process process) {
        if (process != null) {
            process.setState(Process.ProcessState.READY);
            readyQueue.offer(process);
            System.out.println(String.format("[%s] Time %d: Added Process %d to ready queue",
                    schedulerName, currentTime, process.getProcessId()));
        }
    }
    
    /**
     * Select the next process to run (to be implemented by subclasses)
     */
    public abstract Process selectNextProcess();
    
    /**
     * Schedule and execute processes
     */
    public void schedule() {
        System.out.println(String.format("\n========== %s Scheduling Started ==========", schedulerName));
        
        // Check for initial arrivals
        checkNewArrivals();
        
        // Continue until all processes are completed
        while (!readyQueue.isEmpty() || currentProcess != null || hasMoreProcesses()) {
            // Check for new arrivals
            checkNewArrivals();
            
            // Select next process if CPU is idle
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = selectNextProcess();
                if (currentProcess != null) {
                    Dispatcher.dispatch(currentProcess, currentTime);
                }
            }
            
            // Execute current process
            if (currentProcess != null) {
                executeCurrentProcess();
            } else {
                // If no process to run, advance time to next arrival
                if (hasMoreProcesses()) {
                    currentTime++;
                } else {
                    break;
                }
            }
        }
        
        printStatistics();
    }
    
    /**
     * Check if there are more processes to arrive (to be overridden by subclasses)
     */
    protected boolean hasMoreProcesses() {
        return false; // Default: no more processes
    }
    
    /**
     * Execute the current process
     */
    protected void executeCurrentProcess() {
        int executionTime = currentProcess.execute(timeQuantum);
        currentTime += executionTime;
        
        System.out.println(String.format("[%s] Time %d: Executing Process %d for %d time units",
                schedulerName, currentTime, currentProcess.getProcessId(), executionTime));
        
        if (currentProcess.isCompleted()) {
            Dispatcher.complete(currentProcess, currentTime);
            completedProcesses.add(currentProcess);
            currentProcess = null;
        } else {
            // Preempt if time quantum expired
            if (shouldPreempt()) {
                Dispatcher.preempt(currentProcess, currentTime);
                addProcess(currentProcess);
                currentProcess = null;
            }
        }
    }
    
    /**
     * Check if current process should be preempted
     */
    protected boolean shouldPreempt() {
        return true; // Default: preempt after time quantum
    }
    
    /**
     * Check for new process arrivals (to be implemented by subclasses)
     */
    protected abstract void checkNewArrivals();
    
    /**
     * Print scheduling statistics
     */
    protected void printStatistics() {
        System.out.println(String.format("\n========== %s Statistics ==========", schedulerName));
        System.out.println("Process ID | Process Name | Arrival Time | Burst Time | Completion Time | Turnaround Time | Waiting Time");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        double totalTurnaround = 0;
        double totalWaiting = 0;
        
        for (Process p : completedProcesses) {
            System.out.println(String.format("%10d | %12s | %12d | %10d | %15d | %15d | %12d",
                    p.getProcessId(), p.getProcessName(), p.getArrivalTime(),
                    p.getBurstTime(), p.getCompletionTime(), p.getTurnaroundTime(), p.getWaitingTime()));
            
            totalTurnaround += p.getTurnaroundTime();
            totalWaiting += p.getWaitingTime();
        }
        
        if (completedProcesses.size() > 0) {
            double avgTurnaround = totalTurnaround / completedProcesses.size();
            double avgWaiting = totalWaiting / completedProcesses.size();
            
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("Average Turnaround Time: %.2f", avgTurnaround));
            System.out.println(String.format("Average Waiting Time: %.2f", avgWaiting));
        }
        
        System.out.println(String.format("Total Execution Time: %d\n", currentTime));
    }
    
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public int getCurrentTime() {
        return currentTime;
    }
    
    public List<Process> getCompletedProcesses() {
        return new ArrayList<>(completedProcesses);
    }
}

