# Class Diagram and Kernel Architecture Documentation
# Tài liệu Sơ đồ Lớp và Kiến trúc Kernel

## UML Class Diagram / Sơ đồ Lớp UML

```
┌─────────────────────────────────────────────────────────────────┐
│                         Process                                  │
│                         Tiến trình                               │
├─────────────────────────────────────────────────────────────────┤
│ - processId: int                                                │
│ - processName: String                                           │
│ - state: ProcessState                                           │
│ - priorityLevel: int                                           │
│ - burstTime: int                                               │
│ - remainingTime: int                                           │
│ - arrivalTime: int                                             │
│ - startTime: int                                                │
│ - completionTime: int                                           │
│ - waitingTime: int                                              │
│ - turnaroundTime: int                                           │
├─────────────────────────────────────────────────────────────────┤
│ + Process(id, name, priorityLevel, burst, arrival)             │
│ + getProcessId(): int                                           │
│ + getState(): ProcessState                                      │
│ + setState(state): void                                         │
│ + getPriorityLevel(): int                                        │
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
│                      ProcessDispatcher                          │
│                      Bộ Điều phối Tiến trình                    │
│                      [ROLE: DISPATCHER]                         │
│                      [VAI TRÒ: DISPATCHER]                      │
│                                                                  │
│  This class implements the DISPATCHER component of the OS       │
│  kernel. It handles context switching and process dispatching. │
│  Lớp này triển khai thành phần DISPATCHER của kernel OS.       │
│  Xử lý chuyển đổi ngữ cảnh và điều phối tiến trình.            │
├─────────────────────────────────────────────────────────────────┤
│ - contextSwitchOverhead: int                                    │
├─────────────────────────────────────────────────────────────────┤
│ + dispatch(process, systemTime): int                            │
│   [Performs context switch: READY -> RUNNING]                   │
│   [Thực hiện context switch: READY -> RUNNING]                  │
│ + preempt(process, systemTime): void                            │
│   [Saves context and switches: RUNNING -> READY]                 │
│   [Lưu ngữ cảnh và chuyển: RUNNING -> READY]                    │
│ + complete(process, systemTime): void                           │
│   [Marks process as terminated: RUNNING -> TERMINATED]          │
│   [Đánh dấu tiến trình kết thúc: RUNNING -> TERMINATED]        │
│ + getContextSwitchOverhead(): int                               │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      ProcessScheduler                           │
│                      Bộ Lập lịch Tiến trình                    │
│                      [ROLE: SCHEDULER]                          │
│                      [VAI TRÒ: SCHEDULER]                       │
│                                                                  │
│  This class implements the SCHEDULER component of the OS       │
│  kernel. It manages process scheduling algorithms.              │
│  Lớp này triển khai thành phần SCHEDULER của kernel OS.        │
│  Quản lý các thuật toán lập lịch tiến trình.                   │
│                      (Abstract Class)                            │
│                      (Lớp Trừu tượng)                           │
├─────────────────────────────────────────────────────────────────┤
│ # schedulerName: String                                         │
│ # readyQueue: Queue<Process>                                    │
│ # currentProcess: Process                                       │
│ # systemClock: int                                              │
│ # completedProcesses: List<Process>                             │
│ # timeQuantum: int                                              │
├─────────────────────────────────────────────────────────────────┤
│ + ProcessScheduler(name, timeQuantum)                          │
│ + enqueueProcess(process): void                                 │
│ + schedule(): void                                              │
│   [Main scheduling loop - calls Dispatcher]                     │
│   [Vòng lặp lập lịch chính - gọi Dispatcher]                   │
│ # executeCurrentProcess(): void                                │
│ # shouldPreempt(): boolean                                      │
│ # displayStatistics(): void                                     │
│ + selectNextProcess(): Process (abstract)                       │
│ # checkNewArrivals(): void (abstract)                           │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │
                ┌─────────────┴─────────────┐
                │                           │
                │                           │
┌───────────────────────────┐  ┌───────────────────────────┐
│   RoundRobinScheduler      │  │    PriorityScheduler      │
│   [ROLE: SCHEDULER]        │  │    [ROLE: SCHEDULER]      │
│   Bộ Lập lịch Round Robin  │  │    Bộ Lập lịch Priority   │
├───────────────────────────┤  ├───────────────────────────┤
│ - allProcesses: List      │  │ - allProcesses: List      │
│ - processIndex: int       │  │ - processIndex: int       │
├───────────────────────────┤  ├───────────────────────────┤
│ + RoundRobinScheduler(q)   │  │ + PriorityScheduler(q)    │
│ + initializeProcesses()   │  │ + initializeProcesses()   │
│ + selectNextProcess()     │  │ + selectNextProcess()     │
│   [FIFO selection]        │  │   [Priority-based]        │
│ # shouldPreempt(): boolean│  │ # shouldPreempt(): boolean│
│ # checkNewArrivals(): void│  │ # checkNewArrivals(): void│
└───────────────────────────┘  └───────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      ProcessRegistry                            │
│                      Bộ Đăng ký Tiến trình                      │
│                      [ROLE: PROCESS MANAGER]                    │
│                      [VAI TRÒ: QUẢN LÝ TIẾN TRÌNH]              │
├─────────────────────────────────────────────────────────────────┤
│ - processes: List<Process>                                      │
│ - processMap: Map<Integer, Process>                            │
│ - nextProcessId: int                                            │
├─────────────────────────────────────────────────────────────────┤
│ + ProcessRegistry()                                             │
│ + createProcess(name, priorityLevel, burst, arrival): Process  │
│ + getProcess(id): Process                                       │
│ + getAllProcesses(): List<Process>                              │
│ + duplicateProcesses(): List<Process>                           │
│ + createDefaultProcesses(): void                                │
│ + printAllProcesses(): void                                     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      SchedulerThread                            │
│                      Thread Lập lịch                          │
│                      (extends Thread)                           │
│                      (Kế thừa Thread)                           │
├─────────────────────────────────────────────────────────────────┤
│ - scheduler: ProcessScheduler                                  │
│ - processes: List<Process>                                     │
│ - threadLabel: String                                           │
├─────────────────────────────────────────────────────────────────┤
│ + SchedulerThread(label, scheduler, processes)                 │
│ + run(): void                                                   │
│ + getScheduler(): ProcessScheduler                             │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                  ProcessSchedulingDemo                          │
│                  Chương trình Demo Lập lịch                    │
├─────────────────────────────────────────────────────────────────┤
│ + main(args: String[]): void                                    │
│ - displayComparison(rr, priority): void                         │
└─────────────────────────────────────────────────────────────────┘
```

