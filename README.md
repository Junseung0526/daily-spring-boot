# 📝 Daily Todo API Project

> **Spring Boot와 JPA를 활용한 할 일 관리(Todo List) 백엔드 시스템입니다.**

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![H2](https://img.shields.io/badge/H2-database-%23005C84.svg?style=for-the-badge&logo=h2&logoColor=white)

---

## Tech Stack
* **Framework**: Spring Boot 3.x
* **Database**: H2 (In-memory)
* **Library**: 
  * Spring Data JPA
  * Lombok
  * Validation
  * SpringDoc OpenAPI (Swagger)

---

## 주요 기능 및 사용법 (API Endpoints)

### 1. 할 일 관리 (Basic CRUD)
| 기능 | Method | URL | 설명 |
| :--- | :---: | :--- | :--- |
| **등록** | `POST` | `/api/todos` | 새로운 할 일을 저장합니다. |
| **전체 조회** | `GET` | `/api/todos` | 저장된 모든 할 일을 가져옵니다. |
| **상세 조회** | `GET` | `/api/todos/{id}` | 특정 ID의 할 일을 조회합니다. |
| **수정** | `PUT` | `/api/todos/{id}` | 제목이나 완료 여부를 수정합니다. |
| **삭제** | `DELETE` | `/api/todos/{id}` | 특정 할 일을 삭제합니다. |

### 2. 검색 및 필터링 (Search & Filter)
* **키워드 검색**: `GET` `/api/todos/search?keyword=공부`
  * 제목에 특정 키워드가 포함된 항목을 검색
* **상태 필터링**: `GET` `/api/todos/filter?completed=true`
  * 완료(`true`) 또는 미완료(`false`) 항목만 골라봄
* **복합 검색**: `GET` `/api/todos/search/complex?keyword=공부&completed=false`
  * 키워드와 완료 여부를 동시에 만족하는 항목을 검색

### 3. 페이징 및 정렬 (Paging & Sorting)
* **기본 페이징**: `GET` `/api/todos/paging?page=0&size=5`
  * 0번 페이지부터 5개씩 데이터를 가져옴
* **정렬 포함**: `GET` `/api/todos/paging?page=0&size=5&sort=title,asc`
  * 제목 기준 오름차순(`asc`) 또는 내림차순(`desc`)으로 정렬

---

## 예외 처리 및 검증 (Validation)

* **입력값 검증**: 
  * 제목(`title`)은 **필수** 입력 사항
  * 제목은 **2자 이상 20자 이하**여야 함 
  * 규칙 위반 시 `400 Bad Request`를 반환
* **통합 에러 응답 규격**:
```json
{
  "status": 400,
  "message": "에러 메시지 내용"
}
