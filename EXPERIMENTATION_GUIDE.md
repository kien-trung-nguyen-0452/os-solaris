# OS Kernel Simulation - Program Description and Experimentation Guide

## Program Overview

This Operating System Kernel Simulation is a comprehensive Java-based application that demonstrates the core operations of an OS kernel, specifically focusing on process scheduling, dispatching, and state management. The program provides both a basic demonstration mode and an interactive benchmarking system for detailed analysis.

## Core Components

### 1. Process Management System

The program implements a complete process lifecycle management system:

- **Process States**: NEW → READY → RUNNING → WAITING → TERMINATED
- **Process Attributes**: 
  - Unique Process ID
  - Process Name
  - Priority (1 = highest, 10 = lowest)
  - Burst Time (total CPU time required)
  - Arrival Time (when process enters the system)
  - Remaining Time (time left to complete)
  - Completion Time
  - Turnaround Time
  - Waiting Time

### 2. Scheduling Algorithms

#### Round Robin Scheduling
- **Principle**: Each process receives a fixed time quantum (default: 3 time units)
- **Queue Management**: First-In-First-Out (FIFO) ready queue
- **Preemption**: Automatic after time quantum expires
- **Fairness**: Ensures all processes get equal CPU time slices
- **Use Case**: Best for time-sharing systems where fairness is important

#### Priority Scheduling
- **Principle**: Processes with higher priority (lower number) execute first
- **Queue Management**: Priority-based ordering
- **Preemption**: Higher priority processes can preempt lower priority ones
- **Dynamic**: Can handle priority changes during execution
- **Use Case**: Best for systems where some processes are more critical than others

### 3. Dispatching Mechanism

The Dispatcher class handles:
- **Context Switching**: Saving and restoring process state
- **State Transitions**: READY → RUNNING transitions
- **Preemption Handling**: Interrupting running processes
- **Completion Handling**: Moving processes to TERMINATED state

### 4. Multi-Threading Implementation

The program demonstrates concurrent execution using two independent threads:
- **Thread 1**: Round Robin Scheduler (runs independently)
- **Thread 2**: Priority Scheduler (runs simultaneously)
- Each thread operates on separate process copies to avoid conflicts
- Demonstrates real concurrent scheduling behavior

## Program Modes

### Mode 1: Basic Demonstration (OSSimulationMain.java)

**Purpose**: Quick demonstration of both scheduling algorithms running concurrently

**How to Run**:
```bash
cd OS_Simulation/src
javac *.java
java OSSimulationMain
```

**What It Does**:
1. Creates 5 default processes with varying priorities and burst times
2. Launches two threads simultaneously:
   - Round Robin scheduler thread
   - Priority scheduler thread
3. Displays real-time scheduling decisions
4. Shows statistics for both algorithms
5. Provides basic comparison

**Output Includes**:
- Process list with all attributes
- Real-time scheduling events (dispatch, preempt, complete)
- Individual process statistics
- Average turnaround time
- Average waiting time
- Total execution time comparison

### Mode 2: Interactive Benchmarking System (InteractiveMain.java)

**Purpose**: Advanced experimentation with custom processes and detailed metrics

**How to Run**:
```bash
cd OS_Simulation/src
javac *.java
java InteractiveMain
```

**Features**:
- Interactive menu-driven interface
- Manual process input
- Default process sets
- Comprehensive benchmarking
- Detailed performance metrics
- Scheduler comparison
- Results export functionality

## Experimentation Guide

### Experiment 1: Understanding Basic Scheduling

**Objective**: Observe how Round Robin and Priority schedulers handle the same set of processes differently.

**Steps**:
1. Run `OSSimulationMain`
2. Observe the output from both threads
3. Note the order of process execution
4. Compare completion times for each process
5. Analyze which scheduler finishes all processes first

**Expected Observations**:
- Round Robin: Processes execute in order, each getting equal time slices
- Priority: Higher priority processes complete first
- Different total execution times
- Different waiting times for individual processes

### Experiment 2: Custom Process Creation

**Objective**: Create your own process set and observe scheduling behavior.

**Steps**:
1. Run `InteractiveMain`
2. Select option 1: "Input Processes Manually"
3. Enter 3-5 processes with different characteristics:
   - Mix of high and low priorities
   - Varying burst times
   - Different arrival times
4. Select option 3: "Run Benchmark"
5. Analyze the results

**Test Cases to Try**:
- **Case A**: All processes arrive at time 0, different priorities
- **Case B**: Staggered arrival times, same priority
- **Case C**: Mix of short and long burst times
- **Case D**: All high priority processes
- **Case E**: All low priority processes

### Experiment 3: Time Quantum Impact Analysis

**Objective**: Understand how time quantum affects Round Robin performance.

**Steps**:
1. Run `InteractiveMain`
2. Use default processes (option 2)
3. Run benchmark with time quantum = 1
4. Note the results
5. Run benchmark again with time quantum = 5
6. Run benchmark with time quantum = 10
7. Compare the metrics

**What to Observe**:
- Context switch count (higher with smaller quantum)
- Average waiting time
- Total execution time
- CPU utilization
- Throughput

**Hypothesis**: Smaller time quantum = more context switches but better response time for interactive processes.

### Experiment 4: Priority Scheduling Behavior

**Objective**: Study how priority affects scheduling decisions.

**Steps**:
1. Create processes with these priorities: 1, 1, 5, 5, 10, 10
2. Give them different burst times
3. Run Priority scheduler only (option 4)
4. Observe execution order
5. Try changing priorities and re-running