## Kernel Components Mapping / Ánh xạ Thành phần Kernel

### OS Kernel Components / Các Thành phần Kernel OS

This simulation implements the following OS kernel components:
Mô phỏng này triển khai các thành phần kernel OS sau:

1. **ProcessDispatcher [DISPATCHER ROLE]**
   - **Kernel Function**: Context switching and process dispatching
   - **Chức năng Kernel**: Chuyển đổi ngữ cảnh và điều phối tiến trình
   - **Responsibilities**: 
     - Transitions processes from READY to RUNNING state
     - Saves and restores process context during preemption
     - Handles process completion and state transitions
   - **Trách nhiệm**:
     - Chuyển tiến trình từ trạng thái READY sang RUNNING
     - Lưu và khôi phục ngữ cảnh tiến trình khi preemption
     - Xử lý hoàn thành tiến trình và chuyển đổi trạng thái

2. **ProcessScheduler [SCHEDULER ROLE]**
   - **Kernel Function**: Process scheduling and queue management
   - **Chức năng Kernel**: Lập lịch tiến trình và quản lý hàng đợi
   - **Responsibilities**:
     - Manages ready queue of processes
     - Selects next process to execute based on algorithm
     - Coordinates with Dispatcher for process execution
   - **Trách nhiệm**:
     - Quản lý hàng đợi sẵn sàng của các tiến trình
     - Chọn tiến trình tiếp theo để thực thi dựa trên thuật toán
     - Phối hợp với Dispatcher để thực thi tiến trình

3. **RoundRobinScheduler [SCHEDULER ROLE]**
   - **Kernel Function**: Round Robin scheduling algorithm
   - **Chức năng Kernel**: Thuật toán lập lịch Round Robin
   - **Algorithm**: FIFO queue with time quantum preemption

