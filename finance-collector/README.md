# Finance Collector Kt
Finance Collector Kt는 주식 데이터 크롤링, 가공, 적재, 조회 등의 기능을 하는 스프링부트 기반 코틀린 애플리케이션입니다.

## Startup selenium standalone docker container
## 로컬 테스트 환경 꾸리기
### 셀레니움 스탠드어론 실행
애플리케이션을 실행하기 위해 셀레니움 원격 웹 드라이버가 필요합니다.
1. 셀레니움 스탠드어론 도커 컨테이너 실행
    1. 도커 컨테이너 실행환경 준비
    1. selenium standalone 실행: `docker run -d -p 4444:4444 -p 7900:7900 --name selenium-standalone-chrome selenium/standalone-chrome`
    1. selenium standalone 도커 실행 관련 자세한 내용은 https://github.com/SeleniumHQ/docker-selenium 참고
1. 정상 동작 여부 확인
    1. http://localhost:4444 접속
1. 개발 과정에서 http://localhost:7900 접속하여 웹 드라이버의 동작을 모니터링 할 수 있습니다.


## 배포
1. 배포 스크립트가 Gradle 태스크로 구현되어 있습니다: `./gradlew deploy:{env}`
    1. (배포환경에 selenium standalone 실행중이라는 전제하에)
    1. 개발환경은 `deploy:dev`
    1. 상용환경은 `준비중`입니다.