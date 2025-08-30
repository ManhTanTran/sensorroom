# IoT Sensor Data System

## 🛠 Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![MQTT](https://img.shields.io/badge/MQTT-660066?style=for-the-badge&logo=eclipse-mosquitto&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-000000?style=for-the-badge&logo=java&logoColor=white)

## 📌 Giới thiệu
Dự án này là một **hệ thống IoT** dùng để thu thập, xử lý, lưu trữ và trực quan hóa dữ liệu cảm biến.  
Hệ thống tích hợp **cảm biến, MQTT broker, backend server, cơ sở dữ liệu, và JavaFX frontend** để giám sát và tương tác theo thời gian thực.

---

## 🏗 Kiến trúc hệ thống
Hệ thống bao gồm các thành phần chính sau:

1. **Cảm biến (Node.js client / thiết bị phần cứng)**  
   - Thu thập dữ liệu thô (ví dụ: nhiệt độ, độ ẩm, chuyển động).  
   - Gửi dữ liệu dưới dạng **JSON** qua **giao thức MQTT** đến broker.

2. **MQTT Broker**  
   - Đóng vai trò trung gian truyền tin giữa cảm biến và backend server.  
   - Đảm bảo truyền dữ liệu tin cậy bằng mô hình publish/subscribe của MQTT.

3. **Backend Server (Spring Boot / Node.js Express)**  
   - Đăng ký nhận dữ liệu từ MQTT broker.  
   - Xử lý các gói tin JSON từ cảm biến.  
   - Lưu dữ liệu vào **cơ sở dữ liệu MySQL**.  
   - Cung cấp **HTTP REST API** cho việc truy vấn dữ liệu và cập nhật.  
   - Xử lý logic tương tác với người dùng.

4. **Cơ sở dữ liệu MySQL**  
   - Lưu trữ dữ liệu cảm biến và thông tin liên quan.  
   - Hỗ trợ truy vấn dữ liệu lịch sử và dữ liệu theo thời gian thực.  

5. **JavaFX Frontend**  
   - Ứng dụng desktop dành cho người dùng.  
   - Giao tiếp với backend qua **HTTP API**.  
   - Cung cấp giao diện trực quan, bảng điều khiển và chức năng tương tác thời gian thực.  

---

## 🔄 Data Flow
1. **Sensor → MQTT Broker**  
   - Sensor publishes JSON data via MQTT.  

2. **MQTT Broker → Backend Server**  
   - Backend subscribes to MQTT topics.  
   - Extracts and processes sensor data.  

3. **Backend Server → MySQL Database**  
   - Stores processed JSON data into MySQL.  

4. **Backend Server ↔ JavaFX Frontend**  
   - Uses **HTTP requests** for user interaction and updates.  
   - JavaFX fetches data and displays it to the user.  

---

## ⚙️ Công nghệ sử dụng
- **IoT & Messaging**: MQTT, Node.js (sensor simulation)  
- **Backend**: Spring Boot (Java) 
- **Database**: MySQL  
- **Frontend**: JavaFX (desktop app)  
- **Communication**:  
  - MQTT (sensor → broker → backend)  
  - HTTP REST API (backend ↔ frontend, backend ↔ database)

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (for Spring Boot & JavaFX)  
- Node.js (for sensor simulation / Express backend option)  
- MySQL 8.0+  
- MQTT Broker (e.g., Mosquitto)  

### Installation
1. **Start MQTT Broker**  
   ```bash
   mosquitto -v
   ```

2. **Run MySQL Database**  
   ```bash
   mysql -u root -p
   CREATE DATABASE sensor_data;
   ```

3. **Start Backend Server**  
   - **Spring Boot**:  
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Node.js Express**:  
     ```bash
     npm install
     node server.js
     ```

4. **Run Sensor Simulation**  
   ```bash
   node sensor.js
   ```

5. **Run JavaFX Frontend**  
   - Open in IDE (IntelliJ/Eclipse/NetBeans)  
   - Run `Main.java`  

---

## 📊 Example MQTT JSON Message
```json
{
  "deviceId": "DV-101A-01",
  "classroomId": 1
}
```

---

## 📡 Example REST API (Backend → Frontend)

- **Get all sensor data**
  ```http
  GET /api/devices
  ```

- **Get sensor data by deviceId**
  ```http
  GET /api/devices/{Id}
  ```

- **Fix devices by deviceCode**
  ```http
  PUT /api/devices/{deivceCode}
  ```

---

## 🔒 Security & Authentication
- JWT-based authentication (optional).  
- Role-based access for **Admin / User**.  