4. **PriorityScheduler [SCHEDULER ROLE]**
   - **Kernel Function**: Priority-based scheduling algorithm
   - **Chức năng Kernel**: Thuật toán lập lịch dựa trên độ ưu tiên
   - **Algorithm**: Priority queue with preemption on higher priority arrival

5. **ProcessRegistry [PROCESS MANAGER ROLE]**
   - **Kernel Function**: Process lifecycle management
   - **Chức năng Kernel**: Quản lý vòng đời tiến trình
   - **Responsibilities**: Process creation, tracking, and duplication

## Class Relationships / Mối quan hệ Lớp

### 1. Process and ProcessState / Tiến trình và Trạng thái Tiến trình
- **Relationship**: Composition / **Mối quan hệ**: Composition
- **Description**: Process uses ProcessState enum to track its current state / **Mô tả**: Process sử dụng enum ProcessState để theo dõi trạng thái hiện tại

### 2. ProcessScheduler and Process / Bộ Lập lịch và Tiến trình
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: ProcessScheduler manages Process objects in ready queue and executes them / **Mô tả**: ProcessScheduler quản lý các đối tượng Process trong hàng đợi sẵn sàng và thực thi chúng

### 3. ProcessDispatcher and Process / Bộ Điều phối và Tiến trình
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: ProcessDispatcher performs context switching and state transitions on Process objects. **This is the DISPATCHER component of the kernel.** / **Mô tả**: ProcessDispatcher thực hiện chuyển đổi ngữ cảnh và chuyển đổi trạng thái trên các đối tượng Process. **Đây là thành phần DISPATCHER của kernel.**

### 4. ProcessScheduler and ProcessDispatcher / Bộ Lập lịch và Bộ Điều phối
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: ProcessScheduler uses ProcessDispatcher to dispatch selected processes to CPU. This demonstrates the interaction between SCHEDULER and DISPATCHER components. / **Mô tả**: ProcessScheduler sử dụng ProcessDispatcher để điều phối các tiến trình đã chọn lên CPU. Điều này minh họa sự tương tác giữa các thành phần SCHEDULER và DISPATCHER.

### 5. RoundRobinScheduler and ProcessScheduler / RoundRobinScheduler và ProcessScheduler
- **Relationship**: Inheritance / **Mối quan hệ**: Kế thừa
- **Description**: RoundRobinScheduler extends abstract ProcessScheduler class / **Mô tả**: RoundRobinScheduler kế thừa lớp ProcessScheduler trừu tượng

### 6. PriorityScheduler and ProcessScheduler / PriorityScheduler và ProcessScheduler
- **Relationship**: Inheritance / **Mối quan hệ**: Kế thừa
- **Description**: PriorityScheduler extends abstract ProcessScheduler class / **Mô tả**: PriorityScheduler kế thừa lớp ProcessScheduler trừu tượng

### 7. ProcessRegistry and Process / Bộ Đăng ký và Tiến trình
- **Relationship**: Composition / **Mối quan hệ**: Composition
- **Description**: ProcessRegistry creates and manages Process objects / **Mô tả**: ProcessRegistry tạo và quản lý các đối tượng Process

### 8. SchedulerThread and ProcessScheduler / Thread Lập lịch và ProcessScheduler
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: SchedulerThread wraps a ProcessScheduler to run it in a separate thread / **Mô tả**: SchedulerThread bọc một ProcessScheduler để chạy nó trong một thread riêng biệt

### 9. ProcessSchedulingDemo and other classes / ProcessSchedulingDemo và các lớp khác
- **Relationship**: Association (uses) / **Mối quan hệ**: Association (sử dụng)
- **Description**: Main class creates and coordinates all other components / **Mô tả**: Lớp Main tạo và điều phối tất cả các thành phần khác

## Method Descriptions / Mô tả Phương thức

### Process Class / Lớp Tiến trình
- `execute(timeQuantum)`: Executes the process for given time quantum, returns actual execution time / Thực thi tiến trình trong time quantum đã cho, trả về thời gian thực thi thực tế
- `isCompleted()`: Checks if process has completed execution / Kiểm tra xem tiến trình đã hoàn thành thực thi chưa

