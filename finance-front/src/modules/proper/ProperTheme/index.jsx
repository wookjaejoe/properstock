import { observer } from 'mobx-react-lite';
import React, { useCallback } from 'react';
import { useHistory } from 'react-router';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';
import GlobalStore from '../../../store/GlobalStore';
import UIStore from './UIStore';

const ProperTheme = observer(() => {
  const history = useHistory();

  React.useEffect(() => {
    if (UIStore.goDetail) {
      GlobalStore.restoreScroll(UIStore.scrollPos);
      UIStore.setGoDetail(false);
    }

    return () => {
      if (!UIStore.goDetail) {
        UIStore.init();
      }
    };
  }, []);

  React.useEffect(() => {
    if (GlobalStore.formulas.length > 0) {
      if (!UIStore.formulaSymbol) {
        UIStore.changeType(GlobalStore.formulas[0]);
        UIStore.searchProperPrice();
      }
    }
  }, [GlobalStore.formulas]);

  React.useEffect(() => {
    if (GlobalStore.scroll) {
      UIStore.setScrollStatus(GlobalStore.scroll.x, GlobalStore.scroll.y);
    }
  }, [GlobalStore.scroll]);

  const goDetails = useCallback(
    (code) => {
      UIStore.setGoDetail(true);
      history.push(`/proper/${code}`);
    },
    [history]
  );
  return (
    <>
      <PageTitle title="적정주가 (테마 별 랭킹)" />
      <PageContents>
        <FilterContainer
          title="필터"
          onSubmit={() => UIStore.sumbit()}
          onClear={() => UIStore.clear()}
        >
          <FilterListItem
            title="테마"
            options={GlobalStore.theme}
            values={UIStore.selectedThemes}
            border={true}
            onChange={(item) => UIStore.changeFilter(item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector
          formulas={GlobalStore.formulas}
          onChange={(type) => UIStore.changeType(type)}
        ></TypeSelector>
        {Object.keys(UIStore.properPriceByTheme).map((themeKey, themeIdx) => {
          const tickerList = UIStore.properPriceByTheme[themeKey] || [];

          return (
            <div className="card mt" key={themeIdx}>
              <p className="card__title">{themeKey}</p>
              <div className="table__container">
                <table className="table custom-table">
                  <thead className="sticky">
                    <tr>
                      <th className="pc-only">종목 코드</th>
                      <th>종목 명</th>
                      <th className="pc-only">마켓</th>
                      <th className="pc-only">업종</th>
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
                    {tickerList.map((price, idx) => {
                      if (idx >= 5 && !UIStore.showMoreFlag.includes(themeKey)) {
                        return null;
                      }
                      const ticker = GlobalStore.tickers[price.tickerCode];
                      const margin = price.value - ticker.price;
                      const marginRate = (margin / ticker.price) * 100;
                      return (
                        <tr key={`${themeKey}_${idx}`}>
                          <td className="pc-only">{price.tickerCode}</td>
                          <td onClick={() => goDetails(price.tickerCode)} className="go-detail">
                            {price.tickerName}
                          </td>
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
              {tickerList.length > 5 && !UIStore.showMoreFlag.includes(themeKey) ? (
                <div className="more-action" onClick={() => UIStore.changeShowMore(themeKey)}>
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
});

export default ProperTheme;
