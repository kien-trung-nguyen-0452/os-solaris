/**
 * Process class represents a process in the operating system
 * with different states: NEW, READY, RUNNING, WAITING, TERMINATED
 */
public class Process {
    public enum ProcessState {
        NEW,        // Process is being created
        READY,      // Process is ready to run
        RUNNING,    // Process is currently executing
        WAITING,    // Process is waiting for I/O or event
        TERMINATED  // Process has finished execution
    }
    
    private int processId;
    private String processName;
    private ProcessState state;
    private int priority;           // Priority level (1 = highest, 10 = lowest)
    private int burstTime;          // CPU burst time required
    private int remainingTime;      // Remaining CPU time
    private int arrivalTime;        // Time when process arrives
    private int startTime;          // Time when process starts execution
    private int completionTime;     // Time when process completes
    private int waitingTime;        // Total waiting time
    private int turnaroundTime;     // Total turnaround time
    
    public Process(int processId, String processName, int priority, int burstTime, int arrivalTime) {
        this.processId = processId;
        this.processName = processName;
        this.priority = priority;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.state = ProcessState.NEW;
        this.startTime = -1;
        this.completionTime = -1;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }
    
    // Getters and Setters
    public int getProcessId() {
        return processId;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    public ProcessState getState() {
        return state;
    }
    
    public void setState(ProcessState state) {
        this.state = state;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public int getBurstTime() {
        return burstTime;
    }
    
    public int getRemainingTime() {
        return remainingTime;
    }
    
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    
    public int getArrivalTime() {
        return arrivalTime;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    
    public int getCompletionTime() {
        return completionTime;
    }
    
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }
    
    public int getWaitingTime() {
        return waitingTime;
    }
    
    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    public int getTurnaroundTime() {
        return turnaroundTime;
    }
    
    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }
    
    /**
     * Execute the process for a given time quantum
     * @param timeQuantum Time slice allocated to this process
     * @return Actual time executed
     */
    public int execute(int timeQuantum) {
        int executedTime = Math.min(timeQuantum, remainingTime);
        remainingTime -= executedTime;
        
        if (remainingTime <= 0) {
            state = ProcessState.TERMINATED;
        }
        
        return executedTime;
    }
    
    /**
     * Check if process is completed
     */
    public boolean isCompleted() {
        return state == ProcessState.TERMINATED;
    }
    
    @Override
    public String toString() {
        return String.format("Process[ID=%d, Name=%s, State=%s, Priority=%d, BurstTime=%d, RemainingTime=%d]",
                processId, processName, state, priority, burstTime, remainingTime);
    }
}

