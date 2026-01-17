import java.util.*;

/**
 * ProcessManager class manages the lifecycle of processes
 * Handles process creation, state transitions, and process queues
 */
public class ProcessManager {
    private List<Process> processes;
    private Map<Integer, Process> processMap;
    private int nextProcessId;
    
    public ProcessManager() {
        this.processes = new ArrayList<>();
        this.processMap = new HashMap<>();
        this.nextProcessId = 1;
    }
    
    /**
     * Create a new process
     */
    public Process createProcess(String processName, int priority, int burstTime, int arrivalTime) {
        Process process = new Process(nextProcessId++, processName, priority, burstTime, arrivalTime);
        processes.add(process);
        processMap.put(process.getProcessId(), process);
        System.out.println(String.format("[ProcessManager] Created Process: %s", process));
        return process;
    }
    
    /**
     * Get process by ID
     */
    public Process getProcess(int processId) {
        return processMap.get(processId);
    }
    
    /**
     * Get all processes
     */
    public List<Process> getAllProcesses() {
        return new ArrayList<>(processes);
    }
    
    /**
     * Create a copy of processes for scheduling (to avoid modifying original)
     */
    public List<Process> createProcessCopies() {
        List<Process> copies = new ArrayList<>();
        for (Process p : processes) {
            Process copy = new Process(p.getProcessId(), p.getProcessName(), 
                    p.getPriority(), p.getBurstTime(), p.getArrivalTime());
            copies.add(copy);
        }
        return copies;
    }
    
    /**
     * Create default set of processes for demonstration
     */
    public void createDefaultProcesses() {
        processes.clear();
        processMap.clear();
        nextProcessId = 1;
        
        // Create sample processes with different priorities and burst times
        createProcess("P1", 3, 5, 0);
        createProcess("P2", 1, 3, 1);
        createProcess("P3", 4, 8, 2);
        createProcess("P4", 2, 6, 3);
        createProcess("P5", 5, 4, 4);
    }
    
    /**
     * Print all processes
     */
    public void printAllProcesses() {
        System.out.println("\n========== All Processes ==========");
        System.out.println("Process ID | Process Name | Priority | Burst Time | Arrival Time");
        System.out.println("-------------------------------------------------------------------");
        for (Process p : processes) {
            System.out.println(String.format("%10d | %12s | %8d | %10d | %12d",
                    p.getProcessId(), p.getProcessName(), p.getPriority(),
                    p.getBurstTime(), p.getArrivalTime()));
        }
        System.out.println();
    }
}



