import java.util.*;

/**
 * ProcessRegistry class manages the lifecycle of processes
 * Handles process creation, state management, and process collections
 */
public class ProcessRegistry {
    private List<Process> processes;
    private Map<Integer, Process> processMap;
    private int nextProcessId;
    
    public ProcessRegistry() {
        this.processes = new ArrayList<>();
        this.processMap = new HashMap<>();
        this.nextProcessId = 1;
    }
    
    /**
     * Create a new process
     */
    public Process createProcess(String processName, int priorityLevel, int burstTime, int arrivalTime) {
        Process process = new Process(nextProcessId++, processName, priorityLevel, burstTime, arrivalTime);
        processes.add(process);
        processMap.put(process.getProcessId(), process);
        // Silent creation - no output during creation
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
     * Create a copy of processes for planning (to avoid modifying original)
     */
    public List<Process> duplicateProcesses() {
        List<Process> duplicates = new ArrayList<>();
        for (Process p : processes) {
            Process duplicate = new Process(p.getProcessId(), p.getProcessName(), 
                    p.getPriorityLevel(), p.getBurstTime(), p.getArrivalTime());
            duplicates.add(duplicate);
        }
        return duplicates;
    }
    
    /**
     * Create default set of processes for demonstration
     */
    public void createDefaultProcesses() {
        processes.clear();
        processMap.clear();
        nextProcessId = 1;
        
        // Create sample processes with different priorities and execution times
        createProcess("CalcApp", 2, 7, 0);
        createProcess("Editor", 1, 4, 1);
        createProcess("Browser", 4, 10, 2);
        createProcess("MediaPlayer", 3, 6, 3);
        createProcess("BackupTool", 5, 5, 4);
    }
    
    /**
     * Print all processes
     */
    public void printAllProcesses() {
        System.out.println("\n+======================================================================+");
        System.out.println("|              PROCESS REGISTRY - ACTIVE PROCESSES              |");
        System.out.println("+======================================================================+");
        System.out.println("|  ID  |  Process Name  | Priority | Burst | Arrival Time    |");
        System.out.println("+------+----------------+----------+-------+-----------------+");
        for (Process p : processes) {
            System.out.println(String.format("|  %2d  |  %-13s |    %2d     |  %3d  |      %2d          |",
                    p.getProcessId(), p.getProcessName(), p.getPriorityLevel(),
                    p.getBurstTime(), p.getArrivalTime()));
        }
        System.out.println("+======================================================================+\n");
    }
}
