import java.util.List;

/**
 * Main class that demonstrates the OS simulation
 * Creates two threads: one for Round Robin scheduling and one for Priority scheduling
 */
public class OSSimulationMain {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Operating System Kernel Simulation");
        System.out.println("========================================");
        System.out.println();
        
        // Create Process Manager
        ProcessManager processManager = new ProcessManager();
        
        // Create default processes
        processManager.createDefaultProcesses();
        processManager.printAllProcesses();
        
        // Create copies of processes for each scheduler (to avoid conflicts)
        List<Process> processesForRoundRobin = processManager.createProcessCopies();
        List<Process> processesForPriority = processManager.createProcessCopies();
        
        // Create schedulers
        RoundRobinScheduler roundRobinScheduler = new RoundRobinScheduler(3); // Time quantum = 3
        PriorityScheduler priorityScheduler = new PriorityScheduler(3); // Time quantum = 3
        
        // Create threads for each scheduler
        SchedulingThread roundRobinThread = new SchedulingThread(
                "RoundRobin-Thread", 
                roundRobinScheduler, 
                processesForRoundRobin
        );
        
        SchedulingThread priorityThread = new SchedulingThread(
                "Priority-Thread", 
                priorityScheduler, 
                processesForPriority
        );
        
        System.out.println("Starting both scheduling threads simultaneously...");
        System.out.println("========================================\n");
        
        // Start both threads
        roundRobinThread.start();
        priorityThread.start();
        
        // Wait for both threads to complete
        try {
            roundRobinThread.join();
            priorityThread.join();
            
            System.out.println("\n========================================");
            System.out.println("   Both threads completed execution");
            System.out.println("========================================");
            
            // Print comparison
            printComparison(roundRobinScheduler, priorityScheduler);
            
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Print comparison between the two scheduling algorithms
     */
    private static void printComparison(Scheduler roundRobin, Scheduler priority) {
        System.out.println("\n========== Scheduling Algorithm Comparison ==========");
        System.out.println(String.format("Round Robin Total Time: %d", roundRobin.getCurrentTime()));
        System.out.println(String.format("Priority Total Time: %d", priority.getCurrentTime()));
        System.out.println("=====================================================\n");
    }
}

