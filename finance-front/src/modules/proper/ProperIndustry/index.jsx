import axios from 'axios';
import React, { useCallback, useEffect, useState } from 'react';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import ProperHttp from '../../../common/https/ProperHttp';

const ProperIndustry = () => {
  const [industries, setIndustries] = useState([]);
  const [selectedIndustries, setSelectedIndustries] = useState([]);
  const [formulas, setFormulas] = useState([]);
  const [formulaSymbol, setFormulaSymbol] = useState('');
  const [tickers, setTickers] = useState({});
  const [properPriceByIndustry, setProperPriceByIndustry] = useState({});
  const [showMoreFlag, setShowMoreFlag] = useState([]);

  useEffect(() => {
    axios
      .all([
        ProperHttp.searchIndustryNames(),
        ProperHttp.searchFormulas(),
        ProperHttp.searchTickersByCode(),
      ])
      .then(
        axios.spread((industryNames, formulas, tickers) => {
          setIndustries(industryNames);
          setFormulas(formulas);
          setFormulaSymbol(formulas[0].symbol);
          setTickers(tickers);
          ProperHttp.searchProperPriceByIndustry({ formulaSymbol: formulas[0].symbol }).then(
            (res) => {
              setProperPriceByIndustry(res);
            }
          );
        })
      );
  }, []);

  const handleSubmit = useCallback(() => {
    setShowMoreFlag([]);
    ProperHttp.searchProperPriceByIndustry({
      industries: selectedIndustries,
      formulaSymbol: formulaSymbol,
    }).then((res) => setProperPriceByIndustry(res));
  }, [selectedIndustries, formulaSymbol]);

  const handleClear = useCallback(() => {
    setSelectedIndustries([]);
    ProperHttp.searchProperPriceByIndustry({ formulaSymbol: formulaSymbol }).then((res) =>
      setProperPriceByIndustry(res)
    );
  }, [formulaSymbol]);

  const handleChangeType = useCallback(
    (type) => {
      setShowMoreFlag([]);
      setFormulaSymbol(type.symbol);
      ProperHttp.searchProperPriceByIndustry({
        industries: selectedIndustries,
        formulaSymbol: type.symbol,
      }).then((res) => setProperPriceByIndustry(res));
    },
    [selectedIndustries]
  );

  const handleChangeFilter = useCallback((item) => {
    setSelectedIndustries((pre) => {
      let currentSelected;
      if (pre.includes(item)) {
        currentSelected = pre.filter((p) => p !== item);
      } else {
        currentSelected = [...pre, item];
      }

      return currentSelected;
    });
  }, []);

  const handleShowMore = useCallback((key) => {
    setShowMoreFlag((prev) => {
      return [...prev, key];
    });
  }, []);

  return (
    <>
      <PageTitle title="적정주가 (업종 별 랭킹)" />
      <PageContents>
        <FilterContainer title="필터" onSubmit={handleSubmit} onClear={handleClear}>
          <FilterListItem
            title="업종"
            options={industries}
            values={selectedIndustries}
            border={true}
            onChange={(item) => handleChangeFilter(item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector formulas={formulas} onChange={handleChangeType}></TypeSelector>
        {Object.keys(properPriceByIndustry).map((key) => {
          const tickerList = properPriceByIndustry[key] || [];
          return (
            <div className="card mt" key={key}>
              <p className="card__title">{key}</p>
              <div className="table__container">
                <table className="table custom-table">
                  <thead>
                    <tr>
                      <th className="pc-only">종목 코드</th>
                      <th>종목 명</th>
                      <th className="pc-only">마켓</th>
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
                    {tickerList.map((price, idx) => {
                      if (idx >= 5 && !showMoreFlag.includes(key)) {
                        return null;
                      }
                      const ticker = tickers[price.tickerCode];
                      const margin = price.value - ticker.price;
                      const marginRate = (margin / ticker.price) * 100;
                      return (
                        <tr key={idx}>
                          <td className="pc-only">{price.tickerCode}</td>
                          <td>{price.tickerName}</td>
                          <td className="pc-only">
                            <span className={`badge ${ticker.market.toLowerCase()}`}>
                              {ticker.market}
                            </span>
                          </td>
                          <td className="number-cell">
                            <span>{ticker.price.toLocaleString()}</span>
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
              {tickerList.length > 5 && !showMoreFlag.includes(key) ? (
                <div className="more-action" onClick={() => handleShowMore(key)}>
                  더 보기
                </div>
              ) : (
                <></>
              )}
            </div>
          );
        })}
      </PageContents>
    </>
  );
};

export default ProperIndustry;
