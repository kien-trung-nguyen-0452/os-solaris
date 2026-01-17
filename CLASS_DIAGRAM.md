# Class Diagram Documentation
# Tài liệu Sơ đồ Lớp

## UML Class Diagram / Sơ đồ Lớp UML

```
┌─────────────────────────────────────────────────────────────────┐
│                         Process                                  │
│                         Tiến trình                               │
├─────────────────────────────────────────────────────────────────┤
│ - processId: int                                                │
│ - processName: String                                           │
│ - state: ProcessState                                           │
│ - priority: int                                                 │
│ - burstTime: int                                                │
│ - remainingTime: int                                            │
│ - arrivalTime: int                                              │
│ - startTime: int                                                │
│ - completionTime: int                                           │
│ - waitingTime: int                                              │
│ - turnaroundTime: int                                           │
├─────────────────────────────────────────────────────────────────┤
│ + Process(id, name, priority, burst, arrival)                   │
│ + getProcessId(): int                                           │
│ + getState(): ProcessState                                      │
│ + setState(state): void                                         │
│ + getPriority(): int                                            │
│ + getRemainingTime(): int                                       │
│ + execute(timeQuantum): int                                     │
│ + isCompleted(): boolean                                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ uses / sử dụng
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ProcessState (enum)                        │
│                      Trạng thái Tiến trình                      │
├─────────────────────────────────────────────────────────────────┤
│ NEW                                                             │
│ READY                                                           │
│ RUNNING                                                         │
│ WAITING                                                         │
│ TERMINATED                                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        Dispatcher                               │
│                        Bộ Điều phối                             │
├─────────────────────────────────────────────────────────────────┤
│ - contextSwitchTime: int                                        │
├─────────────────────────────────────────────────────────────────┤
│ + dispatch(process, currentTime): int                           │
│ + preempt(process, currentTime): void                           │
│ + complete(process, currentTime): void                          │
│ + getContextSwitchTime(): int                                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        Scheduler                                │
│                        Bộ Lập lịch                             │
│                      (Abstract Class)                           │
│                      (Lớp Trừu tượng)                           │
├─────────────────────────────────────────────────────────────────┤
│ # schedulerName: String                                         │
│ # readyQueue: Queue<Process>                                    │
│ # currentProcess: Process                                       │
│ # currentTime: int                                              │
│ # completedProcesses: List<Process>                             │
│ # timeQuantum: int                                              │
├─────────────────────────────────────────────────────────────────┤
│ + Scheduler(name, timeQuantum)                                  │
│ + addProcess(process): void                                     │
│ + schedule(): void                                              │
│ # executeCurrentProcess(): void                                 │
│ # shouldPreempt(): boolean                                      │
│ # printStatistics(): void                                       │
│ + selectNextProcess(): Process (abstract)                       │
│ # checkNewArrivals(): void (abstract)                           │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │
                ┌─────────────┴─────────────┐
                │                           │
                │                           │
┌───────────────────────────┐  ┌───────────────────────────┐
│   RoundRobinScheduler     │  │    PriorityScheduler      │
│   Bộ Lập lịch Round Robin │  │    Bộ Lập lịch Priority   │
├───────────────────────────┤  ├───────────────────────────┤
│ - allProcesses: List      │  │ - allProcesses: List      │
│ - processIndex: int       │  │ - processIndex: int       │
├───────────────────────────┤  ├───────────────────────────┤
│ + RoundRobinScheduler(q)  │  │ + PriorityScheduler(q)    │
│ + initializeProcesses()   │  │ + initializeProcesses()   │
│ + selectNextProcess()     │  │ + selectNextProcess()     │
│ # shouldPreempt(): boolean│  │ # shouldPreempt(): boolean│
│ # checkNewArrivals(): void│  │ # checkNewArrivals(): void│
└───────────────────────────┘  └───────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      ProcessManager                             │
│                      Quản lý Tiến trình                        │
├─────────────────────────────────────────────────────────────────┤
│ - processes: List<Process>                                      │
│ - processMap: Map<Integer, Process>                            │
│ - nextProcessId: int                                            │
├─────────────────────────────────────────────────────────────────┤
│ + ProcessManager()                                              │
│ + createProcess(name, priority, burst, arrival): Process        │
│ + getProcess(id): Process                                       │
│ + getAllProcesses(): List<Process>                              │
│ + createProcessCopies(): List<Process>                          │
│ + createDefaultProcesses(): void                                │
│ + printAllProcesses(): void                                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     SchedulingThread                            │
│                     Thread Lập lịch                             │
│                      (extends Thread)                           │
│                      (Kế thừa Thread)                           │
├─────────────────────────────────────────────────────────────────┤
│ - scheduler: Scheduler                                          │
│ - processes: List<Process>                                      │
│ - threadName: String                                            │
├─────────────────────────────────────────────────────────────────┤
│ + SchedulingThread(name, scheduler, processes)                 │
│ + run(): void                                                   │
│ + getScheduler(): Scheduler                                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     OSSimulationMain                            │
│                     Chương trình Chính                         │
├─────────────────────────────────────────────────────────────────┤
│ + main(args: String[]): void                                    │
│ - printComparison(rr, priority): void                          │
└─────────────────────────────────────────────────────────────────┘
```

