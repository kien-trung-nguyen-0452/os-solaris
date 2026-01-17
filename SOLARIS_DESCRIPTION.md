# Mô tả Quá trình Thực thi Tiến trình trong SOLARIS trên Hệ thống Đa lõi

## Giới thiệu

SOLARIS (hiện được Oracle phát triển) là một hệ điều hành dựa trên nền tảng Unix, được thiết kế đặc biệt để tận dụng tối đa khả năng của các hệ thống máy tính đa lõi và đa bộ xử lý. Tài liệu này trình bày chi tiết cơ chế mà SOLARIS sử dụng để quản lý và thực thi các tiến trình cùng với các luồng (threads) của chúng trong môi trường đa lõi.

## Kiến trúc Quản lý Tiến trình và Luồng trong SOLARIS

### Cấu trúc của một Tiến trình

Mỗi tiến trình trong SOLARIS được tổ chức như một đơn vị độc lập bao gồm các thành phần sau:

- **Không gian địa chỉ ảo**: Mỗi tiến trình sở hữu một không gian bộ nhớ ảo riêng biệt, được bảo vệ khỏi các tiến trình khác
- **Bảng mô tả file**: Quản lý các file đang mở và các tài nguyên nhập/xuất
- **Định danh tiến trình (PID)**: Mỗi tiến trình có một số định danh duy nhất
- **Lightweight Processes (LWPs)**: Đây là các luồng ở tầng kernel, đóng vai trò cầu nối giữa user threads và kernel
- **User Threads**: Các luồng ở tầng ứng dụng, được quản lý bởi thư viện thread của hệ thống

### Mô hình Luồng M:N

SOLARIS áp dụng mô hình luồng M:N (còn được gọi là mô hình lai):

- **M User Threads** được ánh xạ tới **N Kernel Threads (LWPs)**, trong đó M thường lớn hơn N
- Các user threads được quản lý bởi thư viện `libthread` ở không gian người dùng
- Các LWPs được quản lý trực tiếp bởi kernel của hệ điều hành
- Mô hình này giúp giảm thiểu chi phí chuyển đổi ngữ cảnh và tăng hiệu quả quản lý luồng

## Chi tiết Quá trình Thực thi trên Hệ thống Đa lõi

### Giai đoạn 1: Khởi tạo Tiến trình

Khi một chương trình tạo tiến trình mới thông qua các lời gọi hệ thống như `fork()` hoặc `exec()`:

**Cấp phát tài nguyên bởi Kernel:**
- Kernel tạo một Process Control Block (PCB) mới trong bộ nhớ kernel để lưu trữ thông tin quản lý tiến trình
- Gán một Process ID (PID) duy nhất cho tiến trình mới
- Thiết lập không gian địa chỉ ảo riêng cho tiến trình
- Khởi tạo bảng mô tả file và các xử lý tín hiệu

**Tạo Lightweight Process đầu tiên:**
- Kernel tự động tạo LWP đầu tiên cho tiến trình mới
- LWP này được liên kết với một kernel thread tương ứng
- LWP đầu tiên này sẽ trở thành luồng thực thi chính của tiến trình

**Thiết lập không gian bộ nhớ:**
- Các trang bộ nhớ ảo được cấp phát và ánh xạ
- Các đoạn mã chương trình, dữ liệu và ngăn xếp được ánh xạ vào không gian địa chỉ ảo
- Các thư viện dùng chung được nạp vào bộ nhớ

### Giai đoạn 2: Tạo các Luồng

Khi tiến trình tạo các luồng mới bằng hàm `pthread_create()`:

**Tạo User Thread:**
- Thư viện thread tạo một cấu trúc dữ liệu để đại diện cho luồng ở tầng người dùng
- Luồng mới được thêm vào nhóm luồng của tiến trình
- Ban đầu, luồng có thể chưa được gán một LWP cụ thể

