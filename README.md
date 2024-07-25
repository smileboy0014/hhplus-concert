# [항해 플러스 백엔드 3주차] 서버 구축
### *시나리오 - 콘서트 예약 서비스*


### 목차

- [1. 요구사항 분석](##one)


---
<h2 id="one">1. 요구사항 분석</h2>

### Milestone & 요구사항 명세서 & API 명세서
[Google docs sheet ](https://docs.google.com/spreadsheets/d/1ARL1ZxmE_i0E6gUUF7H9PmBAEcioJcwOfM0VhmNDJx4/edit?gid=1675988926#gid=1675988926)

### 시퀀스 다이어그램
- ### 콘서트 목록 조회
![콘서트목록.png](docs/image/selectConcert.png)
- ### 포인트 충전
![메인로직.png](docs/image/chargePoint.png)
- ### 콘서트 좌석 예약 
![메인로직.png](docs/image/mainLogic.png)

### DB ERD
![ERD.png](docs/image/dbErd.png)

### Dummy Data - Postman
- **좌석 조회**
![img.png](docs/image/seat_result.png)
- **좌석 예약**
![img_1.png](docs/image/reservation_result.png)
- **결제**
![img_2.png](docs/image/payment_result.png)
- **토큰 발급**
![img_3.png](docs/image/token_result.png)
- **대기열 조회**
![img_4.png](docs/image/check_result.png)
- **충전**
![img.png](docs/image/charge_result.png)
---
## Swagger
![swagger.png](docs/image/swagger.png)
---
## Lock 비교
[Lock 비교 글](https://feel2.tistory.com/100)

## 회고

---
## 기술 스택
- Spring boot
- Jpa
- Mysql