## Class Relationships / Mối quan hệ Lớp

### 1. Process and ProcessState / Tiến trình và Trạng thái Tiến trình
- **Relationship**: Composition / **Mối quan hệ**: Composition
- **Description**: Process uses ProcessState enum to track its current state / **Mô tả**: Process sử dụng enum ProcessState để theo dõi trạng thái hiện tại

### 2. Scheduler and Process / Bộ Lập lịch và Tiến trình
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: Scheduler manages Process objects in ready queue and executes them / **Mô tả**: Scheduler quản lý các đối tượng Process trong hàng đợi sẵn sàng và thực thi chúng

### 3. Dispatcher and Process / Bộ Điều phối và Tiến trình
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: Dispatcher performs context switching and state transitions on Process objects / **Mô tả**: Dispatcher thực hiện chuyển đổi ngữ cảnh và chuyển đổi trạng thái trên các đối tượng Process

### 4. RoundRobinScheduler and Scheduler / RoundRobinScheduler và Scheduler
- **Relationship**: Inheritance / **Mối quan hệ**: Kế thừa
- **Description**: RoundRobinScheduler extends abstract Scheduler class / **Mô tả**: RoundRobinScheduler kế thừa lớp Scheduler trừu tượng

### 5. PriorityScheduler and Scheduler / PriorityScheduler và Scheduler
- **Relationship**: Inheritance / **Mối quan hệ**: Kế thừa
- **Description**: PriorityScheduler extends abstract Scheduler class / **Mô tả**: PriorityScheduler kế thừa lớp Scheduler trừu tượng

### 6. ProcessManager and Process / Quản lý Tiến trình và Tiến trình
- **Relationship**: Composition / **Mối quan hệ**: Composition
- **Description**: ProcessManager creates and manages Process objects / **Mô tả**: ProcessManager tạo và quản lý các đối tượng Process

### 7. SchedulingThread and Scheduler / Thread Lập lịch và Scheduler
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: SchedulingThread wraps a Scheduler to run it in a separate thread / **Mô tả**: SchedulingThread bọc một Scheduler để chạy nó trong một thread riêng biệt

### 8. OSSimulationMain and other classes / OSSimulationMain và các lớp khác
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: Main class creates and coordinates all other components / **Mô tả**: Lớp Main tạo và điều phối tất cả các thành phần khác

## Method Descriptions / Mô tả Phương thức

### Process Class / Lớp Tiến trình
- `execute(timeQuantum)`: Executes the process for given time quantum, returns actual execution time / Thực thi tiến trình trong time quantum đã cho, trả về thời gian thực thi thực tế
- `isCompleted()`: Checks if process has completed execution / Kiểm tra xem tiến trình đã hoàn thành thực thi chưa

