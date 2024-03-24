# PROPERSTOCK

재무 데이터를 통한 기업 벨류에이션 및 증권사 목표 주가 기반 퀀트 투자 도구

## Features

- FnGuide 등 재무데이터 수집 및 웹 스크래핑
- 기업가치 평가 모델을 이용한 적정주가 계산
- 적정주가, 목표주가 산출 및 현재주가 괴리율 기반 종목 선정

## Prerequisites

- Selenium Chrome
- Mongo DB
- [Optional] Docker Registry

## Structure

- finance-collector
    - 네이버 파이낸스, FnGuide 웹 스크래핑
    - 재무 데이터 수집 API 제공
- finance-collector-py
    - 실시간 주가 데이터 수집 및 Websocket API 제공
- finance-front
    - 프론트엔드
