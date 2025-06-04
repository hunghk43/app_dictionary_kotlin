# Từ điển Anh-Việt 

## Giới thiệu

Đây là một ứng dụng từ điển Anh-Việt đơn giản được xây dựng cho nền tảng Android, sử dụng Kotlin và Jetpack Compose. Ứng dụng cho phép người dùng tra cứu từ tiếng Anh, xem thông tin chi tiết của từ (phiên âm, loại từ, các định nghĩa bằng tiếng Anh, ví dụ câu).

## Màn hình ứng dụng

![ảnh màn hình trang chủ ](https://i.imgur.com/eqAoC6M.png)

Ví dụ:
| Màn hình Tìm kiếm | Màn hình Chi tiết |
| :---------------: | :----------------: |
| ![Search Screen](https://i.imgur.com/1CGG5Ma.png) | ![Definition Screen](https://i.imgur.com/E6jcZ4o.png) |


## Tính năng chính

*   **Tra cứu từ Anh-Việt:** Nhập từ tiếng Anh để xem nghĩa tiếng Việt chính.
*   **Thông tin từ chi tiết:**
    *   Hiển thị từ gốc tiếng Anh.
    *   Phiên âm quốc tế (IPA).
    *   Nút phát âm thanh cho từ tiếng Anh.
    *   Liệt kê các loại từ (danh từ, động từ, tính từ,...) với các định nghĩa và ví dụ câu bằng tiếng Anh.
*   **Dịch chi tiết theo yêu cầu:** Cho phép người dùng dịch từng định nghĩa hoặc ví dụ câu tiếng Anh cụ thể sang tiếng Việt.
*   **Giao diện người dùng hiện đại:** Xây dựng bằng Jetpack Compose, tuân theo nguyên tắc Material Design 3.
*   **Lịch sử tìm kiếm:**  Hiển thị các từ đã tra cứu gần đây.


## Công nghệ sử dụng

*   **Ngôn ngữ:** Kotlin
*   **Giao diện người dùng (UI):** Jetpack Compose
*   **Kiến trúc:** MVVM (Model-View-ViewModel)
*   **Xử lý bất đồng bộ:** Kotlin Coroutines & Flow
*   **Networking:** Retrofit & OkHttp (với Logging Interceptor)
*   **JSON Parsing:** Gson 
*   **Dependency Injection:** Hilt
*   **Navigation:** Navigation Compose
*   **API:**
    *   `https://api.dictionaryapi.dev/`: Để lấy thông tin chi tiết của từ tiếng Anh (phiên âm, loại từ, định nghĩa tiếng Anh, ví dụ, audio).
    *   `https://api.mymemory.translated.net/`: Để dịch từ tiếng Anh sang nghĩa tiếng Việt.
*   **Môi trường phát triển:** Android Studio 
*   **Build System:** Gradle

## Cấu trúc Project

Dự án được tổ chức theo kiến trúc MVVM và chia thành các package chính:

*   **`data`**: Chứa các model, sources  và repository.
    *   `model`: Các data class  đại diện cho cấu trúc dữ liệu.
    *   `remote`: Các interface Retrofit service
    *   `repository`:  chịu trách nhiệm lấy và kết hợp dữ liệu từ các API.
*   **`di`**: Chứa các Hilt modules (`AppModule`) để cung cấp dependencies.
*   **`ui`**: Chứa các thành phần liên quan đến giao diện người dùng.
    *   `components`: Các Composable tái sử dụng 
    *   `screens`: Các Composable đại diện cho từng màn hình .
    *   `theme`: Định nghĩa màu sắc, typography, và shapes cho ứng dụng 
*   **`utils`**: Chứa các lớp và hàm tiện ích
*   **`viewmodel`**: Chứa các `ViewModel`  xử lý logic nghiệp vụ và quản lý trạng thái cho UI.
*   **Thư mục gốc package**: 

## Hướng dẫn cài đặt và chạy dự án

1.  **Clone repository:**
    ```bash
    git clone [https://github.com/hunghk43/app_dictionary_kotlin.git]
    cd [Project_HK2_24_25_LaptrinhMobile]
    ```
2.  **Mở project bằng Android Studio:** 
3.  **Đồng bộ Gradle:** Android Studio sẽ tự động đồng bộ Gradle. 
4.  **Build và Chạy ứng dụng:**
    *   Chọn thiết bị (máy ảo hoặc thiết bị thật).
    *   Nhấn nút "Run" trong Android Studio.

## Hướng phát triển trong tương lai

*   Cải thiện chất lượng dịch tiếng Việt bằng cách tích hợp API chuyên nghiệp hơn
*   Thêm chức năng dịch Việt - Anh
*   Lưu từ yêu thích
*   Chế độ học từ vựng 
*   Hỗ trợ chế độ tối
*   Tối ưu hóa cho chế độ offline 

## Đóng góp
Nếu bạn muốn đóng góp, vui lòng fork repository và tạo một pull request. Mọi đóng góp đều được chào đón!

## Tác giả

*   **[Hoàng Kim Hùng]**
*   **[Phạm Minh Tuấn]** 



---
