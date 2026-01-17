import java.util.List;

/**
 * Main class that demonstrates the process scheduling simulation
 * Creates two threads: one for Round Robin scheduling and one for Priority scheduling
 */
public class ProcessSchedulingDemo {
    
    public static void main(String[] args) {
        System.out.println("\n+======================================================================+");
        System.out.println("|         PROCESS SCHEDULING SYSTEM - SIMULATION DEMO          |");
        System.out.println("+======================================================================+\n");
        
        // Create Process Registry
        ProcessRegistry processRegistry = new ProcessRegistry();
        
        // Create default processes
        processRegistry.createDefaultProcesses();
        processRegistry.printAllProcesses();
        
        // Create copies of processes for each scheduler (to avoid conflicts)
        List<Process> processesForRoundRobin = processRegistry.duplicateProcesses();
        List<Process> processesForPriority = processRegistry.duplicateProcesses();
        
        // Create schedulers
        RoundRobinScheduler roundRobinScheduler = new RoundRobinScheduler(3); // Time quantum = 3
        PriorityScheduler priorityScheduler = new PriorityScheduler(3); // Time quantum = 3
        
        // Create threads for each scheduler
        SchedulerThread roundRobinThread = new SchedulerThread(
                "RoundRobin-Scheduler-Thread", 
                roundRobinScheduler, 
                processesForRoundRobin
        );
        
        SchedulerThread priorityThread = new SchedulerThread(
                "Priority-Scheduler-Thread", 
                priorityScheduler, 
                processesForPriority
        );
        
        System.out.println(">>> Initializing concurrent scheduling threads...");
        System.out.println(">>> Launching Round Robin and Priority schedulers in parallel mode\n");
        
        // Start both threads
        roundRobinThread.start();
        priorityThread.start();
        
        // Wait for both threads to complete
        try {
            roundRobinThread.join();
            priorityThread.join();
            
            System.out.println("\n+======================================================================+");
            System.out.println("|          ALL SCHEDULING THREADS COMPLETED SUCCESSFULLY          |");
            System.out.println("+======================================================================+");
            
            // Print comparison
            displayComparison(roundRobinScheduler, priorityScheduler);
            
        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Display comparison between the two scheduling algorithms
     */
    private static void displayComparison(ProcessScheduler roundRobin, ProcessScheduler priority) {
        System.out.println("\n+----------------------------------------------------------------------+");
        System.out.println("|           SCHEDULING ALGORITHM PERFORMANCE COMPARISON           |");
        System.out.println("+----------------------------------------------------------------------+");
        System.out.println(String.format("|  Round Robin Scheduler  |  Total Execution Time: %3d units  |", roundRobin.getSystemClock()));
        System.out.println(String.format("|  Priority Scheduler    |  Total Execution Time: %3d units  |", priority.getSystemClock()));
        System.out.println("+----------------------------------------------------------------------+\n");
    }
}
