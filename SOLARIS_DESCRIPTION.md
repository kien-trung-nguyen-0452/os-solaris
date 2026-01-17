# Process Execution in SOLARIS on Multicore Systems
# Thực thi Tiến trình trong SOLARIS trên Hệ thống Đa lõi

## Overview / Tổng quan

SOLARIS (now Oracle Solaris) is a Unix-based operating system that supports advanced process and thread management, especially on multicore and multiprocessor systems. This document describes in detail how SOLARIS handles process and thread execution in a multicore environment.

SOLARIS (hiện tại là Oracle Solaris) là một hệ điều hành dựa trên Unix hỗ trợ quản lý tiến trình và thread tiên tiến, đặc biệt trên các hệ thống đa lõi và đa bộ xử lý. Tài liệu này mô tả chi tiết cách SOLARIS xử lý thực thi tiến trình và thread trong môi trường đa lõi.

## SOLARIS Process and Thread Model / Mô hình Tiến trình và Thread trong SOLARIS

### 1. Process Structure / Cấu trúc Tiến trình

In SOLARIS, a process is a container that includes:
Trong SOLARIS, một tiến trình là một container bao gồm:
- **Address Space**: Virtual memory space unique to the process / **Không gian địa chỉ**: Không gian bộ nhớ ảo duy nhất cho tiến trình
- **File Descriptors**: Open files and I/O resources / **File Descriptors**: Các file đang mở và tài nguyên I/O
- **Process ID (PID)**: Unique identifier / **Process ID (PID)**: Định danh duy nhất
- **Lightweight Processes (LWPs)**: Kernel-level threads / **Lightweight Processes (LWPs)**: Thread ở mức kernel
- **User Threads**: Application-level threads managed by thread library / **User Threads**: Thread ở mức ứng dụng được quản lý bởi thư viện thread

### 2. Thread Model: M:N Model / Mô hình Thread: M:N

SOLARIS uses a **M:N threading model** (also called hybrid model):
SOLARIS sử dụng **mô hình thread M:N** (còn gọi là mô hình hybrid):
- **M User Threads** map to **N Kernel Threads (LWPs)** / **M User Threads** ánh xạ tới **N Kernel Threads (LWPs)**
- User threads are managed by the thread library (libthread) / User threads được quản lý bởi thư viện thread (libthread)
- LWPs are managed by the kernel / LWPs được quản lý bởi kernel
- This allows efficient thread management with less kernel overhead / Điều này cho phép quản lý thread hiệu quả với ít overhead kernel hơn

## Process Execution Flow in Multicore Systems / Luồng Thực thi Tiến trình trong Hệ thống Đa lõi

### Step 1: Process Creation / Bước 1: Tạo Tiến trình

When a program creates a process using `fork()` or `exec()`:
Khi một chương trình tạo tiến trình bằng `fork()` hoặc `exec()`:

1. **Kernel allocates resources** / **Kernel cấp phát tài nguyên**:
   - New Process Control Block (PCB) in kernel memory / Process Control Block (PCB) mới trong bộ nhớ kernel
   - Unique Process ID (PID) / Process ID (PID) duy nhất
   - Virtual address space / Không gian địa chỉ ảo
   - File descriptor table / Bảng file descriptor
   - Signal handlers / Các xử lý tín hiệu

2. **Initial LWP creation** / **Tạo LWP ban đầu**:
   - Kernel creates the first Lightweight Process (LWP) / Kernel tạo Lightweight Process (LWP) đầu tiên
   - LWP is bound to a kernel thread / LWP được gắn với một kernel thread
   - This LWP becomes the main execution thread / LWP này trở thành thread thực thi chính

3. **Memory space setup** / **Thiết lập không gian bộ nhớ**:
   - Virtual memory pages are allocated / Các trang bộ nhớ ảo được cấp phát
   - Code, data, and stack segments are mapped / Các đoạn code, data và stack được ánh xạ
   - Shared libraries are loaded / Các thư viện dùng chung được tải

### Step 2: Thread Creation / Bước 2: Tạo Thread

When a process creates threads using `pthread_create()`:
Khi một tiến trình tạo threads bằng `pthread_create()`:

1. **User Thread Creation** / **Tạo User Thread**:
   - Thread library creates a user-level thread structure / Thư viện thread tạo cấu trúc thread ở mức người dùng
   - Thread is added to the process's thread pool / Thread được thêm vào pool thread của tiến trình
   - Initially, thread may not have an associated LWP / Ban đầu, thread có thể chưa có LWP liên kết

2. **LWP Allocation** / **Cấp phát LWP**:
   - If an LWP is available in the pool, it's assigned to the thread / Nếu có LWP sẵn có trong pool, nó được gán cho thread
   - If no LWP is available, kernel may create a new LWP (up to limits) / Nếu không có LWP sẵn có, kernel có thể tạo LWP mới (tối đa giới hạn)
   - LWP is bound to a kernel thread / LWP được gắn với kernel thread

