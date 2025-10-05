🌿 LEAF-CARE

LEAF-CARE là ứng dụng Android hỗ trợ người dùng chăm sóc cây trồng thông minh, bao gồm các tính năng như nhận diện bệnh trên lá cây, lưu lịch sử chẩn đoán, chatbot hỗ trợ AI và tùy chỉnh cài đặt cá nhân.

📁 Cấu trúc dự án
LEAF-CARE/
│
├── .gradle/                     # Cấu hình Gradle
├── .idea/                       # Cấu hình IDE (Android Studio)
│
├── app/
│   ├── build/                   # Thư mục build tự động sinh ra
│   └── src/
│       ├── androidTest/         # Kiểm thử Android
│       └── main/
│           ├── assets/          # Tài nguyên bổ sung (nếu có)
│           ├── java/com/example/green/
│           │   ├── ChatBotGemini.java          # Chatbot AI tích hợp Gemini
│           │   ├── HistoryAdapter.java         # Adapter cho danh sách lịch sử
│           │   ├── HistoryDatabaseHelper.java  # Quản lý cơ sở dữ liệu SQLite
│           │   ├── HistoryDetailActivity.java  # Màn hình chi tiết lịch sử
│           │   ├── HistoryFragment.java        # Fragment hiển thị lịch sử
│           │   ├── HistoryItem.java            # Model dữ liệu lịch sử
│           │   ├── HomeFragment.java           # Trang chủ ứng dụng
│           │   ├── LeafCareAI.java             # Xử lý AI nhận diện bệnh lá
│           │   ├── MainActivity.java           # Activity chính
│           │   └── SettingFragment.java        # Màn hình cài đặt
│           │
│           ├── res/
│           │   ├── drawable/                   # Ảnh, icon
│           │   ├── layout/
│           │   │   ├── activity_history_detail.xml
│           │   │   ├── activity_main.xml
│           │   │   ├── activity_result.xml
│           │   │   ├── activity_setting.xml
│           │   │   ├── item_history.xml
│           │   │   ├── layout_history.xml
│           │   │   ├── layout_home.xml
│           │   │   ├── result_item.xml
│           │   │   └── view_setting.xml
│           │   ├── mipmap-*dpi/                # Icon ứng dụng theo độ phân giải
│           │   ├── values/                     # Chuỗi, màu sắc, style mặc định
│           │   ├── values-en/                  # Chuỗi tiếng Anh
│           │   ├── values-night/               # Giao diện chế độ tối
│           │   └── xml/                        # Cấu hình bổ sung
│           │
│           └── AndroidManifest.xml             # Cấu hình và khai báo quyền ứng dụng
│
└── build.gradle, settings.gradle               # Cấu hình dự án Android

🚀 Chức năng chính

🌱 Nhận diện bệnh lá cây: Sử dụng mô hình AI trong LeafCareAI.java để phân tích hình ảnh và dự đoán bệnh.

🧠 Chatbot AI: ChatBot Gemini hỗ trợ người dùng trong việc hỏi đáp, chăm sóc cây, và hướng dẫn trồng trọt.

📜 Lưu lịch sử chẩn đoán: Lưu trữ thông tin chẩn đoán trong cơ sở dữ liệu SQLite, hiển thị qua HistoryFragment.

⚙️ Cài đặt người dùng: Cho phép điều chỉnh ngôn ngữ, giao diện, và cấu hình thông báo.

🏠 Trang chủ: Cung cấp truy cập nhanh đến các tính năng chính và kết quả gần nhất.

🧩 Công nghệ sử dụng
Thành phần	Công nghệ
Ngôn ngữ	Java
IDE phát triển	Android Studio
CSDL	SQLite (local)
AI / Machine Learning	Google Gemini API / mô hình tùy chỉnh
Thiết kế giao diện	XML Layout
Quản lý dự án	Gradle
🏗️ Cách chạy dự án

Clone dự án

git clone https://github.com/<tên-repo>/leaf-care.git


Mở bằng Android Studio

Đảm bảo đã cài đặt:

Android SDK (API ≥ 30)

NDK (nếu dự án có xử lý ảnh bằng JNI)

Chạy ứng dụng:
Chọn thiết bị ảo (AVD) hoặc điện thoại thật → Nhấn Run ▶

📸 Các màn hình chính

MainActivity: Giao diện điều hướng chính

HomeFragment: Màn hình tổng quan cây trồng và hướng dẫn

HistoryFragment: Hiển thị danh sách lịch sử chẩn đoán

SettingFragment: Quản lý cài đặt cá nhân

👩‍💻 Nhóm phát triển
Họ tên	Vai trò	Nhiệm vụ
...	Developer	Xây dựng chức năng AI và xử lý hình ảnh
...	UI/UX Designer	Thiết kế giao diện và bố cục layout
...	Tester	Kiểm thử và sửa lỗi ứng dụng
...	Documenter	Viết tài liệu và hướng dẫn sử dụng
🔮 Hướng phát triển trong tương lai

🌍 Tích hợp API thời tiết: Đưa ra khuyến nghị chăm sóc cây dựa trên điều kiện thời tiết thực tế.

☁️ Lưu trữ dữ liệu đám mây: Cho phép người dùng đăng nhập và đồng bộ lịch sử giữa các thiết bị.

📷 Cải thiện mô hình AI: Nâng độ chính xác nhận diện bệnh bằng mạng CNN (Convolutional Neural Network).

🗣️ Chatbot thông minh hơn: Cho phép chatbot nhận diện ngôn ngữ tự nhiên tiếng Việt tốt hơn.

🧾 Báo cáo tăng trưởng cây: Tự động theo dõi và vẽ biểu đồ phát triển dựa trên dữ liệu người dùng nhập.

🔔 Thông báo thông minh: Nhắc nhở tưới nước, bón phân, hoặc kiểm tra cây theo lịch tự động.

📄 Giấy phép

Dự án này được phát hành theo Giấy phép MIT.
Bạn có thể tự do sử dụng, chỉnh sửa và phân phối cho mục đích học tập, nghiên cứu và phát triển.