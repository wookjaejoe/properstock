# Finance Collector Py
파이썬 기반 파이낸스 데이터 수집기

## 개발환경
1. 파이썬 3.8+ 설칯
1. 의존성 설치: `pip install -r requirements.txt`

## 테스트
1. test_sub.py 실행
2. start_pub.py 실행

## 배포
도커 이미지 생성 후 리모트 호스트에 컨테이너 실행한다.
1. 버전 변경: ./start_pub.py 파일의 VERSION 변수 값 수정 
1. 배포 스크립트 실행: `python deploy.py --user $USER --pawd $PASSWORD`