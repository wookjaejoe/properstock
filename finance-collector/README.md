# Finance Collector Kt
Finance Collector Kt는 주식 데이터 크롤링, 가공, 적재, 조회 등의 기능을 하는 스프링부트 기반 코틀린 애플리케이션입니다.

## 로컬 테스트 환경 꾸리기
### 셀레니움 스탠드어론 실행
애플리케이션을 실행하기 위해 셀레니움 원격 웹 드라이버가 필요합니다.
1. 셀레니움 스탠드어론 도커 컨테이너 실행
    1. 도커 컨테이너 실행환경 준비
    2. selenium standalone 실행: `docker run -d -p 4444:4444 -p 7900:7900 --name selenium-standalone-chrome selenium/standalone-chrome`
    3. selenium standalone 도커 실행 관련 자세한 내용은 https://github.com/SeleniumHQ/docker-selenium 참고
2. 정상 동작 여부 확인
    1. http://localhost:4444 접속
3. 개발 과정에서 http://localhost:7900 접속하여 웹 드라이버의 동작을 모니터링 할 수 있습니다.

## 실행
다음 JVM 옵션과 함꼐 실행
```
-Dspring.data.mongodb.host= -Dspring.data.mongodb.username= -Dspring.data.mongodb.password= -Dwebdriver.chrome.remote.url=
```

## 배포
1. 배포 스크립트가 Gradle 태스크로 구현되어 있습니다: `./gradlew deploy:{env}`
    1. (배포환경에 selenium standalone 실행중이라는 전제하에)
    2. 개발환경은 `./gradlew deploy.dev -Puser=$USER -Ppassword=$PASSWORD`
       1. `Cause: reject HostKey: jowookjae.in`와 같은 오류가 발생하면, `ssh-keyscan -t rsa jowookjae.in >> ~/.ssh/known_hosts`를 실행하라.
    3. 상용환경은 `준비중`입니다.