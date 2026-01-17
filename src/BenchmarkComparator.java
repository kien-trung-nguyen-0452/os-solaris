import java.util.List;

/**
 * BenchmarkComparator compares performance metrics between different schedulers
 */
public class BenchmarkComparator {
    
    /**
     * Compare two benchmark metrics and print detailed comparison
     */
    public static void compare(BenchmarkMetrics metrics1, BenchmarkMetrics metrics2) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("   DETAILED BENCHMARK COMPARISON");
        System.out.println("=".repeat(70));
        
        System.out.printf("%-30s | %-20s | %-20s | %-10s\n", 
            "Metric", metrics1.getSchedulerName(), metrics2.getSchedulerName(), "Winner");
        System.out.println("-".repeat(70));
        
        // Average Turnaround Time
        compareMetric("Avg Turnaround Time", 
            metrics1.getAverageTurnaroundTime(), 
            metrics2.getAverageTurnaroundTime(), 
            true); // Lower is better
        
        // Average Waiting Time
        compareMetric("Avg Waiting Time", 
            metrics1.getAverageWaitingTime(), 
            metrics2.getAverageWaitingTime(), 
            true); // Lower is better
        
        // Average Response Time
        compareMetric("Avg Response Time", 
            metrics1.getAverageResponseTime(), 
            metrics2.getAverageResponseTime(), 
            true); // Lower is better
        
        // Throughput
        compareMetric("Throughput", 
            metrics1.getThroughput(), 
            metrics2.getThroughput(), 
            false); // Higher is better
        
        // CPU Utilization
        compareMetric("CPU Utilization", 
            metrics1.getCpuUtilization(), 
            metrics2.getCpuUtilization(), 
            false); // Higher is better
        
        // Context Switches
        compareMetric("Context Switches", 
            (double) metrics1.getContextSwitchCount(), 
            (double) metrics2.getContextSwitchCount(), 
            true); // Lower is better
        
        System.out.println("=".repeat(70));
        
        // Overall recommendation
        printRecommendation(metrics1, metrics2);
    }
    
    private static void compareMetric(String metricName, double value1, double value2, boolean lowerIsBetter) {
        String winner;
        if (lowerIsBetter) {
            winner = value1 < value2 ? "Scheduler 1" : (value1 > value2 ? "Scheduler 2" : "Tie");
        } else {
            winner = value1 > value2 ? "Scheduler 1" : (value1 < value2 ? "Scheduler 2" : "Tie");
        }
        
        System.out.printf("%-30s | %-20.2f | %-20.2f | %-10s\n", 
            metricName, value1, value2, winner);
    }
    
    private static void printRecommendation(BenchmarkMetrics m1, BenchmarkMetrics m2) {
        int score1 = 0;
        int score2 = 0;
        
        // Compare each metric
        if (m1.getAverageTurnaroundTime() < m2.getAverageTurnaroundTime()) score1++;
        else if (m1.getAverageTurnaroundTime() > m2.getAverageTurnaroundTime()) score2++;
        
        if (m1.getAverageWaitingTime() < m2.getAverageWaitingTime()) score1++;
        else if (m1.getAverageWaitingTime() > m2.getAverageWaitingTime()) score2++;
        
        if (m1.getAverageResponseTime() < m2.getAverageResponseTime()) score1++;
        else if (m1.getAverageResponseTime() > m2.getAverageResponseTime()) score2++;
        
        if (m1.getThroughput() > m2.getThroughput()) score1++;
        else if (m1.getThroughput() < m2.getThroughput()) score2++;
        
        if (m1.getCpuUtilization() > m2.getCpuUtilization()) score1++;
        else if (m1.getCpuUtilization() < m2.getCpuUtilization()) score2++;
        
        if (m1.getContextSwitchCount() < m2.getContextSwitchCount()) score1++;
        else if (m1.getContextSwitchCount() > m2.getContextSwitchCount()) score2++;
        
        System.out.println("\nOverall Score:");
        System.out.printf("  %s: %d points\n", m1.getSchedulerName(), score1);
        System.out.printf("  %s: %d points\n", m2.getSchedulerName(), score2);
        
        if (score1 > score2) {
            System.out.printf("\n✓ Recommendation: %s performs better for this workload\n", 
                m1.getSchedulerName());
        } else if (score2 > score1) {
            System.out.printf("\n✓ Recommendation: %s performs better for this workload\n", 
                m2.getSchedulerName());
        } else {
            System.out.println("\n✓ Both schedulers perform similarly for this workload");
        }
    }
    
    /**
     * Generate comparison report as string
     */
    public static String generateComparisonReport(BenchmarkMetrics m1, BenchmarkMetrics m2) {
        StringBuilder report = new StringBuilder();
        report.append("BENCHMARK COMPARISON REPORT\n");
        report.append("=".repeat(70)).append("\n\n");
        
        report.append(m1.getMetricsString()).append("\n");
        report.append(m2.getMetricsString()).append("\n");
        
        return report.toString();
    }
}


