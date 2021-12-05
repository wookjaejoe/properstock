import { observer } from 'mobx-react-lite';
import React, { useCallback } from 'react';
import { useHistory } from 'react-router';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import StockTable from '../../../common/components/StockTable/StockTable';
import TypeSelector from '../../../common/components/TypeSelector';
import GlobalStore from '../../../store/GlobalStore';
import UIStore from './UIStore';

const ProperIndustry = observer(() => {
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
      <PageTitle title="적정주가 (업종 별 랭킹)" />
      <PageContents>
        <FilterContainer
          title="필터"
          onSubmit={() => UIStore.sumbit()}
          onClear={() => UIStore.clear()}
        >
          <FilterListItem
            title="업종"
            options={GlobalStore.industryNames}
            values={UIStore.selectedIndustries}
            border={true}
            onChange={(item) => UIStore.changeFilter(item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector
          formulas={GlobalStore.formulas}
          onChange={(type) => UIStore.changeType(type)}
        ></TypeSelector>
        {Object.keys(UIStore.properPriceByIndustry).map((key) => {
          const tickerList = UIStore.properPriceByIndustry[key] || [];
          return (
            <div className="card mt" key={key}>
              <p className="card__title">{key}</p>
              <StockTable
                properPriceList={tickerList}
                onClick={(code) => UIStore.goDetails(code)}
              />
              {tickerList.length > 5 && !UIStore.showMoreFlag.includes(key) ? (
                <div className="more-action" onClick={() => UIStore.changeShowMore(key)}>
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

export default ProperIndustry;
