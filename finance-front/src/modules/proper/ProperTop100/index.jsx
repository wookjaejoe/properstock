import { observer } from 'mobx-react-lite';
import React, { useCallback } from 'react';
import { useHistory } from 'react-router';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import StockTable from '../../../common/components/StockTable/StockTable';
import TypeSelector from '../../../common/components/TypeSelector';
import GlobalStore from '../../../store/GlobalStore';
import UIStore from './UIStore';

const ProperTop100 = observer(() => {
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
      <PageTitle title="적정주가 (랭킹 Top 100)" />
      <PageContents>
        <TypeSelector
          formulas={GlobalStore.formulas}
          onChange={(type) => UIStore.changeType(type)}
        ></TypeSelector>
        <StockTable
          properPriceList={UIStore.properPriceTop100}
          onClick={(code) => UIStore.goDetails(code)}
        />
      </PageContents>
    </>
  );
});

export default ProperTop100;
