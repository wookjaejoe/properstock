import { faCaretDown, faCaretUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useCallback, useState } from 'react';
import './Main.css';
const Main = () => {
  const [expanded, setExpanded] = useState({});

  const handleExpand = useCallback((type) => {
    console.log('??');
    setExpanded((pre) => {
      return {
        ...pre,
        [type]: pre[type] !== undefined ? !pre[type] : true,
      };
    });
  }, []);
  const test = `1. 개요
  주가지수는 경제 성장률 + 물가 상승률로서 1번 공식의 추정 PER을 ROE를 통해 적정 PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식임.
  
  2. 용어설명
  EPS(Earning per Share) : 기업이 벌어들인 순이익을 총 발행 주식수로 나눈 값이다.
  BPS(Book value per share) 주당 순자산가치, 기업 순자산/발행주식수
  ROE(Return On Equity) : 자본을 이용하여 얼마만큼의 이익을 냈는지를 나타내는 지표
  당기순이익/자본
  BPS(자본), ROE(성장수익)
  ex) 자본총액이 1억인 회사가 1000만원의 당기순이익을 냈다면 ROE 10%가 된다.
  
  3. 계산식
  EPS X ROE = 적정주가
  <ROE = 적정 PER 임을 산출>
  EPS X ROE = 적정주가
  ROE = 적정주가 X 1/EPS
  * EPS = 주당순이익 = 순이익/주식수
  → ROE = 적정주가 X 주식수/순이익
  → ROE = 적정시총 / 순이익 = 적정 PER
  (당해 년도 예상 EPS, ROE 사용)
  
  4. 데이터 참조
  네이버 finance > 종목 > 기업실적 분석 > EPS, ROE 
  
  5. 예외사항
  영업이익이 없어 EPS 및 ROE 가 산출되지 않는 기업의 경우 제외 한다.
  영업이익이 낮고 PER이 고평가 되는 기업의 경우 주가에 비해 상당히 낮은 수치로 반영 될 수 있다.`;
  return (
    <div className="main-area">
      <img src="/main.png"></img>
      <h2>
        PROPERSTOCK은 주식투자자들의 올바른 가치 투자를 위해 검증된 공식들로 산출한 적정주가를
        제공합니다.
      </h2>
      <div className="proper__desc__list">
        <div className="card proper__desc">
          <p className="card__title">EPS × PER</p>
          <div className="card__contents">
            <p className="proper-short__desc">
              순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한
              대중적으로 통용되는 가치평가 방법이다.
            </p>
            <pre className={`proper-long__desc ${expanded['EPSPER'] ? 'active' : ''}`}>{test}</pre>
            <div className="desc-expend" onClick={() => handleExpand('EPSPER')}>
              <FontAwesomeIcon icon={expanded['EPSPER'] ? faCaretUp : faCaretDown} />
            </div>
          </div>
        </div>

        <div className="card proper__desc">
          <p className="card__title">EPS × ROE</p>
          <div className="card__contents">
            <p className="proper-short__desc">
              주가지수는 경제 성장률 + 물가 상승률로서 EPSPER 공식의 추정 PER을 ROE를 통해 적정
              PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식이다.
            </p>
          </div>
        </div>

        <div className="card proper__desc">
          <p className="card__title">Smart Investor</p>
          <div className="card__contents">
            <p className="proper-short__desc">(사업가치 + 재산가치 - 고정부채) / 발행주식</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Main;