# 🏆 Game Leaderboard Service

Dịch vụ bảng xếp hạng game được xây dựng bằng Spring Boot và Redis Master-Replica.

## 📋 Mục Lục

* [Tổng Quan](#-tổng-quan)
* [Kiến Trúc](#-kiến-trúc)
* [Tính Năng](#-tính-năng)
* [Yêu Cầu](#-yêu-cầu)
* [Cài Đặt Nhanh](#-cài-đặt-nhanh)
* [API](#-api)
* [Cấu Hình](#️-cấu-hình)
* [Testing](#-testing)
* [Redis Commands](#-redis-commands)
* [Lưu Ý](#-lưu-ý)

---

# 🎯 Tổng Quan

Game Leaderboard Service sử dụng Redis Sorted Set (ZSET) để lưu trữ và truy vấn bảng xếp hạng với hiệu năng cao.

### Đặc điểm

* Redis Sorted Set cho leaderboard
* Tách biệt luồng đọc và ghi
* Redis Master-Replica replication
* RESTful API đơn giản
* Khả năng mở rộng cao

### Độ phức tạp

| Thao tác      | Redis Command | Complexity   |
| ------------- | ------------- | ------------ |
| Cập nhật điểm | ZADD          | O(log N)     |
| Lấy top N     | ZREVRANGE     | O(log N + M) |
| Lấy điểm      | ZSCORE        | O(1)         |
| Lấy hạng      | ZREVRANK      | O(log N)     |

---

# 🏗 Kiến Trúc

```text
                  WRITE
Application ----------------> Redis Master
                                    │
                                    │ Replication
                                    ▼
                              Redis Replica
                                    ▲
                                    │
                  READ              │
Application ------------------------+
```

### Master

* Nhận tất cả request ghi dữ liệu
* Cập nhật điểm người chơi

### Replica

* Phục vụ các request đọc
* Giảm tải cho Master
* Hỗ trợ mở rộng khả năng đọc

---

# ✨ Tính Năng

✅ Cập nhật điểm người chơi

✅ Lấy top N người chơi

✅ Xem điểm hiện tại

✅ Xem thứ hạng người chơi

✅ Redis Master-Replica Replication

✅ Read/Write Separation

---

# 📦 Yêu Cầu

* Java 17+
* Maven 3.6+
* Docker
* Docker Compose

---

# 🚀 Cài Đặt Nhanh

## 1. Clone project

```bash
git clone <repository-url>
cd leaderboard-service
```

## 2. Khởi động Redis

```bash
docker-compose up -d
```

Kiểm tra container:

```bash
docker ps
```

Kết quả mong đợi:

```text
redis-master
redis-replica
```

---

## 3. Build project

```bash
mvn clean package
```

---

## 4. Chạy ứng dụng

```bash
mvn spring-boot:run
```

Hoặc:

```bash
java -jar target/*.jar
```

---

Ứng dụng sẽ chạy tại:

```text
http://localhost:8080
```

---

# 📖 API

## 1. Cập nhật điểm người chơi

### Request

```http
POST /leaderboard/{username}?score={score}
```

### Ví dụ

```bash
curl -X POST "http://localhost:8080/leaderboard/player1?score=1500"
```

### Redis Command

```redis
ZADD game:leaderboard 1500 player1
```

---

## 2. Lấy Top N người chơi

### Request

```http
GET /leaderboard/top/{n}
```

### Ví dụ

```bash
curl "http://localhost:8080/leaderboard/top/5"
```

### Response

```json
{
  "player1": 2500.0,
  "player2": 2300.0,
  "player3": 2000.0,
  "player4": 1800.0,
  "player5": 1500.0
}
```

### Redis Command

```redis
ZREVRANGE game:leaderboard 0 4 WITHSCORES
```

---

## 3. Lấy thông tin người chơi

### Request

```http
GET /leaderboard/{username}
```

### Ví dụ

```bash
curl "http://localhost:8080/leaderboard/player1"
```

### Response

```json
{
  "username": "player1",
  "score": 2500.0,
  "rank": 1
}
```

### Redis Commands

```redis
ZSCORE game:leaderboard player1
ZREVRANK game:leaderboard player1
```

---

# ⚙️ Cấu Hình

## application.yml

```yaml
spring:
  redis:
    master:
      host: localhost
      port: 6379
      password: masterpass

    replica:
      host: localhost
      port: 6380
      password: replicapass
```

---

## docker-compose.yml

```yaml
version: '3.8'

services:
  redis-master:
    image: redis:7-alpine
    container_name: redis-master
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes --requirepass masterpass

  redis-replica:
    image: redis:7-alpine
    container_name: redis-replica
    ports:
      - "6380:6379"
    command: redis-server --slaveof redis-master 6379 --masterauth masterpass --requirepass replicapass
    depends_on:
      - redis-master
```

---

# 🧪 Testing

## Sinh dữ liệu test

Tạo file:

```bash
generate-test-data.sh
```

```bash
#!/bin/bash

for i in {1..10}
do
  score=$((RANDOM % 5000 + 1000))

  curl -X POST \
  "http://localhost:8080/leaderboard/player$i?score=$score"
done
```

Cấp quyền:

```bash
chmod +x generate-test-data.sh
```

Chạy:

```bash
./generate-test-data.sh
```

---

## Kiểm tra Top 10

```bash
curl "http://localhost:8080/leaderboard/top/10"
```

---

## Kiểm tra thông tin người chơi

```bash
curl "http://localhost:8080/leaderboard/player5"
```

---

# 🔍 Kiểm Tra Replication

## Master

```bash
docker exec -it redis-master redis-cli -a masterpass INFO replication
```

Kết quả:

```text
role:master
connected_slaves:1
```

---

## Replica

```bash
docker exec -it redis-replica redis-cli -a replicapass INFO replication
```

Kết quả:

```text
role:slave
master_link_status:up
```

---

# 📚 Redis Commands

### Xem toàn bộ leaderboard

```bash
docker exec -it redis-master redis-cli -a masterpass
```

```redis
ZREVRANGE game:leaderboard 0 -1 WITHSCORES
```

---

### Xem điểm người chơi

```redis
ZSCORE game:leaderboard player1
```

---

### Xem thứ hạng

```redis
ZREVRANK game:leaderboard player1
```

---

# ⚠️ Lưu Ý

### Eventual Consistency

Do dữ liệu được đồng bộ từ Master sang Replica theo cơ chế replication nên có thể xảy ra độ trễ nhỏ:

```text
Update Score
     ↓
Master Updated
     ↓
Replication Delay
     ↓
Replica Updated
```