**Phân bổ LWP:**
- Nếu có LWP sẵn có trong pool của tiến trình, nó sẽ được gán cho luồng mới
- Trong trường hợp không có LWP sẵn có, kernel có thể tạo LWP mới (tùy thuộc vào giới hạn hệ thống)
- Mỗi LWP được liên kết với một kernel thread tương ứng

**Liên kết với Kernel Thread:**
- Kernel thread được lập lịch bởi bộ lập lịch của kernel
- Trên hệ thống đa lõi, kernel thread có thể được thực thi trên bất kỳ lõi CPU nào đang rảnh
- Có thể thiết lập CPU affinity để gắn luồng với các lõi CPU cụ thể nhằm tối ưu hiệu năng

### Giai đoạn 3: Cơ chế Lập lịch trên Hệ thống Đa lõi

SOLARIS sử dụng mô hình **hàng đợi chạy riêng cho mỗi CPU**:

**Hàng đợi chạy của từng CPU:**
- Mỗi lõi CPU duy trì một hàng đợi chạy riêng của nó
- Hàng đợi này chứa các kernel threads đang sẵn sàng để thực thi
- Mô hình này giúp giảm thiểu xung đột khóa so với việc sử dụng một hàng đợi toàn cục duy nhất

**Cân bằng tải:**
- Kernel định kỳ đánh giá tải của từng CPU
- Khi phát hiện một CPU bị quá tải, kernel có thể di chuyển các kernel threads sang các CPU ít tải hơn
- Quá trình di chuyển này diễn ra ở mức kernel thread

**Các lớp Lập lịch:**
SOLARIS hỗ trợ nhiều lớp lập lịch khác nhau để phù hợp với các loại tác vụ:
- **TS (Time Sharing)**: Lớp mặc định cho hầu hết các tiến trình, sử dụng chia sẻ thời gian
- **RT (Real-Time)**: Dành cho các ứng dụng thời gian thực, có độ ưu tiên cố định và có thể ngắt
- **SYS (System)**: Dành riêng cho các kernel threads
- **IA (Interactive)**: Tối ưu cho các ứng dụng tương tác với người dùng
- **FSS (Fair Share)**: Phân bổ CPU theo cơ chế chia sẻ công bằng

**Tính toán Độ ưu tiên:**
- Mỗi luồng có một độ ưu tiên toàn cục trong phạm vi từ 0 đến 169
- Trong SOLARIS, số độ ưu tiên cao hơn tương ứng với độ ưu tiên cao hơn
- Độ ưu tiên được tính toán lại dựa trên nhiều yếu tố:
  - Độ ưu tiên cơ bản của lớp lập lịch
  - Lịch sử sử dụng CPU của luồng
  - Tăng cường độ ưu tiên cho các ứng dụng tương tác
  - Giá trị nice của tiến trình

### Giai đoạn 4: Chuyển đổi Ngữ cảnh

Khi một luồng được lập lịch để thực thi:

**Lựa chọn CPU:**
- Kernel chọn lõi CPU dựa trên các yếu tố:
  - Cài đặt CPU affinity (nếu có)
  - Tải hiện tại của các CPU
  - Vị trí cache (ưu tiên cùng CPU để tận dụng cache)

**Lưu trữ Ngữ cảnh:**
- Ngữ cảnh của luồng hiện tại được lưu lại:
  - Các thanh ghi CPU (Program Counter, Stack Pointer, các thanh ghi chung)
  - Các thanh ghi dấu phẩy động
  - Các thanh ghi quản lý bộ nhớ
  - Trạng thái luồng chuyển từ RUNNING sang READY

**Khôi phục Ngữ cảnh:**
- Ngữ cảnh của luồng mới được khôi phục:
  - Các thanh ghi được nạp lại
  - Memory Management Unit (MMU) được cập nhật
  - Trạng thái luồng chuyển từ READY sang RUNNING

**Tác động của Cache:**
- Trên hệ thống đa lõi, việc di chuyển luồng giữa các lõi có thể gây ra cache miss
- SOLARIS cố gắng duy trì cache affinity khi có thể để tối ưu hiệu năng

