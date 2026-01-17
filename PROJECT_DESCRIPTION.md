# Operating System Kernel Simulation - Project Description

## Mục đích dự án

Dự án này mô phỏng các hoạt động cơ bản của kernel hệ điều hành, bao gồm:
- **Dispatching**: Chuyển đổi ngữ cảnh và điều phối tiến trình
- **Scheduling**: Lập lịch tiến trình sử dụng Round Robin và Priority
- **State Transitions**: Quản lý các trạng thái của tiến trình

## Các thành phần chính

### 1. Process (Tiến trình)
- Quản lý trạng thái: NEW, READY, RUNNING, WAITING, TERMINATED
- Theo dõi thời gian burst, thời gian còn lại
- Quản lý priority và thời gian chờ

### 2. Dispatcher (Bộ điều phối)
- Thực hiện context switching
- Chuyển đổi trạng thái READY → RUNNING
- Xử lý preemption và completion

### 3. Scheduler (Bộ lập lịch)
- **RoundRobinScheduler**: Lập lịch theo vòng tròn, mỗi tiến trình nhận time quantum cố định
- **PriorityScheduler**: Lập lịch theo độ ưu tiên, tiến trình có priority cao hơn được chạy trước

### 4. ProcessManager (Quản lý tiến trình)
- Tạo và quản lý vòng đời tiến trình
- Tạo bản sao tiến trình cho các scheduler độc lập

### 5. Multi-threading
- **SchedulingThread**: Thread wrapper để chạy scheduler
- Hai thread chạy đồng thời:
  - Thread 1: Round Robin Scheduler
  - Thread 2: Priority Scheduler

## Cách sử dụng

### Biên dịch:
```bash
compile.bat
```

### Chạy chương trình:
```bash
run.bat
```

Hoặc:
```bash
compile_and_run.bat
```

## Kết quả mong đợi

Chương trình sẽ hiển thị:
1. Danh sách tất cả các tiến trình
2. Quá trình lập lịch Round Robin (từ Thread 1)
3. Quá trình lập lịch Priority (từ Thread 2)
4. Thống kê cho mỗi scheduler:
   - Thời gian hoàn thành
   - Turnaround time
   - Waiting time
   - Trung bình turnaround time
   - Trung bình waiting time
5. So sánh giữa hai thuật toán lập lịch

## Tài liệu kèm theo

1. **README.md**: Hướng dẫn tổng quan về dự án
2. **SOLARIS_DESCRIPTION.md**: Mô tả chi tiết về quá trình thực thi tiến trình và thread trong SOLARIS trên hệ thống đa lõi
3. **CLASS_DIAGRAM.md**: Sơ đồ lớp UML và mô tả các mối quan hệ

## Yêu cầu hệ thống

- Java Development Kit (JDK) 8 trở lên
- Hệ điều hành: Windows, Linux, hoặc macOS

## Cấu trúc mã nguồn

```
src/
├── Process.java                 # Lớp tiến trình
├── Dispatcher.java              # Bộ điều phối
├── Scheduler.java               # Lớp trừu tượng scheduler
├── RoundRobinScheduler.java     # Round Robin scheduler
├── PriorityScheduler.java       # Priority scheduler
├── ProcessManager.java          # Quản lý tiến trình
├── SchedulingThread.java        # Thread wrapper
└── OSSimulationMain.java        # Main class
```

## Tính năng nổi bật

✅ Mô phỏng đầy đủ các trạng thái tiến trình  
✅ Hai thuật toán lập lịch: Round Robin và Priority  
✅ Context switching và dispatching  
✅ Multi-threading với 2 thread chạy đồng thời  
✅ Thống kê chi tiết về hiệu suất  
✅ Tài liệu mô tả SOLARIS process execution  
✅ Sơ đồ lớp UML  

## Lưu ý

- Mỗi scheduler hoạt động trên bản sao độc lập của danh sách tiến trình
- Time quantum mặc định là 3 đơn vị thời gian
- Priority: số nhỏ hơn = độ ưu tiên cao hơn (1 = cao nhất, 10 = thấp nhất)

