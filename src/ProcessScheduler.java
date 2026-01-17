import java.util.*;

/**
 * Abstract ProcessScheduler class that defines the interface for different process scheduling algorithms
 */
public abstract class ProcessScheduler {
    protected String schedulerName;
    protected Queue<Process> readyQueue;
    protected Process currentProcess;
    protected int systemClock;
    protected List<Process> completedProcesses;
    protected int timeQuantum; // For time-sliced scheduling
    
    public ProcessScheduler(String schedulerName, int timeQuantum) {
        this.schedulerName = schedulerName;
        this.readyQueue = new LinkedList<>();
        this.currentProcess = null;
        this.systemClock = 0;
        this.completedProcesses = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }
    
    /**
     * Add a process to the ready queue
     */
    public void enqueueProcess(Process process) {
        if (process != null) {
            process.setState(Process.ProcessState.READY);
            readyQueue.offer(process);
            // Silent enqueue - no output
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
        System.out.println(String.format("\n+----------------------------------------------------------------------+"));
        System.out.println(String.format("|  %-60s |", schedulerName + " - Execution Started"));
        System.out.println("+----------------------------------------------------------------------+");
        
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
                    ProcessDispatcher.dispatch(currentProcess, systemClock);
                }
            }
            
            // Execute current process
            if (currentProcess != null) {
                executeCurrentProcess();
            } else {
                // If no process to run, advance time to next arrival
                if (hasMoreProcesses()) {
                    systemClock++;
                } else {
                    break;
                }
            }
        }
        
        displayStatistics();
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
        systemClock += executionTime;
        
        System.out.println(String.format("  [%s] Clock %3d -> Executing Process #%d (%s) for %d time units",
                schedulerName, systemClock, currentProcess.getProcessId(), currentProcess.getProcessName(), executionTime));
        
        if (currentProcess.isCompleted()) {
            ProcessDispatcher.complete(currentProcess, systemClock);
            completedProcesses.add(currentProcess);
            currentProcess = null;
        } else {
            // Preempt if time quantum expired
            if (shouldPreempt()) {
                ProcessDispatcher.preempt(currentProcess, systemClock);
                enqueueProcess(currentProcess);
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
     * Display scheduling statistics
     */
    protected void displayStatistics() {
        System.out.println(String.format("\n+----------------------------------------------------------------------+"));
        System.out.println(String.format("|  %-60s |", schedulerName + " - Performance Summary"));
        System.out.println("+----------+--------------+----------+----------+--------------+--------------+-------------+");
        System.out.println("| Process  | Process Name | Arrival  |  Burst   |  Completion  |  Turnaround  |   Waiting   |");
        System.out.println("|    ID    |              |   Time   |   Time   |     Time     |     Time     |    Time     |");
        System.out.println("+----------+--------------+----------+----------+--------------+--------------+-------------+");
        
        double totalTurnaround = 0;
        double totalWaiting = 0;
        
        for (Process p : completedProcesses) {
            System.out.println(String.format("|    %2d    |  %-12s |    %2d    |    %2d    |      %2d      |      %2d      |      %2d      |",
                    p.getProcessId(), p.getProcessName(), p.getArrivalTime(),
                    p.getBurstTime(), p.getCompletionTime(), p.getTurnaroundTime(), p.getWaitingTime()));
            
            totalTurnaround += p.getTurnaroundTime();
            totalWaiting += p.getWaitingTime();
        }
        
        if (completedProcesses.size() > 0) {
            double avgTurnaround = totalTurnaround / completedProcesses.size();
            double avgWaiting = totalWaiting / completedProcesses.size();
            
            System.out.println("+----------+--------------+----------+----------+--------------+--------------+-------------+");
            System.out.println(String.format("|  Average Turnaround Time: %6.2f units                                    |", avgTurnaround));
            System.out.println(String.format("|  Average Waiting Time:     %6.2f units                                    |", avgWaiting));
        }
        
        System.out.println(String.format("|  Total Execution Time:      %3d units                                    |", systemClock));
        System.out.println("+----------------------------------------------------------------------+\n");
    }
    
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public int getSystemClock() {
        return systemClock;
    }
    
    public List<Process> getCompletedProcesses() {
        return new ArrayList<>(completedProcesses);
    }
}