### Giai đoạn 5: Thực thi trên Lõi CPU

**Thực thi Lệnh:**
- CPU thực thi các lệnh từ luồng được lập lịch
- Các lệnh được lấy từ bộ nhớ hoặc cache
- Dữ liệu được tải và lưu trữ khi cần thiết

**Quản lý Time Slice:**
- Mỗi luồng được cấp phát một lượng thời gian (time quantum) để thực thi
- Khi time quantum hết, ngắt timer được kích hoạt
- Kernel kiểm tra xem luồng có nên bị ngắt để nhường CPU cho luồng khác không

**Cơ chế Ngắt:**
- Một luồng có thể bị ngắt trong các trường hợp:
  - Time quantum đã hết
  - Có luồng có độ ưu tiên cao hơn trở nên sẵn sàng
  - Luồng bị chặn do chờ I/O hoặc đồng bộ hóa
- Việc ngắt được xử lý bởi trình xử lý ngắt của kernel

### Giai đoạn 6: Đồng bộ hóa và Chặn Luồng

Khi các luồng cần đồng bộ hóa với nhau:

**Thao tác Mutex/Semaphore:**
- Khi một luồng cố gắng lấy khóa (lock) nhưng khóa đang bị chiếm bởi luồng khác, luồng đó sẽ bị chặn
- LWP của luồng bị chặn sẽ không được lập lịch, và kernel thread tương ứng được chuyển sang hàng đợi sleep
- Khi khóa được giải phóng, luồng đang chờ sẽ được đánh thức và chuyển trở lại hàng đợi chạy

**Thao tác Nhập/Xuất:**
- Khi một luồng thực hiện yêu cầu I/O (đọc hoặc ghi), luồng sẽ bị chặn cho đến khi thao tác I/O hoàn thành
- LWP của luồng bị chặn sẽ không được lập lịch
- Khi thao tác I/O hoàn thành, trình xử lý ngắt sẽ đánh thức luồng đang chờ

**Biến Điều kiện:**
- Các luồng có thể chờ trên biến điều kiện cho đến khi điều kiện được thỏa mãn
- Luồng bị chặn sẽ không chiếm CPU trong khi chờ
- Các luồng khác có thể tiếp tục thực thi bình thường

### Giai đoạn 7: Các Vấn đề Đặc biệt của Hệ thống Đa lõi

**Đồng nhất Cache:**
- Trên hệ thống đa lõi, nhiều lõi chia sẻ cùng một bộ nhớ chính
- Giao thức đồng nhất cache đảm bảo tính nhất quán dữ liệu giữa các cache của các lõi
- Khi một lõi sửa đổi dữ liệu trong cache của nó, các dòng cache đã sửa đổi sẽ được đồng bộ hóa với các lõi khác

**Rào cản Bộ nhớ:**
- SOLARIS sử dụng rào cản bộ nhớ để đảm bảo thứ tự đúng của các thao tác bộ nhớ
- Điều này rất quan trọng để ngăn chặn các điều kiện đua trong môi trường thực thi đa lõi
- Rào cản bộ nhớ đặc biệt quan trọng cho các nguyên thủy đồng bộ hóa

**NUMA (Non-Uniform Memory Access):**
- Trên các hệ thống NUMA, thời gian truy cập bộ nhớ khác nhau tùy thuộc vào vị trí của CPU
- SOLARIS cố gắng cấp phát bộ nhớ từ nút NUMA cục bộ của CPU đang chạy luồng
- Khi di chuyển luồng giữa các CPU, kernel cũng xem xét cấu trúc NUMA để tối ưu hiệu năng

**CPU Affinity:**
- Có thể gắn các luồng với các lõi CPU cụ thể để cải thiện hiệu năng
- Điều này giúp tận dụng tốt hơn cache cục bộ của CPU
- Có thể thiết lập CPU affinity bằng các hàm như `pthread_setaffinity_np()` hoặc `processor_bind()`

## Ví dụ Cụ thể: Chương trình với Nhiều Tiến trình và Luồng

