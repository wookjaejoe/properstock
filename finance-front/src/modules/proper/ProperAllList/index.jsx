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

const ProperAllList = observer(() => {
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

  const goDetail = useCallback(
    (code) => {
      UIStore.setGoDetail(true);
      history.push(`/proper/${code}`);
    },
    [history]
  );

  return (
    <>
      <PageTitle title="적정주가 (전체)" />
      <PageContents>
        <FilterContainer
          title="필터"
          onSubmit={() => UIStore.searchProperPrice()}
          onClear={() => UIStore.clear()}
        >
          <FilterListItem
            title="마켓"
            options={['KOSDAQ', 'KOSPI']}
            values={UIStore.filter['market'] || []}
            onChange={(item) => UIStore.changeFilter('market', item)}
          ></FilterListItem>
          <FilterListItem
            title="업종"
            options={GlobalStore.industryNames}
            values={UIStore.filter['industries'] || []}
            border={true}
            onChange={(item) => UIStore.changeFilter('industries', item)}
          ></FilterListItem>
          <FilterListItem
            title="테마"
            options={GlobalStore.theme}
            values={UIStore.filter['themes'] || []}
            border={true}
            onChange={(item) => UIStore.changeFilter('themes', item)}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector
          formulas={GlobalStore.formulas}
          onChange={(type) => UIStore.changeType(type)}
        ></TypeSelector>
        <div className="card mt">
          <p className="card__title">조회 목록</p>
          <StockTable
            properPriceList={UIStore.properPriceList}
            onClick={(code) => goDetails(code)}
          />

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
});

export default ProperAllList;
