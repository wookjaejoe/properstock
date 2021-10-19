# Finance Collector KT

## 로컬 테스트 환경 꾸리기
1. 셀레니움 스탠드어론 실행
    1. 도커 컨테이너 실행환경 준비
    1. selenium standalone 실행: `docker run -d -p 4444:4444 -p 7900:7900 --shm-size="2g" selenium/standalone-chrome`
    1. selenium standalone 도커 실행 관련 자세한 내용은 https://github.com/SeleniumHQ/docker-selenium 참고
1. 그리고 그냥 실행하면 됩니다. 프로파일 지정 잊지 마세요.

## 배포
1. 배포 스크립트가 Gradle 태스크로 구현되어 있습니다: `./gradlew deploy:{env}`
    1. (배포환경에 selenium standalone 실행중이라는 전제하에)
    1. 개발환경은 `deploy:dev`
    1. 상용환경은 `준비중`입니다.
1. 그럼, 끝! 쉽죠?