import java.util.List;

/**
 * Thread class for running process scheduling algorithms
 * This allows multiple schedulers to run concurrently
 */
public class SchedulerThread extends Thread {
    private ProcessScheduler scheduler;
    private List<Process> processes;
    private String threadLabel;
    
    public SchedulerThread(String threadLabel, ProcessScheduler scheduler, List<Process> processes) {
        super(threadLabel);
        this.threadLabel = threadLabel;
        this.scheduler = scheduler;
        this.processes = processes;
    }
    
    @Override
    public void run() {
        System.out.println(String.format("\n>>> [%s] Initialized at %s", 
                threadLabel, java.time.LocalTime.now()));
        
        try {
            // Initialize scheduler with processes
            if (scheduler instanceof RoundRobinScheduler) {
                ((RoundRobinScheduler) scheduler).initializeProcesses(processes);
            } else if (scheduler instanceof PriorityScheduler) {
                ((PriorityScheduler) scheduler).initializeProcesses(processes);
            }
            
            // Run the scheduler
            scheduler.schedule();
            
            System.out.println(String.format(">>> [%s] Completed at %s", 
                    threadLabel, java.time.LocalTime.now()));
            
        } catch (Exception e) {
            System.err.println(String.format("[%s] Error occurred: %s", threadLabel, e.getMessage()));
            e.printStackTrace();
        }
    }
    
    public ProcessScheduler getScheduler() {
        return scheduler;
    }
}