Xét một chương trình có các đặc điểm sau:
- Tạo ra 3 tiến trình độc lập
- Mỗi tiến trình tạo 4 luồng
- Chạy trên hệ thống có 4 lõi CPU

### Luồng Thực thi:

**1. Tạo Tiến trình:**
```
Chương trình chính → fork() → Tiến trình 1 (PID 1001)
                → fork() → Tiến trình 2 (PID 1002)
                → fork() → Tiến trình 3 (PID 1003)
```

**2. Tạo Luồng trong Mỗi Tiến trình:**
```
Tiến trình 1 (PID 1001):
  - pthread_create() → User Thread 1 → LWP 1 → Kernel Thread 1
  - pthread_create() → User Thread 2 → LWP 2 → Kernel Thread 2
  - pthread_create() → User Thread 3 → LWP 3 → Kernel Thread 3
  - pthread_create() → User Thread 4 → LWP 4 → Kernel Thread 4
```

**3. Phân phối Lập lịch của Kernel:**
- Tổng cộng có 12 kernel threads (3 tiến trình × 4 luồng)
- Có 4 lõi CPU có sẵn
- Kernel phân phối các kernel threads trên các lõi:
  ```
  Lõi 0: Kernel Thread 1 (P1), Kernel Thread 5 (P2), Kernel Thread 9 (P3)
  Lõi 1: Kernel Thread 2 (P1), Kernel Thread 6 (P2), Kernel Thread 10 (P3)
  Lõi 2: Kernel Thread 3 (P1), Kernel Thread 7 (P2), Kernel Thread 11 (P3)
  Lõi 3: Kernel Thread 4 (P1), Kernel Thread 8 (P2), Kernel Thread 12 (P3)
  ```

**4. Chia sẻ Thời gian:**
- Mỗi luồng nhận được một time slice (thường từ 10-100ms)
- Các luồng bị ngắt và được lập lịch lại theo chu kỳ
- Bộ cân bằng tải có thể di chuyển các luồng giữa các lõi để tối ưu hiệu năng

**5. Đồng bộ hóa:**
- Các luồng có thể sử dụng mutex, semaphore hoặc biến điều kiện để đồng bộ hóa
- Các luồng bị chặn sẽ không được lập lịch
- Khi được đánh thức, các luồng sẽ được thêm vào hàng đợi chạy

## Các Tính năng Nổi bật của SOLARIS cho Hệ thống Đa lõi

1. **Processor Sets**: Cho phép nhóm các CPU lại và gán chúng cho các tiến trình cụ thể
2. **Resource Pools**: Cho phép phân vùng tài nguyên CPU và bộ nhớ một cách linh hoạt
3. **Dynamic Reconfiguration**: Hỗ trợ thêm hoặc loại bỏ CPU mà không cần khởi động lại hệ thống
4. **Công cụ Quan sát**: Cung cấp các công cụ như `prstat`, `mpstat`, `dtrace` để giám sát hiệu năng
5. **Khả năng Mở rộng**: Sử dụng các cơ chế khóa hiệu quả được tối ưu cho hệ thống đa lõi

## Tóm tắt

SOLARIS cung cấp một hệ thống quản lý tiến trình và luồng rất tinh vi cho các hệ thống đa lõi thông qua:

- Mô hình luồng M:N giúp tăng hiệu quả quản lý
- Hàng đợi chạy riêng cho mỗi CPU giúp tăng khả năng mở rộng
- Nhiều lớp lập lịch phù hợp với các loại tác vụ khác nhau
- Nhận thức về NUMA để tối ưu truy cập bộ nhớ
- Các nguyên thủy đồng bộ hóa tiên tiến
- Cân bằng tải thông minh giữa các lõi CPU

Kiến trúc này cho phép SOLARIS sử dụng hiệu quả tất cả các lõi CPU có sẵn trong khi vẫn đảm bảo tính công bằng, khả năng phản hồi và sự ổn định của hệ thống.
