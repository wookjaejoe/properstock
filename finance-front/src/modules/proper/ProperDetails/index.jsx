import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import ProperHttp from '../../../common/https/ProperHttp';
import FinanceStatTable from './FinanceStatTable';
import FinanceSummaries from './FinanceSummaries';
import ProperPriceDetails from './ProperPriceDetails';

const ProperDetails = () => {
  let { id } = useParams();
  const [financeSummaries, setSummaries] = useState(null);
  const [financeStat, setFinanceStat] = useState(null);
  const [ticker, setTicker] = useState(null);
  const [properPrices, setProperPrices] = useState(null);

  useEffect(() => {
    ProperHttp.searchDetails(id).then((res) => {
      setTicker(res.ticker);
      setFinanceStat(res.financeAnalysis?.financeStat);
      setSummaries(res.corpStat?.financeSummaries);
      setProperPrices(res.properPrices);
    });
  }, [id]);
  return (
    <>
      {ticker && (
        <div className="card mt">
          <div className="table__container">
            <table className="table custom-table">
              <thead>
                <tr>
                  <th style={{ width: 100, padding: '0.5rem' }}>종목 코드</th>
                  <th style={{ width: 200, padding: '0.5rem' }}>종목 명</th>
                  <th style={{ width: 100, padding: '0.5rem' }}>마켓</th>
                  <th style={{ width: 150, padding: '0.5rem' }} className="number-cell">
                    현재 가격
                  </th>
                  <th style={{ width: 150, padding: '0.5rem' }} className="number-cell">
                    목표 주가
                  </th>
                  <th style={{ width: 200, padding: '0.5rem' }} className="number-cell">
                    시가총액
                  </th>
                  <th style={{ width: 100, padding: '0.5rem' }} className="number-cell">
                    PER
                  </th>
                  <th style={{ width: 100, padding: '0.5rem' }} className="number-cell">
                    ROE
                  </th>
                  <th style={{ width: 150, padding: '0.5rem' }} className="number-cell">
                    상장주식수
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td style={{ padding: 8 }}>{ticker.code}</td>
                  <td style={{ padding: 8 }}>{ticker.name}</td>
                  <td style={{ padding: 8 }}>
                    <span className={`badge ${ticker.market.toLowerCase()}`}>{ticker.market}</span>
                  </td>
                  <td className="number-cell" style={{ padding: 8 }}>
                    {Number(ticker.price).toLocaleString()}
                  </td>
                  <td className="number-cell" style={{ padding: 8 }}>
                    <span>{ticker.targetPrice?.toLocaleString()}</span>
                  </td>
                  <td className="number-cell" style={{ padding: 8 }}>{`${(
                    ticker.marketCap / 100000000
                  ).toFixed(1)} 억원`}</td>
                  <td className="number-cell" style={{ padding: 8 }}>
                    {ticker.per}
                  </td>
                  <td className="number-cell" style={{ padding: 8 }}>
                    {ticker.roe}
                  </td>
                  <td className="number-cell" style={{ padding: 8 }}>
                    {Number(ticker.shares).toLocaleString()}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      )}
      {properPrices && ticker && <ProperPriceDetails ticker={ticker} properPrices={properPrices} />}
      {financeStat && (
        <div className="card mt">
          <div style={{ display: 'flex', flexWrap: 'wrap' }}>
            <FinanceStatTable stat={financeStat.currentAssets} unit />
            <FinanceStatTable stat={financeStat.currentLiabilities} unit />
            <FinanceStatTable stat={financeStat.investmentAssets} unit />
            <FinanceStatTable stat={financeStat.nonCurrentLiabilities} unit />
          </div>
        </div>
      )}
      {financeSummaries && <FinanceSummaries financeSummaries={financeSummaries} />}
    </>
  );
};

export default ProperDetails;
