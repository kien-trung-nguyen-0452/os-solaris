import java.util.List;

/**
 * BenchmarkMetrics class calculates and stores performance metrics
 * for scheduling algorithms
 */
public class BenchmarkMetrics {
    private String schedulerName;
    private int totalProcesses;
    private int totalExecutionTime;
    private double averageTurnaroundTime;
    private double averageWaitingTime;
    private double throughput;
    private double cpuUtilization;
    private int contextSwitchCount;
    private double averageResponseTime;
    
    public BenchmarkMetrics(String schedulerName, List<Process> completedProcesses, 
                           int totalTime, int contextSwitches) {
        this.schedulerName = schedulerName;
        this.totalProcesses = completedProcesses.size();
        this.totalExecutionTime = totalTime;
        this.contextSwitchCount = contextSwitches;
        
        calculateMetrics(completedProcesses);
    }
    
    private void calculateMetrics(List<Process> completedProcesses) {
        if (completedProcesses.isEmpty()) {
            averageTurnaroundTime = 0;
            averageWaitingTime = 0;
            throughput = 0;
            cpuUtilization = 0;
            averageResponseTime = 0;
            return;
        }
        
        double totalTurnaround = 0;
        double totalWaiting = 0;
        double totalResponse = 0;
        int totalBurstTime = 0;
        
        for (Process p : completedProcesses) {
            totalTurnaround += p.getTurnaroundTime();
            totalWaiting += p.getWaitingTime();
            totalResponse += p.getStartTime() - p.getArrivalTime(); // Response time
            totalBurstTime += p.getBurstTime();
        }
        
        averageTurnaroundTime = totalTurnaround / completedProcesses.size();
        averageWaitingTime = totalWaiting / completedProcesses.size();
        averageResponseTime = totalResponse / completedProcesses.size();
        
        // Throughput = number of processes completed per time unit
        throughput = totalExecutionTime > 0 ? (double) totalProcesses / totalExecutionTime : 0;
        
        // CPU Utilization = (total burst time / total execution time) * 100
        cpuUtilization = totalExecutionTime > 0 ? 
            ((double) totalBurstTime / totalExecutionTime) * 100 : 0;
    }
    
    // Getters
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public int getTotalProcesses() {
        return totalProcesses;
    }
    
    public int getTotalExecutionTime() {
        return totalExecutionTime;
    }
    
    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }
    
    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }
    
    public double getThroughput() {
        return throughput;
    }
    
    public double getCpuUtilization() {
        return cpuUtilization;
    }
    
    public int getContextSwitchCount() {
        return contextSwitchCount;
    }
    
    public double getAverageResponseTime() {
        return averageResponseTime;
    }
    
    /**
     * Print formatted metrics
     */
    public void printMetrics() {
        System.out.println("\n+====================================================================+");
        System.out.println(String.format("|  PERFORMANCE ANALYSIS: %-42s |", schedulerName));
        System.out.println("+====================================================================+");
        System.out.printf("|  Total Processes Completed:        %3d processes              |\n", totalProcesses);
        System.out.printf("|  Total Execution Time:            %3d time units            |\n", totalExecutionTime);
        System.out.printf("|  Context Switch Operations:       %3d switches              |\n", contextSwitchCount);
        System.out.printf("|  Average Turnaround Time:          %6.2f units              |\n", averageTurnaroundTime);
        System.out.printf("|  Average Waiting Time:             %6.2f units              |\n", averageWaitingTime);
        System.out.printf("|  Average Response Time:            %6.2f units              |\n", averageResponseTime);
        System.out.printf("|  Throughput Rate:                  %6.4f proc/time unit     |\n", throughput);
        System.out.printf("|  CPU Utilization Percentage:       %6.2f%%                 |\n", cpuUtilization);
        System.out.println("+====================================================================+");
    }
    
    /**
     * Get metrics as formatted string
     */
    public String getMetricsString() {
        return String.format(
            "Scheduler: %s\n" +
            "Total Processes: %d\n" +
            "Total Execution Time: %d\n" +
            "Context Switches: %d\n" +
            "Average Turnaround Time: %.2f\n" +
            "Average Waiting Time: %.2f\n" +
            "Average Response Time: %.2f\n" +
            "Throughput: %.4f\n" +
            "CPU Utilization: %.2f%%\n",
            schedulerName, totalProcesses, totalExecutionTime, contextSwitchCount,
            averageTurnaroundTime, averageWaitingTime, averageResponseTime,
            throughput, cpuUtilization
        );
    }
}
