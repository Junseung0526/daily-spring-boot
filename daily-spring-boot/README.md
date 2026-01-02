# 📝 Daily Todo API Project

> **Spring Boot 3.5.8과 JPA, Redis를 활용한 JWT 인증 기반 할 일 관리(Todo List) 백엔드 시스템입니다.**

![Java](https://img.shields.io/badge/java-21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/springboot-3.5.8-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![H2](https://img.shields.io/badge/H2-database-%23005C84.svg?style=for-the-badge&logo=h2&logoColor=white)

---

## 🛠 Tech Stack
* **Framework**: Spring Boot 3.5.8
* **Language**: Java 21
* **Security**: Spring Security, JWT (jjwt 0.11.5)
* **Database**: H2 (In-memory), Spring Data JPA
* **Caching**: Redis (Lettuce)
* **External API**: OpenWeatherMap API (실시간 날씨 연동)
* **Documentation**: SpringDoc OpenAPI v2 (Swagger)

---

## 주요 기능

### 1. 유저 인증 및 권한 (Security)
* **JWT 기반 인증**: 로그인 성공 시 발급되는 토큰을 `Authorization: Bearer {Token}` 헤더에 담아 요청을 인증합니다.
* **입력값 검증**: 유저 아이디(4~10자 소문자/숫자), 비밀번호(8~15자 알파벳/숫자/특수문자) 등에 대한 유효성 검사를 수행합니다.
* **관리자 권한**: 특정 관리자 토큰(`AAABnv...`)을 통해 `ADMIN` 권한으로 가입할 수 있으며, 관리자는 모든 할 일을 수정/삭제할 수 있습니다.

### 2. 성능 최적화 (Redis Caching)
* **프로필 및 목록 캐싱**: 유저 정보 조회 및 할 일 목록 조회 시 Redis를 사용하여 DB 부하를 줄이고 응답 속도를 높였습니다.
* **캐시 일관성**: 데이터가 변경(생성, 수정, 삭제)될 때 관련 캐시를 즉시 삭제(`@CacheEvict`)하여 데이터 정합성을 유지합니다.
* **QueryDSL 동적 검색**: 제목(키워드), 태그명, 완료 여부를 조합하여 복잡한 조건의 검색을 null-safe하게 처리합니다.

### 3. 할 일 관리 (Todo Service)
* **개인화된 목록**: 모든 유저는 자신만의 할 일 목록을 가지며, 타인의 데이터에 대한 접근이 차단됩니다.
* **상태 토글**: 할 일의 완료 여부를 간단하게 반전(True/False)시킬 수 있는 기능을 제공합니다.
* **검색 및 페이징**: 제목 키워드 검색, 완료 여부 필터링 및 페이징 처리를 통해 효율적인 조회가 가능합니다.

### 4. 외부 API 연동 (Weather Service)
* **실시간 날씨 기록**: 할 일을 등록하는 시점(createTodo)의 서울 날씨 정보를 OpenWeatherMap API를 통해 가져와 함께 저장합니다.
* **데이터 부가 정보**: 각 할 일 데이터에 날씨 정보(예: Clouds, Rain, Clear)가 포함되어 사용자 경험을 향상시킵니다.
---

## 주요 API 명세

### 유저 인증 (User Auth)
| 기능 | Method | URL | 설명 |
| :--- | :---: | :--- | :--- |
| **회원가입** | `POST` | `/api/users/signup` | 아이디, 비번, 이메일로 가입 |
| **로그인** | `POST` | `/api/users/login` | 로그인 후 JWT 토큰 발급 |
| **단건 조회** | `GET` | `/api/users/{userId}` | 유저 상세 정보 (Redis 캐싱) |
| **유저 삭제** | `DELETE` | `/api/users/{userId}` | 유저 및 관련 할 일 전체 삭제 |

### 할 일 관리 (Todo Management)
| 기능 | Method | URL | 설명 |
| :--- | :---: | :--- | :--- |
| **할 일 등록** | `POST` | `/api/todos` | 새로운 할 일 저장 |
| **내 목록 조회** | `GET` | `/api/todos` | 로그인한 유저의 전체 목록 (Redis 캐싱) |
| **상태 토글** | `PATCH` | `/api/todos/{id}/completed` | 완료 여부 반전 |
| **검색** | `GET` | `/api/todos/search` | 키워드로 할 일 검색 |
| **페이징 조회** | `GET` | `/api/todos/paging` | 5개씩 내림차순 페이징 조회 |

---

## 예외 응답 규격 (Error Handling)

공통 에러 핸들러(`GlobalExceptionHandler`)를 통해 통일된 규격의 에러 메시지를 반환합니다.

```json
{
  "timestamp": "2024-01-01T12:00:00.000",
  "status": 400,
  "code": "U-002",
  "message": "이미 존재하는 아이디입니다."
}
```

* INVALID_INPUT_VALUE (C-001): 입력 데이터 검증 실패
* USER_NOT_FOUND (U-001): 존재하지 않는 사용자
* UNAUTHORIZED_UPDATE (T-002): 본인 또는 관리자가 아닌 경우의 수정 시도
---

### 실행 방법
* **Redis 설정**: 로컬에 Redis 서버가 실행 중이어야 합니다 (기본 포트 6379)
* **JWT Secret**: application.properties에 jwt.secret.key를 설정해야 합니다.
* **Swagger 접속**: 서버 실행 후 http://localhost:8080/swagger-ui/index.html 에서 API를 테스트할 수 있습니다.