### ProcessDispatcher Class [DISPATCHER ROLE] / Lớp ProcessDispatcher [VAI TRÒ DISPATCHER]
- `dispatch()`: **Dispatcher function** - Transitions process from READY to RUNNING state. Performs context switching. / **Chức năng Dispatcher** - Chuyển tiến trình từ trạng thái READY sang RUNNING. Thực hiện context switching.
- `preempt()`: **Dispatcher function** - Preempts running process, saves context, transitions from RUNNING to READY. / **Chức năng Dispatcher** - Preempt tiến trình đang chạy, lưu ngữ cảnh, chuyển từ RUNNING sang READY.
- `complete()`: **Dispatcher function** - Marks process as terminated, calculates statistics, transitions from RUNNING to TERMINATED. / **Chức năng Dispatcher** - Đánh dấu tiến trình là đã kết thúc, tính toán thống kê, chuyển từ RUNNING sang TERMINATED.

### ProcessScheduler Class (Abstract) [SCHEDULER ROLE] / Lớp ProcessScheduler (Trừu tượng) [VAI TRÒ SCHEDULER]
- `enqueueProcess()`: Adds process to ready queue / Thêm tiến trình vào hàng đợi sẵn sàng
- `schedule()`: **Scheduler function** - Main scheduling loop. Calls Dispatcher to dispatch selected processes. / **Chức năng Scheduler** - Vòng lặp lập lịch chính. Gọi Dispatcher để điều phối các tiến trình đã chọn.
- `selectNextProcess()`: Abstract method to select next process (implemented by subclasses) / Phương thức trừu tượng để chọn tiến trình tiếp theo (được triển khai bởi các lớp con)
- `checkNewArrivals()`: Abstract method to check for new process arrivals / Phương thức trừu tượng để kiểm tra các tiến trình mới đến

### RoundRobinScheduler [SCHEDULER ROLE] / Bộ Lập lịch Round Robin [VAI TRÒ SCHEDULER]
- `selectNextProcess()`: Selects process from front of queue (FIFO) / Chọn tiến trình từ đầu hàng đợi (FIFO)
- `shouldPreempt()`: Always preempts after time quantum / Luôn preempt sau time quantum

### PriorityScheduler [SCHEDULER ROLE] / Bộ Lập lịch Priority [VAI TRÒ SCHEDULER]
- `selectNextProcess()`: Selects process with highest priority (lowest priority number) / Chọn tiến trình có độ ưu tiên cao nhất (số priority thấp nhất)
- `shouldPreempt()`: Preempts if higher priority process arrives or time quantum expires / Preempt nếu tiến trình priority cao hơn đến hoặc time quantum hết

### ProcessRegistry / Bộ Đăng ký Tiến trình
- `createProcess()`: Creates new process with given parameters / Tạo tiến trình mới với các tham số đã cho
- `duplicateProcesses()`: Creates independent copies of processes for schedulers / Tạo bản sao độc lập của các tiến trình cho schedulers

### SchedulerThread / Thread Lập lịch
- `run()`: Thread execution method that runs the scheduler / Phương thức thực thi thread chạy scheduler

## Data Flow / Luồng Dữ liệu

1. **Process Creation**: ProcessRegistry creates Process objects / **Tạo Tiến trình**: ProcessRegistry tạo các đối tượng Process
2. **Process Initialization**: Processes are added to schedulers / **Khởi tạo Tiến trình**: Các tiến trình được thêm vào schedulers
3. **Scheduling**: ProcessScheduler selects next process / **Lập lịch**: ProcessScheduler chọn tiến trình tiếp theo
4. **Dispatching**: ProcessDispatcher transitions process to RUNNING / **Điều phối**: ProcessDispatcher chuyển tiến trình sang RUNNING
5. **Execution**: Process executes for time quantum / **Thực thi**: Tiến trình thực thi trong time quantum
6. **Preemption/Completion**: ProcessDispatcher handles preemption or completion / **Preemption/Hoàn thành**: ProcessDispatcher xử lý preemption hoặc hoàn thành
7. **Statistics**: Statistics are calculated and printed / **Thống kê**: Thống kê được tính toán và in ra

## Design Patterns Used / Các Mẫu Thiết kế được Sử dụng

