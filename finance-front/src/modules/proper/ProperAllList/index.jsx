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
  const [tickers, setTickers] = useState({});
  const [properPriceList, setProperPriceList] = useState([]);

  useEffect(() => {
    axios
      .all([
        ProperHttp.searchIndustryNames(),
        ProperHttp.searchThemeNames(),
        ProperHttp.searchFormulas(),
        ProperHttp.searchTickersByCode(),
      ])
      .then(
        axios.spread((industryNames, themeNames, formulas, tickers) => {
          setIndustries(industryNames);
          setThemes(themeNames);
          setFormulas(formulas);
          setFormulaSymbol(formulas[0].symbol);
          setTickers(tickers);
          ProperHttp.searchProperPrice({ formulaSymbol: formulas[0].symbol }).then((res) =>
            setProperPriceList(res)
          );
        })
      );
  }, []);

  const handleSubmit = useCallback(() => {
    ProperHttp.searchProperPrice({ ...filter, formulaSymbol: formulaSymbol }).then((res) =>
      setProperPriceList(res)
    );
  }, [filter, formulaSymbol]);

  const handleClear = useCallback(() => {
    setFilter({});
    ProperHttp.searchProperPrice({ ormulaSymbol: formulaSymbol }).then((res) =>
      setProperPriceList(res)
    );
  }, [formulaSymbol]);

  const handleChangeType = useCallback(
    (type) => {
      setFormulaSymbol(type.symbol);
      ProperHttp.searchProperPrice({ ...filter, formulaSymbol: type.symbol }).then((res) =>
        setProperPriceList(res)
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
                  <th className="number-cell">PER</th>
                  <th className="number-cell">ROE</th>
                  <th className="pc-only">참고 데이터</th>
                  <th className="pc-only">비고</th>
                </tr>
              </thead>
              <tbody>
                {properPriceList.map((price, idx) => {
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
                      <td className="pc-only">
                        <span>{ticker.industry}</span>
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
                      <td className="pc-only">
                        <pre>{price.note}</pre>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
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