3. **Kernel Thread Binding** / **Gắn Kernel Thread**:
   - Kernel thread is scheduled by the kernel scheduler / Kernel thread được lập lịch bởi kernel scheduler
   - On multicore systems, kernel thread can run on any available CPU core / Trên hệ thống đa lõi, kernel thread có thể chạy trên bất kỳ lõi CPU nào có sẵn
   - CPU affinity can be set to bind thread to specific cores / CPU affinity có thể được thiết lập để gắn thread với các lõi cụ thể

### Step 3: Scheduling on Multicore Systems / Bước 3: Lập lịch trên Hệ thống Đa lõi

SOLARIS uses a **per-CPU run queue** model:
SOLARIS sử dụng mô hình **per-CPU run queue**:

1. **CPU Run Queues** / **Hàng đợi Chạy CPU**:
   - Each CPU core has its own run queue / Mỗi lõi CPU có hàng đợi chạy riêng
   - Contains kernel threads ready to execute / Chứa các kernel threads sẵn sàng thực thi
   - Reduces lock contention compared to single global queue / Giảm tranh chấp khóa so với hàng đợi toàn cục đơn

2. **Load Balancing** / **Cân bằng Tải**:
   - Kernel periodically checks CPU load / Kernel định kỳ kiểm tra tải CPU
   - If one CPU is overloaded, threads can be migrated to less loaded CPUs / Nếu một CPU bị quá tải, threads có thể được di chuyển đến các CPU ít tải hơn
   - Migration happens at kernel thread level / Di chuyển xảy ra ở mức kernel thread

3. **Scheduling Classes** / **Lớp Lập lịch**:
   SOLARIS supports multiple scheduling classes:
   SOLARIS hỗ trợ nhiều lớp lập lịch:
   - **TS (Time Sharing)**: Default for most processes, uses time slices / Mặc định cho hầu hết tiến trình, sử dụng time slice
   - **RT (Real-Time)**: Fixed priority, preemptive / Độ ưu tiên cố định, preemptive
   - **SYS (System)**: For kernel threads / Cho kernel threads
   - **IA (Interactive)**: For interactive applications / Cho ứng dụng tương tác
   - **FSS (Fair Share)**: CPU shares allocation / Phân bổ chia sẻ CPU

4. **Priority Calculation** / **Tính toán Độ ưu tiên**:
   - Each thread has a global priority (0-169) / Mỗi thread có độ ưu tiên toàn cục (0-169)
   - Higher number = higher priority / Số cao hơn = độ ưu tiên cao hơn
   - Priority is recalculated based on: / Độ ưu tiên được tính lại dựa trên:
     - Base priority of scheduling class / Độ ưu tiên cơ bản của lớp lập lịch
     - CPU usage history / Lịch sử sử dụng CPU
     - Interactive boost / Tăng cường tương tác
     - Nice value / Giá trị nice

### Step 4: Context Switching / Bước 4: Chuyển đổi Ngữ cảnh

When a thread is scheduled to run:
Khi một thread được lập lịch để chạy:

1. **CPU Selection** / **Lựa chọn CPU**:
   - Kernel selects a CPU core based on: / Kernel chọn lõi CPU dựa trên:
     - CPU affinity settings / Cài đặt CPU affinity
     - Current CPU load / Tải CPU hiện tại
     - Cache locality (prefer same CPU for cache efficiency) / Vị trí cache (ưu tiên cùng CPU để hiệu quả cache)

2. **Context Save** / **Lưu Ngữ cảnh**:
   - Current thread's context is saved: / Ngữ cảnh của thread hiện tại được lưu:
     - CPU registers (PC, SP, general registers) / Các thanh ghi CPU (PC, SP, thanh ghi chung)
     - Floating-point registers / Các thanh ghi dấu phẩy động
     - Memory management registers / Các thanh ghi quản lý bộ nhớ
     - Thread state (RUNNING → READY) / Trạng thái thread (RUNNING → READY)

3. **Context Restore** / **Khôi phục Ngữ cảnh**:
   - New thread's context is restored: / Ngữ cảnh của thread mới được khôi phục:
     - Registers are loaded / Các thanh ghi được tải
     - Memory management unit (MMU) is updated / Memory management unit (MMU) được cập nhật
     - Thread state (READY → RUNNING) / Trạng thái thread (READY → RUNNING)

4. **Cache Effects** / **Ảnh hưởng Cache**:
   - On multicore systems, cache misses may occur when thread migrates / Trên hệ thống đa lõi, cache miss có thể xảy ra khi thread di chuyển
   - SOLARIS tries to maintain cache affinity when possible / SOLARIS cố gắng duy trì cache affinity khi có thể