1. **Template Method Pattern**: ProcessScheduler abstract class defines algorithm structure, subclasses implement specific steps / **Mẫu Template Method**: Lớp ProcessScheduler trừu tượng định nghĩa cấu trúc thuật toán, các lớp con triển khai các bước cụ thể
2. **Strategy Pattern**: Different scheduling algorithms (Round Robin, Priority) are interchangeable / **Mẫu Strategy**: Các thuật toán lập lịch khác nhau (Round Robin, Priority) có thể thay thế cho nhau
3. **Factory Pattern**: ProcessRegistry creates Process objects / **Mẫu Factory**: ProcessRegistry tạo các đối tượng Process
4. **Thread Pattern**: SchedulerThread wraps schedulers to run concurrently / **Mẫu Thread**: SchedulerThread bọc các schedulers để chạy đồng thời

---

## Kernel Architecture Block Diagram / Sơ đồ Khối Kiến trúc Kernel

This simulation implements the core components of an Operating System kernel:
Mô phỏng này triển khai các thành phần cốt lõi của kernel hệ điều hành:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    OPERATING SYSTEM KERNEL SIMULATION                   │
│                    MÔ PHỎNG KERNEL HỆ ĐIỀU HÀNH                         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
        ┌───────────────────────────────────────────────────┐
        │         PROCESS REGISTRY (Process Manager)        │
        │         BỘ ĐĂNG KÝ TIẾN TRÌNH                     │
        │                                                     │
        │  - Creates and manages Process objects             │
        │  - Tạo và quản lý các đối tượng Process            │
        │  - Maintains process lifecycle                     │
        │  - Duy trì vòng đời tiến trình                     │
        └───────────────────────────────────────────────────┘
                                    │
                                    │ Creates Processes
                                    │ Tạo Tiến trình
                                    ▼
        ┌───────────────────────────────────────────────────┐
        │                    PROCESS                        │
        │                    TIẾN TRÌNH                     │
        │                                                     │
        │  States: NEW → READY → RUNNING → WAITING →         │
        │          TERMINATED                                 │
        │  Trạng thái: NEW → READY → RUNNING → WAITING →     │
        │            TERMINATED                               │
        └───────────────────────────────────────────────────┘
                                    │
                                    │
        ┌───────────────────────────────────────────────────┐
        │              PROCESS SCHEDULER                    │
        │              [ROLE: SCHEDULER]                    │
        │              BỘ LẬP LỊCH TIẾN TRÌNH               │
        │              [VAI TRÒ: SCHEDULER]                 │
        │                                                     │
        │  Kernel Function: Process Scheduling               │
        │  Chức năng Kernel: Lập lịch tiến trình             │
        │                                                     │
        │  Responsibilities:                                  │
        │  Trách nhiệm:                                       │
        │  - Manages Ready Queue                             │
        │  - Quản lý Hàng đợi Sẵn sàng                       │
        │  - Selects next process to run                     │
        │  - Chọn tiến trình tiếp theo để chạy              │
        │  - Coordinates with Dispatcher                      │
        │  - Phối hợp với Dispatcher                         │
        │                                                     │
        │  ┌─────────────────────────────────────────────┐  │
        │  │  RoundRobinScheduler                        │  │
        │  │  - FIFO queue management                     │  │
        │  │  - Time quantum allocation                   │  │
        │  └─────────────────────────────────────────────┘  │
        │                                                     │
        │  ┌─────────────────────────────────────────────┐  │
        │  │  PriorityScheduler                           │  │
        │  │  - Priority-based selection                  │  │
        │  │  - Preemption on higher priority             │  │
        │  └─────────────────────────────────────────────┘  │
        └───────────────────────────────────────────────────┘
                                    │
                                    │ Selects Process
                                    │ Chọn Tiến trình
                                    │
                                    ▼
        ┌───────────────────────────────────────────────────┐
        │            PROCESS DISPATCHER                      │
        │            [ROLE: DISPATCHER]                     │
        │            BỘ ĐIỀU PHỐI TIẾN TRÌNH                │
        │            [VAI TRÒ: DISPATCHER]                  │
        │                                                     │
        │  Kernel Function: Context Switching &             │
        │                    Process Dispatching              │
        │  Chức năng Kernel: Chuyển đổi Ngữ cảnh &          │
        │                    Điều phối Tiến trình           │
        │                                                     │
        │  Responsibilities:                                  │
        │  Trách nhiệm:                                       │
        │  - Context switching (READY → RUNNING)            │
        │  - Chuyển đổi ngữ cảnh (READY → RUNNING)          │
        │  - Saves process context                           │
        │  - Lưu ngữ cảnh tiến trình                        │
        │  - Restores process context                        │
        │  - Khôi phục ngữ cảnh tiến trình                   │
        │  - Handles preemption                              │
        │  - Xử lý preemption                                │
        │  - Manages state transitions                       │
        │  - Quản lý chuyển đổi trạng thái                  │
        │                                                     │
        │  Methods:                                          │
        │  Phương thức:                                      │
        │  + dispatch()    [READY → RUNNING]                │
        │  + preempt()     [RUNNING → READY]                │
        │  + complete()    [RUNNING → TERMINATED]            │
        └───────────────────────────────────────────────────┘
                                    │
                                    │ Dispatches to CPU
                                    │ Điều phối lên CPU
                                    ▼
        ┌───────────────────────────────────────────────────┐
        │                      CPU                           │
        │                      BỘ XỬ LÝ                     │
        │                                                     │
        │  - Executes process instructions                   │
        │  - Thực thi các lệnh của tiến trình                │
        │  - Time quantum enforcement                        │
        │  - Thực thi time quantum                           │
        └───────────────────────────────────────────────────┘
                                    │
                                    │ Execution Complete or
                                    │ Time Quantum Expires
                                    │ Thực thi Hoàn thành hoặc
                                    │ Time Quantum Hết
                                    ▼
        ┌───────────────────────────────────────────────────┐
        │              STATE TRANSITION MANAGER              │
        │              QUẢN LÝ CHUYỂN ĐỔI TRẠNG THÁI        │
        │                                                     │
        │  Handled by ProcessDispatcher:                     │
        │  Được xử lý bởi ProcessDispatcher:                │
        │                                                     │
        │  - If completed: TERMINATED                        │
        │  - Nếu hoàn thành: TERMINATED                      │
        │  - If preempted: READY (back to queue)             │
        │  - Nếu bị preempt: READY (về hàng đợi)            │
        └───────────────────────────────────────────────────┘
