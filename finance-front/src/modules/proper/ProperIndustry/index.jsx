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
  const [tickerByIndustry, setTckerByIndustry] = useState({});
  const [showMoreFlag, setShowMoreFlag] = useState([]);

  useEffect(() => {
    axios.all([ProperHttp.searchIndustryNames(), ProperHttp.searchFormulas()]).then(
      axios.spread((industryNames, formulas) => {
        setIndustries(industryNames);
        setFormulas(formulas);
        setFormulaSymbol(formulas[0].symbol);
        ProperHttp.searchTickerByIndustry({ formulaSymbol: formulas[0].symbol }).then((res) => {
          setTckerByIndustry(res);
        });
      })
    );
  }, []);

  const handleSubmit = useCallback(() => {
    setShowMoreFlag([]);
    ProperHttp.searchTickerByIndustry({
      industries: selectedIndustries,
      formulaSymbol: formulaSymbol,
    }).then((res) => setTckerByIndustry(res));
  }, [selectedIndustries, formulaSymbol]);

  const handleClear = useCallback(() => {
    setSelectedIndustries([]);
    ProperHttp.searchTickerByIndustry({ formulaSymbol: formulaSymbol }).then((res) =>
      setTckerByIndustry(res)
    );
  }, [formulaSymbol]);

  const handleChangeType = useCallback(
    (type) => {
      setShowMoreFlag([]);
      setFormulaSymbol(type.symbol);
      ProperHttp.searchTickerByIndustry({
        industries: selectedIndustries,
        formulaSymbol: type.symbol,
      }).then((res) => setTckerByIndustry(res));
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
        {Object.keys(tickerByIndustry).map((key) => {
          const tickerList = tickerByIndustry[key] || [];
          return (
            <div className="card mt" key={key}>
              <p className="card__title">{key}</p>
              <div className="table__container">
                <table className="table custom-table">
                  <thead>
                    <tr>
                      <th>종목 코드</th>
                      <th>종목 명</th>
                      <th className="pc-only">마켓</th>
                      <th className="number-cell">현재 가격</th>
                      <th className="number-cell">적정 주가</th>
                      <th className="number-cell">차액</th>
                      <th className="number-cell">괴리율</th>
                      <th className="pc-only">비고</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tickerList.map((ticker, idx) => {
                      if (idx >= 5 && !showMoreFlag.includes(key)) {
                        return null;
                      }
                      return (
                        <tr key={idx}>
                          <td>{ticker.tickerCode}</td>
                          <td>{ticker.tickerName}</td>
                          <td className="pc-only">
                            <span className={`badge ${ticker.tickerMarket.toLowerCase()}`}>
                              {ticker.tickerMarket}
                            </span>
                          </td>
                          <td className="number-cell">
                            <span>{ticker.currentPrice.toLocaleString()}</span>
                          </td>
                          <td className="number-cell">
                            <span>{parseInt(ticker.value).toLocaleString()}</span>
                          </td>
                          <td className="number-cell">
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