### Step 5: Execution on CPU Core / Bước 5: Thực thi trên Lõi CPU

1. **Instruction Execution** / **Thực thi Lệnh**:
   - CPU executes instructions from the thread / CPU thực thi các lệnh từ thread
   - Instructions are fetched from memory (or cache) / Các lệnh được lấy từ bộ nhớ (hoặc cache)
   - Data is loaded/stored as needed / Dữ liệu được tải/lưu khi cần

2. **Time Slice Management** / **Quản lý Time Slice**:
   - Thread runs for its allocated time quantum / Thread chạy trong time quantum được cấp phát
   - Timer interrupt fires when quantum expires / Ngắt timer kích hoạt khi quantum hết
   - Kernel checks if thread should be preempted / Kernel kiểm tra xem thread có nên bị preempt không

3. **Preemption** / **Preemption**:
   - Thread can be preempted if: / Thread có thể bị preempt nếu:
     - Time quantum expires / Time quantum hết
     - Higher priority thread becomes ready / Thread độ ưu tiên cao hơn trở nên sẵn sàng
     - Thread blocks (I/O, synchronization) / Thread bị chặn (I/O, đồng bộ hóa)
   - Preemption is handled by kernel interrupt handler / Preemption được xử lý bởi kernel interrupt handler

### Step 6: Synchronization and Blocking / Bước 6: Đồng bộ hóa và Chặn

When threads need to synchronize:
Khi threads cần đồng bộ hóa:

1. **Mutex/Semaphore Operations** / **Thao tác Mutex/Semaphore**:
   - Thread attempts to acquire lock / Thread cố gắng lấy khóa
   - If lock is unavailable, thread blocks / Nếu khóa không có sẵn, thread bị chặn
   - LWP is descheduled, kernel thread moves to sleep queue / LWP bị hủy lập lịch, kernel thread chuyển sang hàng đợi sleep
   - When lock is released, thread is woken and moved to run queue / Khi khóa được giải phóng, thread được đánh thức và chuyển sang hàng đợi chạy

2. **I/O Operations** / **Thao tác I/O**:
   - Thread issues I/O request (read/write) / Thread phát yêu cầu I/O (đọc/ghi)
   - Thread blocks waiting for I/O completion / Thread bị chặn chờ hoàn thành I/O
   - LWP is descheduled / LWP bị hủy lập lịch
   - When I/O completes, interrupt handler wakes the thread / Khi I/O hoàn thành, interrupt handler đánh thức thread

3. **Condition Variables** / **Biến Điều kiện**:
   - Thread waits on condition variable / Thread chờ trên biến điều kiện
   - Thread blocks until condition is signaled / Thread bị chặn cho đến khi điều kiện được báo hiệu
   - Other threads can continue execution / Các thread khác có thể tiếp tục thực thi

### Step 7: Multicore Considerations / Bước 7: Cân nhắc Đa lõi

1. **Cache Coherency** / **Đồng nhất Cache**:
   - Multiple cores share main memory / Nhiều lõi chia sẻ bộ nhớ chính
   - Cache coherency protocol ensures data consistency / Giao thức đồng nhất cache đảm bảo tính nhất quán dữ liệu
   - Modified cache lines are synchronized across cores / Các dòng cache đã sửa đổi được đồng bộ hóa giữa các lõi

2. **Memory Barriers** / **Rào cản Bộ nhớ**:
   - SOLARIS uses memory barriers to ensure proper ordering / SOLARIS sử dụng rào cản bộ nhớ để đảm bảo thứ tự đúng
   - Prevents race conditions in multicore execution / Ngăn chặn điều kiện đua trong thực thi đa lõi
   - Critical for synchronization primitives / Quan trọng cho các nguyên thủy đồng bộ hóa

3. **NUMA (Non-Uniform Memory Access)** / **NUMA (Truy cập Bộ nhớ Không đồng nhất)**:
   - On NUMA systems, memory access time varies by CPU / Trên hệ thống NUMA, thời gian truy cập bộ nhớ thay đổi theo CPU
   - SOLARIS tries to allocate memory from local NUMA node / SOLARIS cố gắng cấp phát bộ nhớ từ nút NUMA cục bộ
   - Thread migration considers NUMA topology / Di chuyển thread xem xét cấu trúc NUMA

4. **CPU Affinity** / **CPU Affinity**:
   - Threads can be bound to specific CPU cores / Threads có thể được gắn với các lõi CPU cụ thể
   - Improves cache locality / Cải thiện vị trí cache
   - Can be set using `pthread_setaffinity_np()` or `processor_bind()` / Có thể được thiết lập bằng `pthread_setaffinity_np()` hoặc `processor_bind()`