```

## Complete Kernel Simulation Flow / Luồng Mô phỏng Kernel Hoàn chỉnh

```
START
  │
  ▼
[1] ProcessRegistry.createProcess()
    Creates Process in NEW state
    │
    ▼
[2] ProcessScheduler.enqueueProcess()
    Adds to Ready Queue
    Process state: NEW → READY
    │
    ▼
[3] ProcessScheduler.schedule() [SCHEDULER]
    Main scheduling loop starts
    │
    ▼
[4] ProcessScheduler.selectNextProcess() [SCHEDULER]
    Algorithm selects next process
    (Round Robin: FIFO, Priority: Highest priority)
    │
    ▼
[5] ProcessDispatcher.dispatch() [DISPATCHER]
    Context switch performed
    Process state: READY → RUNNING
    │
    ▼
[6] Process.execute()
    Process runs on CPU for time quantum
    │
    ├─────────────────────────────────┐
    │                                 │
    ▼                                 ▼
[7a] Process completed            [7b] Time quantum expired
     │                                 │
     ▼                                 ▼
[8a] ProcessDispatcher.complete() [8b] ProcessDispatcher.preempt()
     [DISPATCHER]                      [DISPATCHER]
     │                                 │
     ▼                                 ▼
     TERMINATED                    READY (back to queue)
     │                                 │
     └─────────────────────────────────┘
                    │
                    ▼
[9] Statistics Calculation
    Turnaround time, Waiting time, etc.
    │
    ▼
[10] Repeat from [3] if more processes
     │
     ▼
END
```

## Verification / Xác minh

This simulation includes all required kernel components:
Mô phỏng này bao gồm tất cả các thành phần kernel yêu cầu:

✅ **DISPATCHER**: ProcessDispatcher class implements dispatcher functionality
✅ **SCHEDULER**: ProcessScheduler classes implement scheduler functionality  
✅ **Process Management**: ProcessRegistry manages process lifecycle
✅ **State Transitions**: All process states (NEW, READY, RUNNING, WAITING, TERMINATED)
✅ **Context Switching**: Implemented in ProcessDispatcher
✅ **Scheduling Algorithms**: Round Robin and Priority implemented
✅ **Multi-threading**: Two schedulers run concurrently
