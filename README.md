
# 🐥 Nest.dev
<img src="https://github.com/user-attachments/assets/50a32839-cb81-4877-9571-a96f5674f605" width="400px" height="280px">
<br><br>
현직 개발자와 개발자 꿈나무들이 멘토와 멘티로 만나는 소통의 공간

<br><br>

## 📅 개발 기간

2025.05.27 ~ 2025.07.07

<br>

## 📚 팀원 및 역할 분담

| 팀원  | 기능          |
|-----|-------------|
|  신은주(팀장) | Toss Payments API 연동, 이용권/ 쿠폰 발급 API, Redis를 이용한 동시성 제어 적용   |
|  조혁준(부팀장) | 유저 API, JWT 인증/인가, 카카오 및 네이버 로그인 기능, AWS S3 이미지 기능, Redis를 이용한 캐시 적용  |
|  이윤승 | 관리자/ 프로필 API, AWS SES 이메일 알림 서비스 기능, 프론트(vercel) / 백(EC2) 배포    |
|  박한비 | 실시간 1:1 채팅, Redis pub/sub 적용, SSE 알림 |
|  진혜정 | 예약/ 리뷰/ 민원 API, Redis를 이용한 동시성 제어 적용, Grafana + Prometheus를 이용한 모니터링 및 Slack 알림 |


<br><br>

## 📌 개발 환경 및 기술 스택

<img width="653" alt="스크린샷 2025-07-07 오후 9 53 45" src="https://github.com/user-attachments/assets/8f3fccc3-875d-4cb8-a2aa-55a7f36d2efb" />


<br><br>

## 📌 주요 기능

**1. Oauth2**
 - 별도의 회원 가입 없이 카카오톡 또는 네이버 계정으로 간편 로그인 가능.
 - Oauth2 인증 절차를 따르며, 외부 인증 후 내부 사용자로 연동.
 - 로그인 시 JWT 토큰 발급 → 이후 인증 기반으로 시스템 이용.

<br>

**2. 채팅방 관리**
 - 예약 및 결제 후 채팅 가능 시스템.
 - 예약 시간에만 채팅방 생성, 예약 시간 종료 시 채팅방 종료.
 - 채팅방 생성 시 알림 기능, 채팅 시간 종료 5분 전 알림 및 종료.

<br>

**3. 결제 기능**
  - Toss Payments API를 활용하여 간편하고 안전한 **토스 페이 결제**.
  - 결제 승인 전후 상태 구분, 이중 처리 방지.

<br>


## [와이어 프레임](https://www.figma.com/design/dox3JoPTwOwT1J9fZxh1fT/Nest.Dev2?node-id=0-1&t=SXoiFLZLk7U7cH1p-1)

![스크린샷 2025-07-07 오전 11 11 07](https://github.com/user-attachments/assets/a8b246e9-324a-4344-bf6a-bf433e9e6533)


## 📌 API 명세서

[📚 Swagger](https://gganb.github.io/swagger-ui/)

<br><br>

## 📌 ERD

<img src="https://github.com/user-attachments/assets/e34063ba-68cb-4693-acaf-79bca8ae2429">


<br><br>

## 시스템 아키텍처
![image](https://github.com/user-attachments/assets/26026022-7768-4ffc-b370-13affa202530)


