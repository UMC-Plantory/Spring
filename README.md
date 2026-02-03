# 🚀 플랜토리 (Plantory)

<img width="512" height="512" alt="Property 1=Logo" src="https://github.com/user-attachments/assets/a2eb5ccf-7476-47cb-a224-fb174d2f2f10" />


> 기억으로 꽃 피우는 나만의 정원 일기


---

<br>

## 👥 멤버
| 노먼/박승태 | 유즈/박유정 | 지니/박형진 | 메이/손가영 | 하루/심현민 - 중도 탈퇴 |
|:------:|:------:|:------:|:------:|:------:|
| <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/671e9737-20ab-45d5-b755-f5e9c84ed31f" /> | <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/c1241267-2aba-4e66-beaf-f1919841f765" /> | <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/7b8e410c-6ff6-4969-90c2-7c45906b2cbe" /> | <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/c6bf4d3e-46b6-4111-80e6-fbea97a3510e" /> | <img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/03c4ae65-6f3d-44f0-aab7-7dad662c13af" /> |
| PL | BE | BE | BE | BE |
| [GitHub](https://github.com/iseevict) | [GitHub](https://github.com/yujeong430) | [GitHub](https://github.com/gud0217) | [GitHub](https://github.com/gayo73) | [GitHub](https://github.com/simhyunmin) |

<br>


## 📱 소개

> '플랜토리'는 성장형 일기 작성 어플리케이션으로 사용자의 작성 동기를 부여하고 스스로 성찰할 수 있는 서비스 입니다.
>
> 지속성과 동기부여 제공, 감정표현의 개방, 기록의 가치 제공 등을 통해 사용자의 성장을 도와드릴게요.

<br>

## 📆 프로젝트 기간
- 전체 기간: `2025.06.30 - ing`
- 개발 기간: `2025.06.30 - ing`

<br>

## 🤔 요구사항
For building and running the application you need:

[![Java](https://img.shields.io/badge/Java-17-red.svg)]()
[![SpringBoot](https://img.shields.io/badge/SpringBoot-3.3.13-green.svg)]()

<br>

## ⚒️ 개발 환경
* BackEnd : IntelliJ
* 버전 및 이슈 관리 : Github, Github Issues
* 협업 툴 : Discord, Notion

<br>

## 아키텍처 다이어그램
<img width="2277" height="531" alt="image" src="https://github.com/user-attachments/assets/820d7248-3166-4c3e-bf54-05dc5840f7ff" />


<br>

## 🔎 기술 스택
### Envrionment
<div align="left">
<img src="https://img.shields.io/badge/AWS-FF9900?style=for-the-badge&logo=AmazonAWS&logoColor=white" />
<img src="https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=Linux&logoColor=black" /> 
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white" /> 
<img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white" /> 
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white" />
</div>

### Development
<div align="left">
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white" />
<img src="https://img.shields.io/badge/Spring%20Boot-5FA04E?style=for-the-badge&logo=SpringBoot&logoColor=white" />
<img src="https://img.shields.io/badge/JPA-D70F64?style=for-the-badge&logo=jakarta&logoColor=white" /> 
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white" /> 
</div>

### Communication
<div align="left">
<img src="https://img.shields.io/badge/Notion-white.svg?style=for-the-badge&logo=Notion&logoColor=000000" />
<img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white" />
<img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white" />
</div>

<br>

## 📱 화면 구성
<table>
  <tr>
    <td>
      사진 넣어주세요
    </td>
    <td>
      사진 넣어주세요
    </td>
   
  </tr>
</table>

## 🔖 브랜치 컨벤션
| 브랜치 종류   | Prefix     | 용도                                                 | 예시                              |
| -------- | ---------- | -------------------------------------------------- | ------------------------------- |
| 운영(메인)   | `main`     | 실제 서비스가 동작하는 배포용 브랜치                               | `main`                          |
| 개발 통합    | `develop`  | 다음 릴리스 준비를 위한 개발 통합 브랜치                            | `develop`                       |
| 기능 추가    | `feat/`    | 새로운 기능(Feature) 개발용 브랜치                            | `feat/user-auth`                |
| 버그 수정    | `bugfix/`  | develop 브랜치상의 일반 버그 수정                             | `bugfix/login-nullpointer`      |
| 긴급 수정    | `hotfix/`  | 운영 중인 main 브랜치에서 발생한 긴급 버그 수정                      | `hotfix/payment-timeout`        |
| 리팩터링     | `refactor/`   | 기존 코드 구조 개선·리팩터링용 브랜치                              | `refactor/order-service`           |
| 릴리스 준비   | `release/` | 배포 전 최종 버전 준비·테스트용 브랜치                             | `release/v1.2.0`                |
| 빌드·설정    | `chore/`   | 패키지/의존성 업데이트, 빌드 설정, 환경 구성 등 비기능 작업                | `chore/update-dependencies`     |
| 문서       | `docs/`    | API 명세서·README·아키텍처 다이어그램 등 문서 작업                  | `docs/openapi-spec`             |
| CI/CD 설정 | `ci/`      | GitHub Actions·Jenkins·Dockerfile 등 CI·CD 파이프라인 설정 | `ci/github-actions-pipeline`    |




<br>

## 🌀 코딩 컨벤션
* https://jumbled-coyote-e75.notion.site/2243d073953b809d8ae6f15834f85b49?source=copy_link

<br>

## 📁 PR 컨벤션
* PR 시, 템플릿이 등장한다. 해당 템플릿에서 작성해야할 부분은 아래와 같다
    1. `PR 제목 작성`, PR 제목 규칙에 맞게 작성
    2. `작업 내용 작성`, 작업 내용에 대해 자세하게 작성
    3. `관련 이슈 작성`, PR과 관련된 이슈를 연결
    4. `리뷰 포인트`, 본인 PR에서 꼭 확인해야 할 부분을 작성
    6. `PR 올리기 전 확인`, PR 올리기 전 확인사항 체크

#### 🌟 태그 종류 (커밋 컨벤션과 동일)
| 태그        | 설명                                                   |
|-------------|--------------------------------------------------------|
| [Feat]      | 새로운 기능 추가                                       |
| [Fix]       | 버그 수정                                              |
| [Refactor]  | 코드 리팩토링 (기능 변경 없이 구조 개선)              |
| [Style]     | 코드 포맷팅, 들여쓰기 수정 등                         |
| [Docs]      | 문서 관련 수정                                         |
| [Test]      | 테스트 코드 추가 또는 수정                            |
| [Chore]     | 빌드/설정 관련 작업                                    |
| [Design]    | UI 디자인 수정                                         |
| [Hotfix]    | 운영 중 긴급 수정                                      |
| [CI/CD]     | 배포 및 워크플로우 관련 작업                          |

### ✅ PR 예시 모음
> [Chore] 프로젝트 초기 세팅 <br>
> [Feat] 프로필 화면 UI 구현 <br>
> [Fix] iOS 17에서 버튼 클릭 오류 수정 <br>
> [Design] 로그인 화면 레이아웃 조정 <br>
> [Docs] README에 프로젝트 소개 추가 <br>

<br>

## 💡 이슈 컨벤션
* 이슈 작성 시, 템플릿 선택 창 등장
    1. 작성할 이슈에 맞게 템플릿 선택
    2. 이슈 템플릿에 맞게 작성 후 이슈 작성
    3. 기능/버그 외의 이슈는 Cutom(기본) 이슈 템플릿 선택

## 📑 커밋 컨벤션

### 🏷️ 커밋 태그 가이드

 | 태그        | 설명                                                   |
|-------------|--------------------------------------------------------|
| [Feat]      | 새로운 기능 추가                                       |
| [Fix]       | 버그 수정                                              |
| [Refactor]  | 코드 리팩토링 (기능 변경 없이 구조 개선)              |
| [Style]     | 코드 포맷팅, 세미콜론 누락, 들여쓰기 수정 등          |
| [Docs]      | README, 문서 수정                                     |
| [Test]      | 테스트 코드 추가 및 수정                              |
| [Chore]     | 패키지 매니저 설정, 빌드 설정 등 기타 작업           |
| [Design]    | UI, CSS, 레이아웃 등 디자인 관련 수정                |
| [Hotfix]    | 운영 중 긴급 수정이 필요한 버그 대응                 |
| [CI/CD]     | 배포 관련 설정, 워크플로우 구성 등                    |

### ✅ 커밋 예시 모음
> [Chore] 프로젝트 초기 세팅 <br>
> [Feat] 프로필 화면 UI 구현 <br>
> [Fix] iOS 17에서 버튼 클릭 오류 수정 <br>
> [Design] 로그인 화면 레이아웃 조정 <br>
> [Docs] README에 프로젝트 소개 추가 <br>

<br>

## 🗂️ 폴더 컨벤션
- 도메인형 구조
  
![image](https://github.com/user-attachments/assets/0b566fe6-708e-48be-b8d2-afabbf060bf3)

