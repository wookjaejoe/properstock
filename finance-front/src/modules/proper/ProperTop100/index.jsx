import React, { useCallback, useEffect, useState } from 'react';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import ProperHttp from '../../../common/https/ProperHttp';

const ProperTop100 = () => {
  const [formulas, setFormulas] = useState([]);
  const [tickerList, setTckerList] = useState([]);

  useEffect(() => {
    ProperHttp.searchFormulas().then((formulas) => {
      setFormulas(formulas);
      ProperHttp.searchTop100(formulas[0].symbol).then((tickerList) => {
        setTckerList(tickerList);
      });
    });
  }, []);

  const handleChangeType = useCallback((type) => {
    ProperHttp.searchTop100(type.symbol).then((tickerList) => {
      setTckerList(tickerList);
    });
  }, []);

  return (
    <>
      <PageTitle title="적정주가 (랭킹 Top 100)" />
      <PageContents>
        <TypeSelector formulas={formulas} onChange={handleChangeType}></TypeSelector>
        <table className="table custom-table">
          <thead>
            <tr>
              <th width="60px">No</th>
              <th width="80px">종목 코드</th>
              <th width="160px">종목 명</th>
              <th width="100px">마켓</th>
              <th width="200px">업종</th>
              <th width="300px">테마</th>
              <th width="100px">현재 가격</th>
              <th width="100px">적정 주가</th>
              <th width="100px">차액</th>
              <th width="80px">차액 비율</th>
              <th width="200px">비고</th>
            </tr>
          </thead>
          <tbody>
            {tickerList.map((ticker, idx) => {
              return (
                <tr key={idx}>
                  <td>{idx + 1}</td>
                  <td>{ticker.tickerCode}</td>
                  <td>{ticker.tickerName}</td>
                  <td>
                    <span className={`badge ${ticker.tickerMarket.toLowerCase()}`}>
                      {ticker.tickerMarket}
                    </span>
                  </td>
                  <td>
                    <span>{ticker.tickerIndustry}</span>
                  </td>

                  <td>
                    {ticker.tickerThemes.map((theme, index) => {
                      return (
                        <span className="badge" key={index}>
                          {theme}
                        </span>
                      );
                    })}
                  </td>
                  <td>
                    <span>{ticker.currentPrice.toLocaleString()}</span>
                  </td>
                  <td>
                    <span>{parseInt(ticker.value).toLocaleString()}</span>
                  </td>
                  <td>
                    <span className={ticker.margin > 0 ? 'font-green' : 'font-red'}>
                      {parseInt(ticker.margin).toLocaleString()}
                    </span>
                  </td>
                  <td>
                    <span className={ticker.marginRate > 0 ? 'font-green' : 'font-red'}>
                      {parseInt(ticker.marginRate)}%
                    </span>
                  </td>
                  <td>
                    <pre>{ticker.note}</pre>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </PageContents>
    </>
  );
};

export default ProperTop100;