## Example: Program with Multiple Processes and Threads / Ví dụ: Chương trình với Nhiều Tiến trình và Threads

Consider a program that:
Xem xét một chương trình:
- Creates 3 processes / Tạo 3 tiến trình
- Each process creates 4 threads / Mỗi tiến trình tạo 4 threads
- Runs on a 4-core system / Chạy trên hệ thống 4 lõi

### Execution Flow / Luồng Thực thi:

1. **Process Creation** / **Tạo Tiến trình**:
   ```
   Main Program → fork() → Process 1 (PID 1001)
                  fork() → Process 2 (PID 1002)
                  fork() → Process 3 (PID 1003)
   ```

2. **Thread Creation in Each Process** / **Tạo Thread trong Mỗi Tiến trình**:
   ```
   Process 1 (PID 1001):
     - pthread_create() → User Thread 1 → LWP 1 → Kernel Thread 1
     - pthread_create() → User Thread 2 → LWP 2 → Kernel Thread 2
     - pthread_create() → User Thread 3 → LWP 3 → Kernel Thread 3
     - pthread_create() → User Thread 4 → LWP 4 → Kernel Thread 4
   ```

3. **Kernel Scheduling** / **Lập lịch Kernel**:
   - 12 kernel threads total (3 processes × 4 threads) / Tổng cộng 12 kernel threads (3 tiến trình × 4 threads)
   - 4 CPU cores available / 4 lõi CPU có sẵn
   - Kernel distributes threads across cores: / Kernel phân phối threads trên các lõi:
     ```
     Core 0: Kernel Thread 1 (P1), Kernel Thread 5 (P2), Kernel Thread 9 (P3)
     Core 1: Kernel Thread 2 (P1), Kernel Thread 6 (P2), Kernel Thread 10 (P3)
     Core 2: Kernel Thread 3 (P1), Kernel Thread 7 (P2), Kernel Thread 11 (P3)
     Core 3: Kernel Thread 4 (P1), Kernel Thread 8 (P2), Kernel Thread 12 (P3)
     ```

4. **Time Slicing** / **Chia Time Slice**:
   - Each thread gets time slice (typically 10-100ms) / Mỗi thread nhận time slice (thường 10-100ms)
   - Threads are preempted and rescheduled / Threads bị preempt và lập lịch lại
   - Load balancer may migrate threads between cores / Bộ cân bằng tải có thể di chuyển threads giữa các lõi

5. **Synchronization** / **Đồng bộ hóa**:
   - Threads may use mutexes, semaphores, or condition variables / Threads có thể sử dụng mutexes, semaphores hoặc biến điều kiện
   - Blocked threads are descheduled / Threads bị chặn bị hủy lập lịch
   - Woken threads are added to run queue / Threads được đánh thức được thêm vào hàng đợi chạy

## Key SOLARIS Features for Multicore / Tính năng SOLARIS Chính cho Đa lõi

1. **Processor Sets**: Groups of CPUs can be assigned to specific processes / **Processor Sets**: Nhóm CPU có thể được gán cho các tiến trình cụ thể
2. **Resource Pools**: CPU and memory resources can be partitioned / **Resource Pools**: Tài nguyên CPU và bộ nhớ có thể được phân vùng
3. **Dynamic Reconfiguration**: CPUs can be added/removed without reboot / **Dynamic Reconfiguration**: CPU có thể được thêm/xóa mà không cần khởi động lại
4. **Observability**: Tools like `prstat`, `mpstat`, `dtrace` for monitoring / **Observability**: Các công cụ như `prstat`, `mpstat`, `dtrace` để giám sát
5. **Scalability**: Efficient locking mechanisms for multicore systems / **Scalability**: Cơ chế khóa hiệu quả cho hệ thống đa lõi

## Summary / Tóm tắt

SOLARIS provides sophisticated process and thread management for multicore systems through:
SOLARIS cung cấp quản lý tiến trình và thread tinh vi cho hệ thống đa lõi thông qua:
- M:N threading model for efficiency / Mô hình thread M:N để hiệu quả
- Per-CPU run queues for scalability / Hàng đợi chạy per-CPU để mở rộng
- Multiple scheduling classes for different workloads / Nhiều lớp lập lịch cho các workload khác nhau
- NUMA awareness for optimal memory access / Nhận thức NUMA để truy cập bộ nhớ tối ưu
- Advanced synchronization primitives / Các nguyên thủy đồng bộ hóa tiên tiến
- Load balancing across CPU cores / Cân bằng tải trên các lõi CPU

This architecture allows SOLARIS to efficiently utilize all available CPU cores while maintaining fairness, responsiveness, and system stability.

Kiến trúc này cho phép SOLARIS sử dụng hiệu quả tất cả các lõi CPU có sẵn trong khi duy trì tính công bằng, khả năng phản hồi và ổn định hệ thống.
