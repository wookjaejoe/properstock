import axios from 'axios';
import React, { useCallback, useEffect, useState } from 'react';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import ProperHttp from '../../../common/https/ProperHttp';

const ProperTheme = () => {
  const [themes, setThemes] = useState([]);
  const [selectedThemes, setSelectedThemes] = useState([]);
  const [formulas, setFormulas] = useState([]);
  const [formulaSymbol, setFormulaSymbol] = useState('');
  const [tickerByTheme, setTckerByTheme] = useState({});
  const [showMoreFlag, setShowMoreFlag] = useState([]);

  useEffect(() => {
    axios.all([ProperHttp.searchThemeNames(), ProperHttp.searchFormulas()]).then(
      axios.spread((themeNames, formulas) => {
        setThemes(themeNames);
        setFormulas(formulas);
        setFormulaSymbol(formulas[0].symbol);
        ProperHttp.searchTickerByTheme({ formulaSymbol: formulas[0].symbol }).then((res) => {
          setTckerByTheme(res);
        });
      })
    );
  }, []);

  const filterTheme = useCallback((tickerByTheme, selectedThemes) => {
    const filtered = {};
    selectedThemes.forEach((key) => {
      if (tickerByTheme[key]) {
        filtered[key] = tickerByTheme[key];
      }
    });
    return filtered;
  }, []);

  const searchTickers = useCallback(
    (selectedThemes, formulaSymbol) => {
      ProperHttp.searchTickerByTheme({
        themes: selectedThemes,
        formulaSymbol: formulaSymbol,
      }).then((res) => {
        if (selectedThemes.length == 0) {
          setTckerByTheme(res);
        } else {
          setTckerByTheme(filterTheme(res, selectedThemes));
        }
      });
    },
    [filterTheme]
  );

  const handleSubmit = useCallback(() => {
    setShowMoreFlag([]);
    searchTickers(selectedThemes, formulaSymbol);
  }, [selectedThemes, formulaSymbol]);

  const handleClear = useCallback(() => {
    setSelectedThemes([]);
    ProperHttp.searchTickerByTheme({ formulaSymbol: formulaSymbol }).then((res) =>
      setTckerByTheme(res)
    );
  }, [formulaSymbol]);

  const handleChangeType = useCallback(
    (type) => {
      setShowMoreFlag([]);
      setFormulaSymbol(type.symbol);
      searchTickers(selectedThemes, type.symbol);
    },
    [selectedThemes]
  );

  const handleChangeFilter = useCallback((item) => {
    setSelectedThemes((pre) => {
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
      <PageTitle title="적정주가 (테마 별 랭킹)" />
      <PageContents>
        <FilterContainer title="필터" onSubmit={handleSubmit} onClear={handleClear}>
          <FilterListItem
            title="테마"
            options={themes}
            values={selectedThemes}
            border={true}
            onChange={(item) => handleChangeFilter(item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector formulas={formulas} onChange={handleChangeType}></TypeSelector>
        {Object.keys(tickerByTheme).map((themeKey, themeIdx) => {
          const tickerList = tickerByTheme[themeKey] || [];

          return (
            <div className="card mt" key={themeIdx}>
              <p className="card__title">{themeKey}</p>
              <table className="table custom-table">
                <thead>
                  <tr>
                    <th>No</th>
                    <th>종목 코드</th>
                    <th>종목 명</th>
                    <th className="pc-only">마켓</th>
                    <th className="pc-only">업종</th>
                    <th>현재 가격</th>
                    <th>적정 주가</th>
                    <th>차액</th>
                    <th>괴리율</th>
                    <th className="pc-only">비고</th>
                  </tr>
                </thead>
                <tbody>
                  {tickerList.map((ticker, idx) => {
                    if (idx >= 5 && !showMoreFlag.includes(themeKey)) {
                      return null;
                    }
                    return (
                      <tr key={`${themeKey}_${idx}`}>
                        <td>{idx + 1}</td>
                        <td>{ticker.tickerCode}</td>
                        <td>{ticker.tickerName}</td>
                        <td className="pc-only">
                          <span className={`badge ${ticker.tickerMarket.toLowerCase()}`}>
                            {ticker.tickerMarket}
                          </span>
                        </td>
                        <td className="pc-only">
                          <span>{ticker.tickerIndustry}</span>
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
                        <td className="pc-only">
                          <pre>{ticker.note}</pre>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
              {tickerList.length > 5 && !showMoreFlag.includes(themeKey) ? (
                <div className="more-action" onClick={() => handleShowMore(themeKey)}>
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

export default ProperTheme;
