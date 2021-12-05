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
              <StockTable properPriceList={tickerList} onClick={(code) => goDetails(code)} />
            </div>
          );
        })}
      </PageContents>
    </>
  );
});

export default ProperTheme;
