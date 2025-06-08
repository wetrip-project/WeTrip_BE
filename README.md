# WeTrip_BE
 여행 동행 플랫폼(WeTrip) Back-End main Repository

## 기술 스택
- springboot
- rds(mysql)
- redis
- docker

## 작업 규칙
- 브랜치 파서 작업 후,
- `main` 브랜치로 PR 날려서 머지
  <br/><br/>

## 기타 세팅 내용
### 멀티모듈 디렉터리 구조
- 채팅 작업시 deploy.yml 파일 수정 필요
- 코드 수정 감지시 해당 디렉터리 배포 필요

### Docker로 CI/CD 배포룰 세팅
- main 브랜치에서 CI/CD Actions 동작합니다.
    - `Dockerfile .jar` 파일 복사 및 실행
    - `.github/workflows/deploy.yml` main 브랜치에 푸시 이벤트 발생 시, 서버에 배포