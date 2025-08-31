# 🌿 LeafCare - Ứng dụng nhận diện bệnh lá cây

## 📱 Mô tả

LeafCare là ứng dụng Android giúp người dùng nhận diện bệnh lá cây một cách nhanh chóng và chính xác thông qua công nghệ AI. Ứng dụng cho phép chụp ảnh hoặc chọn ảnh từ thư viện để phân tích và đưa ra kết quả chẩn đoán cùng với gợi ý xử lý.

## ✨ Tính năng chính

### 🎯 Màn hình chào (Splash)
- Logo LeafCare với thiết kế đẹp mắt
- Tagline: "Nhận diện bệnh lá cây nhanh chóng & chính xác"
- Nút "Bắt đầu" để vào ứng dụng

### 🏠 Màn hình chính (Home)
- **Chụp ảnh lá**: Mở camera để chụp ảnh lá cây mới
- **Chọn ảnh từ thư viện**: Chọn ảnh có sẵn từ thư viện
- **Lịch sử chẩn đoán**: Hiển thị các lần chẩn đoán gần đây
- **Bottom Navigation**: Điều hướng giữa các màn hình chính

### 📸 Chức năng chụp/chọn ảnh
- Tích hợp camera để chụp ảnh trực tiếp
- Chọn ảnh từ thư viện thiết bị
- Preview ảnh trước khi phân tích

### 🔍 Màn hình kết quả phân tích
- Hiển thị ảnh lá đã chụp/chọn
- Kết quả chẩn đoán bệnh với độ tin cậy
- Gợi ý xử lý và chăm sóc
- Nút "Chẩn đoán lại" và "Quay lại trang chủ"

### 📚 Màn hình kiến thức cây trồng
- Tìm kiếm thông tin về bệnh cây trồng
- Danh sách bài viết và mẹo chăm sóc
- Tìm kiếm theo tên bệnh hoặc tên cây

### ⚙️ Màn hình cài đặt
- Chọn ngôn ngữ ứng dụng
- Thông tin phiên bản
- Thông tin nhóm phát triển

## 🎨 Thiết kế giao diện

### Màu sắc chủ đạo
- **Primary Green**: #2E7D32 (Xanh lá chủ đạo)
- **Primary Light**: #4CAF50 (Xanh lá nhạt)
- **Accent Color**: #8BC34A (Xanh lá phụ)
- **Disease Red**: #F44336 (Đỏ cho bệnh)
- **Normal Green**: #4CAF50 (Xanh cho lá khỏe)

### Trải nghiệm người dùng
- Giao diện tối giản, dễ sử dụng
- Màu sắc phân biệt rõ ràng (xanh = khỏe, đỏ = bệnh)
- Animation mượt mà khi chuyển màn hình
- Responsive design cho nhiều kích thước màn hình

## 🛠️ Công nghệ sử dụng

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Components**: Material Design Components
- **Navigation**: Android Navigation Component
- **Image Loading**: Glide
- **Permissions**: Camera, Storage

## 📁 Cấu trúc dự án

```
app/src/main/
├── java/com/example/green/
│   ├── SplashActivity.kt          # Màn hình chào
│   ├── MainActivity.kt            # Màn hình chính
│   ├── ResultActivity.kt          # Màn hình kết quả
│   ├── KnowledgeActivity.kt       # Màn hình kiến thức
│   ├── SettingsActivity.kt        # Màn hình cài đặt
│   └── CameraActivity.kt          # Màn hình camera
├── res/
│   ├── layout/                    # Layout files
│   ├── drawable/                  # Icons và backgrounds
│   ├── values/                    # Colors, strings, themes
│   └── menu/                      # Menu files
└── AndroidManifest.xml
```

## 🚀 Cài đặt và chạy

1. **Clone repository**:
   ```bash
   git clone <repository-url>
   cd Green
   ```

2. **Mở project trong Android Studio**

3. **Sync Gradle và build project**:
   ```bash
   ./gradlew build
   ```

4. **Chạy trên thiết bị hoặc emulator**

## 📋 Yêu cầu hệ thống

- Android API Level 24+ (Android 7.0+)
- Camera permission
- Storage permission (để chọn ảnh từ thư viện)

## 🔮 Tính năng tương lai

- [ ] Tích hợp AI model để phân tích bệnh thực tế
- [ ] Lưu trữ lịch sử chẩn đoán
- [ ] Push notification nhắc nhở chăm sóc cây
- [ ] Chia sẻ kết quả chẩn đoán
- [ ] Hỗ trợ đa ngôn ngữ
- [ ] Dark mode

## 👥 Nhóm phát triển

- **LeafCare Team**
- **Phiên bản**: 1.0.0
- **Năm**: 2024

## 📄 License

© 2024 LeafCare. All rights reserved.

---

*Với LeafCare, việc chăm sóc cây trồng trở nên dễ dàng và thông minh hơn bao giờ hết! 🌱*
