import axios from 'axios';
import React, { useCallback, useEffect, useState } from 'react';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import ProperHttp from '../../../common/https/ProperHttp';

const ProperAllList = () => {
  const [industries, setIndustries] = useState([]);
  const [themes, setThemes] = useState([]);
  const [filter, setFilter] = useState({});
  const [formulas, setFormulas] = useState([]);
  const [formulaSymbol, setFormulaSymbol] = useState('');
  const [tickerList, setTckerList] = useState([]);

  useEffect(() => {
    axios
      .all([
        ProperHttp.searchIndustryNames(),
        ProperHttp.searchThemeNames(),
        ProperHttp.searchFormulas(),
      ])
      .then(
        axios.spread((industryNames, themeNames, formulas) => {
          setIndustries(industryNames);
          setThemes(themeNames);
          setFormulas(formulas);
          setFormulaSymbol(formulas[0].symbol);
          ProperHttp.searchTickerList({ formulaSymbol: formulas[0].symbol }).then((res) =>
            setTckerList(res)
          );
        })
      );
  }, []);

  const handleSubmit = useCallback(() => {
    ProperHttp.searchTickerList({ ...filter, formulaSymbol: formulaSymbol }).then((res) =>
      setTckerList(res)
    );
  }, [filter, formulaSymbol]);

  const handleClear = useCallback(() => {
    setFilter({});
    ProperHttp.searchTickerList({ ormulaSymbol: formulaSymbol }).then((res) => setTckerList(res));
  }, [formulaSymbol]);

  const handleChangeType = useCallback(
    (type) => {
      setFormulaSymbol(type.symbol);
      ProperHttp.searchTickerList({ ...filter, formulaSymbol: type.symbol }).then((res) =>
        setTckerList(res)
      );
    },
    [filter]
  );

  const handleChangeFilter = useCallback((key, item) => {
    setFilter((pre) => {
      const preSelected = pre[key] || [];
      let currentSelected;
      if (preSelected.includes(item)) {
        currentSelected = preSelected.filter((p) => p !== item);
      } else {
        currentSelected = [...preSelected, item];
      }

      return {
        ...pre,
        [key]: currentSelected,
      };
    });
  }, []);

  return (
    <>
      <PageTitle title="적정주가 (전체)" />
      <PageContents>
        <FilterContainer title="필터" onSubmit={handleSubmit} onClear={handleClear}>
          <FilterListItem
            title="마켓"
            options={['KOSDAQ', 'KOSPI']}
            values={filter['market'] || []}
            onChange={(item) => handleChangeFilter('market', item)}
          ></FilterListItem>
          <FilterListItem
            title="업종"
            options={industries}
            values={filter['industries'] || []}
            border={true}
            onChange={(item) => handleChangeFilter('industries', item)}
          ></FilterListItem>
          <FilterListItem
            title="테마"
            options={themes}
            values={filter['themes'] || []}
            border={true}
            onChange={(item) => handleChangeFilter('themes', item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector formulas={formulas} onChange={handleChangeType}></TypeSelector>
        <div className="card mt">
          <p className="card__title">조회 목록</p>
          <table className="table custom-table">
            <thead>
              <tr>
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
                return (
                  <tr key={idx}>
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
          {/* <div className="pagination">
            <span>
              <FontAwesomeIcon icon={faAngleDoubleLeft} />
            </span>
            <span className="active">1</span>
            <span>2</span>
            <span>
              <FontAwesomeIcon icon={faAngleDoubleRight} />
            </span>
          </div> */}
        </div>
      </PageContents>
    </>
  );
};

export default ProperAllList;
