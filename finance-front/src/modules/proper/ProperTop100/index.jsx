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
        <div className="table__container">
          <table className="table custom-table">
            <thead>
              <tr>
                <th className="pc-only">종목 코드</th>
                <th>종목 명</th>
                <th className="pc-only">마켓</th>
                <th className="pc-only">업종</th>
                <th className="number-cell">현재 가격</th>
                <th className="number-cell">적정 주가</th>
                <th className="number-cell pc-only">차액</th>
                <th className="number-cell">괴리율</th>
                <th className="pc-only">비고</th>
              </tr>
            </thead>
            <tbody>
              {tickerList.map((ticker, idx) => {
                return (
                  <tr key={idx}>
                    <td className="pc-only">{ticker.tickerCode}</td>
                    <td>{ticker.tickerName}</td>
                    <td className="pc-only">
                      <span className={`badge ${ticker.tickerMarket.toLowerCase()}`}>
                        {ticker.tickerMarket}
                      </span>
                    </td>
                    <td className="pc-only">
                      <span>{ticker.tickerIndustry}</span>
                    </td>
                    <td className="number-cell">
                      <span>{ticker.currentPrice.toLocaleString()}</span>
                    </td>
                    <td className="number-cell">
                      <span>{parseInt(ticker.value).toLocaleString()}</span>
                    </td>
                    <td className="number-cell pc-only">
                      <span className={ticker.margin > 0 ? 'font-green' : 'font-red'}>
                        {parseInt(ticker.margin).toLocaleString()}
                      </span>
                    </td>
                    <td className="number-cell">
                      <span className={ticker.marginRate > 0 ? 'font-green' : 'font-red'}>
                        {parseInt(ticker.marginRate)}%
                      </span>
                    </td>
                    <td className="pc-only">
                      <pre>{ticker.note}</pre>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </PageContents>
    </>
  );
};

export default ProperTop100;