**Key Questions**:
- Do processes with same priority execute in FIFO order?
- What happens when a high priority process arrives while a low priority one is running?
- How does preemption work?

### Experiment 5: Performance Comparison

**Objective**: Determine which scheduler performs better under different conditions.

**Steps**:
1. Create a test scenario (e.g., 5 processes with specific characteristics)
2. Run benchmark (option 3)
3. Review the comparison metrics
4. Export results (option 6)
5. Modify process characteristics
6. Re-run and compare

**Metrics to Compare**:
- Average Turnaround Time (lower is better)
- Average Waiting Time (lower is better)
- Average Response Time (lower is better)
- Throughput (higher is better)
- CPU Utilization (higher is better)
- Context Switch Count (lower is better for efficiency)

### Experiment 6: Large Process Set Analysis

**Objective**: Test scheduler performance with many processes.

**Steps**:
1. Create 10-15 processes manually
2. Mix of priorities and burst times
3. Run benchmark
4. Analyze if one scheduler scales better
5. Check for starvation issues (especially in Priority)

**Observations**:
- Does Round Robin maintain fairness with many processes?
- Can Priority scheduling cause starvation?
- Which scheduler handles large sets more efficiently?

### Experiment 7: Arrival Time Impact

**Objective**: Understand how arrival times affect scheduling.

**Steps**:
1. Create processes with staggered arrival times:
   - Process 1: arrival = 0, burst = 10
   - Process 2: arrival = 5, burst = 5
   - Process 3: arrival = 10, burst = 8
2. Run both schedulers
3. Compare how each handles late arrivals
4. Observe waiting times

**Key Insight**: Round Robin may have processes waiting longer if they arrive late, while Priority might preempt to serve high-priority late arrivals.

## Understanding the Output

### Process Execution Log

Each scheduling event is logged with:
- **Time**: Current simulation time
- **Event Type**: DISPATCH, PREEMPT, or COMPLETE
- **Process**: Which process is affected
- **State Change**: Previous state → New state

### Statistics Table

For each scheduler, you'll see:
- **Process ID/Name**: Identifier
- **Priority**: Process priority level
- **Burst Time**: Original CPU time needed
- **Arrival Time**: When process entered system
- **Completion Time**: When process finished
- **Turnaround Time**: Completion - Arrival
- **Waiting Time**: Turnaround - Burst

### Benchmark Metrics

The interactive system provides:
- **Average Turnaround Time**: Mean time from arrival to completion
- **Average Waiting Time**: Mean time spent in ready queue
- **Average Response Time**: Mean time from arrival to first execution
- **Throughput**: Processes completed per time unit
- **CPU Utilization**: Percentage of time CPU was busy
- **Context Switches**: Number of process switches

## Advanced Experiments

### Experiment 8: Starvation Analysis

Create a scenario where low-priority processes might starve:
- Multiple high-priority processes (priority 1-2)
- Few low-priority processes (priority 9-10)
- Continuous arrival of high-priority processes

**Question**: Does Priority scheduling cause starvation? How does Round Robin handle this?

### Experiment 9: Optimal Time Quantum

Find the optimal time quantum for a specific process set:
1. Fix a process set
2. Test with quantum = 1, 2, 3, 5, 10, 20
3. Plot turnaround time vs quantum
4. Find the sweet spot

### Experiment 10: Mixed Workload

Simulate a realistic workload:
- Interactive processes (short burst, high priority)
- Batch processes (long burst, low priority)
- Background processes (medium burst, medium priority)

Compare how each scheduler handles the mix.

## Troubleshooting

### Common Issues

1. **No processes error**: Make sure to create processes before running schedulers
2. **Compilation errors**: Ensure all Java files are in the same directory
3. **Thread interleaving**: Output may appear mixed - this is normal and demonstrates true concurrency

### Tips for Better Experiments

1. **Start Simple**: Begin with 3-4 processes to understand behavior
2. **Take Notes**: Document your hypotheses and observations
3. **Vary One Thing**: Change one parameter at a time to see its effect
4. **Use Export**: Save results for later comparison
5. **Try Edge Cases**: Test with extreme values (very high/low priority, very long/short bursts)

## Comparison with Basic Version

The program has evolved from a simple demonstration to a comprehensive experimentation platform:

**Basic Version (OSSimulationMain)**:
- Fixed process set
- Simple output
- Basic statistics
- Concurrent execution demonstration

**Interactive Version (InteractiveMain)**:
- Custom process creation
- Advanced benchmarking
- Detailed metrics (6 performance indicators)
- Comparison analysis
- Results export
- Flexible time quantum
- Single scheduler mode

**Key Improvements**:
- User control over process characteristics
- Quantitative performance analysis
- Export capabilities for documentation
- Better understanding of algorithm trade-offs
- Educational value for learning OS concepts

## Educational Value

This simulation helps understand:
- How CPU scheduling works in real operating systems
- Trade-offs between different scheduling algorithms
- Impact of process characteristics on performance
- Multi-threading and concurrent execution
- Performance metrics and their significance
- Context switching overhead
- Fairness vs efficiency trade-offs

## Conclusion

The OS Kernel Simulation provides a hands-on platform for experimenting with and understanding operating system scheduling algorithms. Through systematic experimentation, you can gain deep insights into how different scheduling strategies affect system performance and process behavior.

Use this guide as a starting point, but don't hesitate to create your own experiments and explore scenarios that interest you. The interactive system is designed to support your learning journey.



