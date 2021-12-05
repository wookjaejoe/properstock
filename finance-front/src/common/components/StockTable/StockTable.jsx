import { observer } from 'mobx-react-lite';
import React from 'react';
import PropTypes from 'prop-types';
import GlobalStore from '../../../store/GlobalStore';
const StockTable = observer(({ properPriceList, onClick }) => {
  return (
    <div className="table__container">
      <table className="table custom-table">
        <thead className="sticky">
          <tr>
            <th className="pc-only">종목 코드</th>
            <th>종목 명</th>
            <th className="pc-only">마켓</th>
            <th className="number-cell">현재 가격</th>
            <th className="number-cell">목표 주가</th>
            <th className="number-cell">적정 주가</th>
            <th className="number-cell pc-only">차액</th>
            <th className="number-cell">괴리율</th>
            <th className="number-cell">PER</th>
            <th className="number-cell">ROE</th>
            <th className="pc-only">참고 데이터</th>
          </tr>
        </thead>
        <tbody>
          {properPriceList.map((price, idx) => {
            const ticker = GlobalStore.tickers[price.tickerCode];
            const margin = price.value - ticker.price;
            const marginRate = (margin / ticker.price) * 100;
            return (
              <tr key={idx}>
                <td className="pc-only">{price.tickerCode}</td>
                <td onClick={() => onClick(price.tickerCode)} className="go-detail">
                  {price.tickerName}
                </td>
                <td className="pc-only">
                  <span className={`badge ${ticker.market.toLowerCase()}`}>{ticker.market}</span>
                </td>
                <td className="number-cell">
                  <span>{ticker.price.toLocaleString()}</span>
                </td>
                <td className="number-cell">
                  <span>{ticker.targetPrice?.toLocaleString()}</span>
                </td>
                <td className="number-cell">
                  <span>{parseInt(price.value).toLocaleString()}</span>
                </td>
                <td className="number-cell pc-only">
                  <span className={margin > 0 ? 'font-green' : 'font-red'}>
                    {parseInt(margin).toLocaleString()}
                  </span>
                </td>
                <td className="number-cell">
                  <span className={marginRate > 0 ? 'font-green' : 'font-red'}>
                    {parseInt(marginRate)}%
                  </span>
                </td>
                <td className="number-cell">
                  <span>{ticker.per}</span>
                </td>
                <td className="number-cell">
                  <span>{!Number.isNaN(Number.parseInt(ticker.roe)) && ticker.roe}</span>
                </td>
                <td className="pc-only">
                  {ticker.externalLinks.map((link, index) => {
                    return (
                      <a
                        className="external_link"
                        href={link.url}
                        key={index}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <span className="badge">{link.domainName}</span>
                      </a>
                    );
                  })}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
});

StockTable.propTypes = {
  properPriceList: PropTypes.array.isRequired,
  onClick: PropTypes.func.isRequired,
};

export default StockTable;
