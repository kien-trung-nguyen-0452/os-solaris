import java.util.*;
import java.io.*;

/**
 * InteractiveSimulator provides a command-line interface for the process scheduling simulation
 * with benchmarking capabilities
 */
public class InteractiveSimulator {
    private Scanner scanner;
    private ProcessRegistry processRegistry;
    
    public InteractiveSimulator() {
        this.scanner = new Scanner(System.in);
        this.processRegistry = new ProcessRegistry();
    }
    
    /**
     * Main interactive menu
     */
    public void run() {
        System.out.println("\n+======================================================================+");
        System.out.println("|     PROCESS SCHEDULING SYSTEM - INTERACTIVE MODE             |");
        System.out.println("+======================================================================+\n");
        
        while (true) {
            printMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    inputProcessesManually();
                    break;
                case 2:
                    useDefaultProcesses();
                    break;
                case 3:
                    runBenchmark();
                    break;
                case 4:
                    runSingleScheduler();
                    break;
                case 5:
                    compareSchedulers();
                    break;
                case 6:
                    exportResults();
                    break;
                case 7:
                    viewCurrentProcesses();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private void printMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Input Processes Manually");
        System.out.println("2. Use Default Processes");
        System.out.println("3. Run Benchmark (Both Schedulers)");
        System.out.println("4. Run Single Scheduler");
        System.out.println("5. Compare Schedulers");
        System.out.println("6. Export Results");
        System.out.println("7. View Current Processes");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
    }
    
    private void inputProcessesManually() {
        System.out.println("\n--- Input Processes Manually ---");
        processRegistry = new ProcessRegistry();
        
        int numProcesses = getIntInput("Number of processes: ");
        
        for (int i = 0; i < numProcesses; i++) {
            System.out.println("\nProcess " + (i + 1) + ":");
            String name = getStringInput("Process name: ");
            int priority = getIntInput("Priority (1=highest, 10=lowest): ");
            int burstTime = getIntInput("Burst time: ");
            int arrivalTime = getIntInput("Arrival time: ");
            
            processRegistry.createProcess(name, priority, burstTime, arrivalTime);
        }
        
        System.out.println("\n[*] Processes created successfully!");
        processRegistry.printAllProcesses();
    }
    
    private void useDefaultProcesses() {
        System.out.println("\n>>> Loading default process set...");
        processRegistry = new ProcessRegistry();
        processRegistry.createDefaultProcesses();
        System.out.println(">>> Default processes loaded successfully!");
        processRegistry.printAllProcesses();
    }
    
    private void runBenchmark() {
        if (processRegistry.getAllProcesses().isEmpty()) {
            System.out.println("No processes! Using default processes.");
            useDefaultProcesses();
        }
        
        System.out.println("\n>>> Starting benchmark analysis...");
        int timeQuantum = getIntInput("Time quantum (default 3): ", 3);
        
        // Create process copies
        List<Process> processesRR = processRegistry.duplicateProcesses();
        List<Process> processesPriority = processRegistry.duplicateProcesses();
        
        // Create schedulers
        RoundRobinScheduler rrScheduler = new RoundRobinScheduler(timeQuantum);
        PriorityScheduler priorityScheduler = new PriorityScheduler(timeQuantum);
        
        // Initialize
        rrScheduler.initializeProcesses(processesRR);
        priorityScheduler.initializeProcesses(processesPriority);
        
        // Run schedulers
        System.out.println("\n>>> Executing Round Robin Scheduler...");
        long startTime = System.currentTimeMillis();
        rrScheduler.schedule();
        long rrTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n>>> Executing Priority Scheduler...");
        startTime = System.currentTimeMillis();
        priorityScheduler.schedule();
        long priorityTime = System.currentTimeMillis() - startTime;
        
        // Calculate metrics
        int rrContextSwitches = countContextSwitches(rrScheduler);
        int priorityContextSwitches = countContextSwitches(priorityScheduler);
        
        BenchmarkMetrics rrMetrics = new BenchmarkMetrics(
            "Round Robin Scheduler", 
            rrScheduler.getCompletedProcesses(),
            rrScheduler.getSystemClock(),
            rrContextSwitches
        );
        
        BenchmarkMetrics priorityMetrics = new BenchmarkMetrics(
            "Priority Scheduler",
            priorityScheduler.getCompletedProcesses(),
            priorityScheduler.getSystemClock(),
            priorityContextSwitches
        );
        
        // Print metrics
        rrMetrics.printMetrics();
        priorityMetrics.printMetrics();
        
        // Compare
        BenchmarkComparator.compare(rrMetrics, priorityMetrics);
        
        // Store for export
        lastRRMetrics = rrMetrics;
        lastPriorityMetrics = priorityMetrics;
    }
    
    private void runSingleScheduler() {
        if (processRegistry.getAllProcesses().isEmpty()) {
            System.out.println("No processes! Using default processes.");
            useDefaultProcesses();
        }
        
        System.out.println("\n--- Run Single Scheduler ---");
        System.out.println("1. Round Robin");
        System.out.println("2. Priority");
        int choice = getIntInput("Choose scheduler: ");
        
        int timeQuantum = getIntInput("Time quantum (default 3): ", 3);
        List<Process> processes = processRegistry.duplicateProcesses();
        
        ProcessScheduler scheduler;
        if (choice == 1) {
            scheduler = new RoundRobinScheduler(timeQuantum);
            ((RoundRobinScheduler) scheduler).initializeProcesses(processes);
        } else {
            scheduler = new PriorityScheduler(timeQuantum);
            ((PriorityScheduler) scheduler).initializeProcesses(processes);
        }
        
        scheduler.schedule();
    }
    
    private void compareSchedulers() {
        if (lastRRMetrics == null || lastPriorityMetrics == null) {
            System.out.println("Please run benchmark first!");
            return;
        }
        
        BenchmarkComparator.compare(lastRRMetrics, lastPriorityMetrics);
    }
    
    private void exportResults() {
        if (lastRRMetrics == null || lastPriorityMetrics == null) {
            System.out.println("Please run benchmark first!");
            return;
        }
        
        System.out.println("\n--- Export Results ---");
        String filename = getStringInput("Filename (without extension): ");
        
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename + ".txt"));
            writer.println("PROCESS SCHEDULING SYSTEM BENCHMARK RESULTS");
            writer.println("=".repeat(70));
            writer.println();
            writer.println(lastRRMetrics.getMetricsString());
            writer.println();
            writer.println(lastPriorityMetrics.getMetricsString());
            writer.println();
            writer.println(BenchmarkComparator.generateComparisonReport(lastRRMetrics, lastPriorityMetrics));
            writer.close();
            
            System.out.println("[*] Results exported to " + filename + ".txt");
        } catch (IOException e) {
            System.out.println("Error exporting results: " + e.getMessage());
        }
    }
    
    private void viewCurrentProcesses() {
        if (processRegistry.getAllProcesses().isEmpty()) {
            System.out.println("No processes!");
        } else {
            processRegistry.printAllProcesses();
        }
    }
    
    // Helper methods
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return getIntInput(prompt);
        }
    }
    
    private int getIntInput(String prompt, int defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int countContextSwitches(ProcessScheduler scheduler) {
        // Estimate context switches (each preemption + dispatch = 2 switches)
        // This is a simplified count
        return scheduler.getCompletedProcesses().size() * 2;
    }
    
    // Store last metrics for comparison and export
    private BenchmarkMetrics lastRRMetrics;
    private BenchmarkMetrics lastPriorityMetrics;
}