### Dispatcher Class / Lớp Bộ Điều phối
- `dispatch()`: Transitions process from READY to RUNNING state / Chuyển tiến trình từ trạng thái READY sang RUNNING
- `preempt()`: Preempts running process, saves context / Preempt tiến trình đang chạy, lưu ngữ cảnh
- `complete()`: Marks process as terminated, calculates statistics / Đánh dấu tiến trình là đã kết thúc, tính toán thống kê

### Scheduler Class (Abstract) / Lớp Scheduler (Trừu tượng)
- `addProcess()`: Adds process to ready queue / Thêm tiến trình vào hàng đợi sẵn sàng
- `schedule()`: Main scheduling loop / Vòng lặp lập lịch chính
- `selectNextProcess()`: Abstract method to select next process (implemented by subclasses) / Phương thức trừu tượng để chọn tiến trình tiếp theo (được triển khai bởi các lớp con)
- `checkNewArrivals()`: Abstract method to check for new process arrivals / Phương thức trừu tượng để kiểm tra các tiến trình mới đến

### RoundRobinScheduler / Bộ Lập lịch Round Robin
- `selectNextProcess()`: Selects process from front of queue (FIFO) / Chọn tiến trình từ đầu hàng đợi (FIFO)
- `shouldPreempt()`: Always preempts after time quantum / Luôn preempt sau time quantum

### PriorityScheduler / Bộ Lập lịch Priority
- `selectNextProcess()`: Selects process with highest priority (lowest priority number) / Chọn tiến trình có độ ưu tiên cao nhất (số priority thấp nhất)
- `shouldPreempt()`: Preempts if higher priority process arrives or time quantum expires / Preempt nếu tiến trình priority cao hơn đến hoặc time quantum hết

### ProcessManager / Quản lý Tiến trình
- `createProcess()`: Creates new process with given parameters / Tạo tiến trình mới với các tham số đã cho
- `createProcessCopies()`: Creates independent copies of processes for schedulers / Tạo bản sao độc lập của các tiến trình cho schedulers

### SchedulingThread / Thread Lập lịch
- `run()`: Thread execution method that runs the scheduler / Phương thức thực thi thread chạy scheduler

## Data Flow / Luồng Dữ liệu

1. **Process Creation**: ProcessManager creates Process objects / **Tạo Tiến trình**: ProcessManager tạo các đối tượng Process
2. **Process Initialization**: Processes are added to schedulers / **Khởi tạo Tiến trình**: Các tiến trình được thêm vào schedulers
3. **Scheduling**: Scheduler selects next process / **Lập lịch**: Scheduler chọn tiến trình tiếp theo
4. **Dispatching**: Dispatcher transitions process to RUNNING / **Điều phối**: Dispatcher chuyển tiến trình sang RUNNING
5. **Execution**: Process executes for time quantum / **Thực thi**: Tiến trình thực thi trong time quantum
6. **Preemption/Completion**: Process is preempted or completes / **Preemption/Hoàn thành**: Tiến trình bị preempt hoặc hoàn thành
7. **Statistics**: Statistics are calculated and printed / **Thống kê**: Thống kê được tính toán và in ra

## Design Patterns Used / Các Mẫu Thiết kế được Sử dụng

1. **Template Method Pattern**: Scheduler abstract class defines algorithm structure, subclasses implement specific steps / **Mẫu Template Method**: Lớp Scheduler trừu tượng định nghĩa cấu trúc thuật toán, các lớp con triển khai các bước cụ thể
2. **Strategy Pattern**: Different scheduling algorithms (Round Robin, Priority) are interchangeable / **Mẫu Strategy**: Các thuật toán lập lịch khác nhau (Round Robin, Priority) có thể thay thế cho nhau
3. **Factory Pattern**: ProcessManager creates Process objects / **Mẫu Factory**: ProcessManager tạo các đối tượng Process
4. **Thread Pattern**: SchedulingThread wraps schedulers to run concurrently / **Mẫu Thread**: SchedulingThread bọc các schedulers để chạy đồng thời
