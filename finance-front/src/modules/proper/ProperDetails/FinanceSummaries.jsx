import React from 'react';
import FinanceStatTable from './FinanceStatTable';
import PropTypes from 'prop-types';

const FinanceSummaries = ({ financeSummaries }) => {
  return (
    <>
      {financeSummaries?.QUARTER && (
        <div className="card mt">
          <p className="card__title" style={{ paddingTop: 0 }}>
            분기별
          </p>
          <div style={{ display: 'flex', flexWrap: 'wrap' }}>
            <FinanceStatTable stat={financeSummaries.QUARTER.controllingInterest} unit />
            <FinanceStatTable stat={financeSummaries.QUARTER.netProfit} unit />
            <FinanceStatTable stat={financeSummaries.QUARTER.operatingProfit} unit />
            <FinanceStatTable stat={financeSummaries.QUARTER.sales} unit />
            <FinanceStatTable stat={financeSummaries.QUARTER.issuedCommonShares} />
            <FinanceStatTable stat={financeSummaries.QUARTER.eps} />
            <FinanceStatTable stat={financeSummaries.QUARTER.per} />
            <FinanceStatTable stat={financeSummaries.QUARTER.roe} />
          </div>
        </div>
      )}
      {financeSummaries?.YEAR && (
        <div className="card mt">
          <p className="card__title" style={{ paddingTop: 0 }}>
            년도별
          </p>
          <div style={{ display: 'flex', flexWrap: 'wrap' }}>
            <FinanceStatTable stat={financeSummaries.YEAR.controllingInterest} unit />
            <FinanceStatTable stat={financeSummaries.YEAR.netProfit} unit />
            <FinanceStatTable stat={financeSummaries.YEAR.operatingProfit} unit />
            <FinanceStatTable stat={financeSummaries.YEAR.sales} unit />
            <FinanceStatTable stat={financeSummaries.YEAR.issuedCommonShares} />
            <FinanceStatTable stat={financeSummaries.YEAR.eps} />
            <FinanceStatTable stat={financeSummaries.YEAR.per} />
            <FinanceStatTable stat={financeSummaries.YEAR.roe} />
          </div>
        </div>
      )}
    </>
  );
};
FinanceSummaries.propTypes = {
  financeSummaries: PropTypes.object.isRequired,
};
export default FinanceSummaries;
