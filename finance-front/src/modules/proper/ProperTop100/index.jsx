import axios from 'axios';
import React, { useCallback, useEffect, useState } from 'react';
import { useHistory } from 'react-router';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import ProperHttp from '../../../common/https/ProperHttp';

const ProperTop100 = () => {
  const [formulas, setFormulas] = useState([]);
  const [properPriceTop100, setProperPriceTop100] = useState([]);
  const [tickers, setTickers] = useState({});
  const history = useHistory();
  useEffect(() => {
    axios.all([ProperHttp.searchFormulas(), ProperHttp.searchTickersByCode()]).then(
      axios.spread((formulas, tickers) => {
        setFormulas(formulas);
        setTickers(tickers);
        console.log(formulas[0].symbol);
        ProperHttp.searchProperPriceTop100(formulas[0].symbol).then((res) => {
          setProperPriceTop100(res);
        });
      })
    );
  }, []);

  const handleChangeType = useCallback((type) => {
    ProperHttp.searchProperPriceTop100(type.symbol).then((properPriceTop100) => {
      setProperPriceTop100(properPriceTop100);
    });
  }, []);

  const goDetails = useCallback(
    (code) => {
      history.push(`/proper/${code}`);
    },
    [history]
  );

  return (
    <>
      <PageTitle title="적정주가 (랭킹 Top 100)" />
      <PageContents>
        <TypeSelector formulas={formulas} onChange={handleChangeType}></TypeSelector>
        <div className="table__container">
          <table className="table custom-table">
            <thead className="sticky">
              <tr>
                <th className="pc-only">종목 코드</th>
                <th>종목 명</th>
                <th className="pc-only">마켓</th>
                <th className="pc-only">업종</th>
                <th className="number-cell">현재 가격</th>
                <th className="number-cell">적정 주가</th>
                <th className="number-cell pc-only">차액</th>
                <th className="number-cell">괴리율</th>
                <th className="number-cell">PER</th>
                <th className="number-cell">ROE</th>
                <th className="pc-only">참고 데이터</th>
              </tr>
            </thead>
            <tbody>
              {properPriceTop100.map((price, idx) => {
                const ticker = tickers[price.tickerCode];
                const margin = price.value - ticker.price;
                const marginRate = (margin / ticker.price) * 100;
                return (
                  <tr key={idx}>
                    <td className="pc-only">{price.tickerCode}</td>
                    <td onClick={() => goDetails(price.tickerCode)} className="go-detail">
                      {price.tickerName}
                    </td>
                    <td className="pc-only">
                      <span className={`badge ${price.tickerMarket.toLowerCase()}`}>
                        {price.tickerMarket}
                      </span>
                    </td>
                    <td className="pc-only">
                      <span>{price.tickerIndustry}</span>
                    </td>
                    <td className="number-cell">
                      <span>{price.currentPrice.toLocaleString()}</span>
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
      </PageContents>
    </>
  );
};

export default ProperTop100;
