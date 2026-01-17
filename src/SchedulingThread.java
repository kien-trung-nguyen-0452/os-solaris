import java.util.List;

/**
 * Thread class for running scheduling algorithms
 * This allows multiple schedulers to run concurrently
 */
public class SchedulingThread extends Thread {
    private Scheduler scheduler;
    private List<Process> processes;
    private String threadName;
    
    public SchedulingThread(String threadName, Scheduler scheduler, List<Process> processes) {
        super(threadName);
        this.threadName = threadName;
        this.scheduler = scheduler;
        this.processes = processes;
    }
    
    @Override
    public void run() {
        System.out.println(String.format("\n[%s] Thread started at: %s", 
                threadName, java.time.LocalTime.now()));
        
        try {
            // Initialize scheduler with processes
            if (scheduler instanceof RoundRobinScheduler) {
                ((RoundRobinScheduler) scheduler).initializeProcesses(processes);
            } else if (scheduler instanceof PriorityScheduler) {
                ((PriorityScheduler) scheduler).initializeProcesses(processes);
            }
            
            // Run the scheduler
            scheduler.schedule();
            
            System.out.println(String.format("[%s] Thread completed at: %s", 
                    threadName, java.time.LocalTime.now()));
            
        } catch (Exception e) {
            System.err.println(String.format("[%s] Error occurred: %s", threadName, e.getMessage()));
            e.printStackTrace();
        }
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
}

