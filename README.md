# Operating System Kernel Simulation
# Mô phỏng Kernel Hệ Điều Hành

## Project Overview / Tổng quan dự án

This project simulates the basic operations of an Operating System kernel, including:
Dự án này mô phỏng các hoạt động cơ bản của kernel hệ điều hành, bao gồm:
- **Dispatching**: Context switching and process dispatching / Chuyển đổi ngữ cảnh và điều phối tiến trình
- **Scheduling**: Round Robin and Priority scheduling algorithms / Lập lịch Round Robin và Priority
- **Process State Transitions**: NEW → READY → RUNNING → WAITING → TERMINATED / Chuyển đổi trạng thái tiến trình

## Project Structure / Cấu trúc dự án

```
OS_Simulation/
├── src/
│   ├── Process.java                 # Process class with states / Lớp tiến trình với các trạng thái
│   ├── Dispatcher.java              # Handles process dispatching / Xử lý điều phối tiến trình
│   ├── Scheduler.java               # Abstract scheduler class / Lớp scheduler trừu tượng
│   ├── RoundRobinScheduler.java     # Round Robin implementation / Triển khai Round Robin
│   ├── PriorityScheduler.java       # Priority scheduling implementation / Triển khai lập lịch Priority
│   ├── ProcessManager.java          # Manages process lifecycle / Quản lý vòng đời tiến trình
│   ├── SchedulingThread.java        # Thread wrapper for schedulers / Wrapper thread cho scheduler
│   └── OSSimulationMain.java        # Main entry point / Điểm vào chính
├── README.md                        # This file / File này
├── SOLARIS_DESCRIPTION.md          # SOLARIS process execution description / Mô tả thực thi tiến trình SOLARIS
└── CLASS_DIAGRAM.md                # Class diagram documentation / Tài liệu sơ đồ lớp
```

## Features / Tính năng

### 1. Process Management / Quản lý tiến trình
- Process creation with unique IDs / Tạo tiến trình với ID duy nhất
- Process states: NEW, READY, RUNNING, WAITING, TERMINATED / Các trạng thái tiến trình
- Priority levels (1 = highest, 10 = lowest) / Mức độ ưu tiên (1 = cao nhất, 10 = thấp nhất)
- Burst time and remaining time tracking / Theo dõi thời gian burst và thời gian còn lại

### 2. Scheduling Algorithms / Thuật toán lập lịch

#### Round Robin Scheduling / Lập lịch Round Robin
- Each process gets a fixed time quantum / Mỗi tiến trình nhận time quantum cố định
- Fair CPU time distribution / Phân phối thời gian CPU công bằng
- Preemption after time quantum expires / Preemption sau khi time quantum hết

#### Priority Scheduling / Lập lịch Priority
- Higher priority processes (lower number) scheduled first / Tiến trình priority cao hơn được lập lịch trước
- Preemption when higher priority process arrives / Preemption khi tiến trình priority cao hơn đến
- Time quantum for each process / Time quantum cho mỗi tiến trình

### 3. Dispatching / Điều phối
- Context switching between processes / Chuyển đổi ngữ cảnh giữa các tiến trình
- State transitions (READY → RUNNING) / Chuyển đổi trạng thái
- Process preemption handling / Xử lý preemption tiến trình
- Completion handling / Xử lý hoàn thành

### 4. Multi-threading / Đa luồng
- Two separate threads run simultaneously: / Hai thread riêng biệt chạy đồng thời:
  - Thread 1: Round Robin Scheduler
  - Thread 2: Priority Scheduler
- Each thread operates on independent process copies / Mỗi thread hoạt động trên bản sao tiến trình độc lập
- Demonstrates concurrent scheduling execution / Minh họa thực thi lập lịch đồng thời

## How to Compile and Run / Cách biên dịch và chạy

### Prerequisites / Yêu cầu
- Java Development Kit (JDK) 8 or higher / JDK 8 trở lên

### Compilation / Biên dịch
```bash
cd OS_Simulation/src
javac *.java
```

### Execution / Chạy chương trình
```bash
java OSSimulationMain
```

## Output / Kết quả

The program will display: / Chương trình sẽ hiển thị:
1. List of all processes with their properties / Danh sách tất cả tiến trình với các thuộc tính
2. Round Robin scheduling execution (from Thread 1) / Thực thi lập lịch Round Robin (từ Thread 1)
3. Priority scheduling execution (from Thread 2) / Thực thi lập lịch Priority (từ Thread 2)
4. Statistics for each scheduler: / Thống kê cho mỗi scheduler:
   - Completion time for each process / Thời gian hoàn thành cho mỗi tiến trình
   - Turnaround time / Thời gian turnaround
   - Waiting time / Thời gian chờ
   - Average turnaround time / Thời gian turnaround trung bình
   - Average waiting time / Thời gian chờ trung bình
5. Comparison between both scheduling algorithms / So sánh giữa hai thuật toán lập lịch

## Process States / Trạng thái tiến trình

- **NEW**: Process is being created / Tiến trình đang được tạo
- **READY**: Process is ready to run, waiting in ready queue / Tiến trình sẵn sàng chạy, đợi trong hàng đợi sẵn sàng
- **RUNNING**: Process is currently executing on CPU / Tiến trình đang thực thi trên CPU
- **WAITING**: Process is waiting for I/O or event (not fully implemented in this simulation) / Tiến trình đang chờ I/O hoặc sự kiện (chưa được triển khai đầy đủ trong mô phỏng này)
- **TERMINATED**: Process has completed execution / Tiến trình đã hoàn thành thực thi

## Scheduling Flow / Luồng lập lịch

1. **Process Creation**: Processes are created with arrival times, priorities, and burst times / **Tạo tiến trình**: Tiến trình được tạo với thời gian đến, độ ưu tiên và thời gian burst
2. **Arrival**: Processes arrive at their specified arrival times / **Đến**: Tiến trình đến tại thời gian đến được chỉ định
3. **Selection**: Scheduler selects next process based on algorithm (Round Robin or Priority) / **Lựa chọn**: Scheduler chọn tiến trình tiếp theo dựa trên thuật toán (Round Robin hoặc Priority)
4. **Dispatching**: Dispatcher transitions process from READY to RUNNING / **Điều phối**: Dispatcher chuyển tiến trình từ READY sang RUNNING
5. **Execution**: Process executes for time quantum or until completion / **Thực thi**: Tiến trình thực thi trong time quantum hoặc cho đến khi hoàn thành
6. **Preemption/Completion**: Process is preempted or completes, returns to queue or terminates / **Preemption/Hoàn thành**: Tiến trình bị preempt hoặc hoàn thành, trả về hàng đợi hoặc kết thúc
7. **Statistics**: Calculate turnaround time, waiting time, and averages / **Thống kê**: Tính toán turnaround time, waiting time và các giá trị trung bình

## Example Output

```
========================================
   Operating System Kernel Simulation
========================================

========== All Processes ==========
Process ID | Process Name | Priority | Burst Time | Arrival Time
-------------------------------------------------------------------
         1 |           P1 |        3 |         10 |            0
         2 |           P2 |        1 |          5 |            1
         3 |           P3 |        4 |          8 |            2
...

[RoundRobin-Thread] Thread started at: 10:30:45.123
[Priority-Thread] Thread started at: 10:30:45.125

========== Round Robin Scheduler Scheduling Started ==========
[Dispatcher] Time 0: Dispatching Process 1 (P1) to CPU
...
```

## Author / Tác giả

Operating Systems Final Project / Dự án cuối kỳ Hệ Điều Hành

