import PropTypes from 'prop-types';
import React from 'react';

const ProperPriceDetails = ({ ticker, properPrices }) => {
  return (
    <>
      {properPrices && ticker && (
        <div className="card mt">
          <p className="card__title" style={{ paddingTop: 0 }}>
            적정주가
          </p>
          <div style={{ fontSize: 12, display: 'flex', gap: 24, flexWrap: 'wrap' }}>
            {properPrices.map((properPrice, index) => {
              const margin = properPrice.value - ticker.price;
              const marginRate = (margin / ticker.price) * 100;
              return (
                <div key={index} style={{ flexGrow: 1 }}>
                  <div
                    style={{
                      fontSize: 13,
                      display: 'flex',
                      paddingTop: 8,
                      justifyContent: 'space-between',
                    }}
                  >
                    <div style={{ display: 'flex', gap: 16 }}>
                      {!isNaN(Number(properPrice.value)) && (
                        <>
                          <p>
                            <span style={{ marginRight: 8 }}>적정주가:</span>
                            {!isNaN(Number(properPrice.value)) &&
                              Number(properPrice.value).toLocaleString(0)}
                          </p>
                          <p>
                            <span style={{ marginRight: 8 }}>차액:</span>
                            <span className={margin > 0 ? 'font-green' : 'font-red'}>
                              {parseInt(margin).toLocaleString()}
                            </span>
                          </p>
                          <p>
                            <span style={{ marginRight: 8 }}>괴리율:</span>
                            <span className={marginRate > 0 ? 'font-green' : 'font-red'}>
                              {parseInt(marginRate)}%
                            </span>
                          </p>
                        </>
                      )}
                    </div>
                    <p>타입: {properPrice.formulaSymbol}</p>
                  </div>
                  {isNaN(Number(properPrice.value)) && (
                    <div
                      style={{
                        width: '100%',
                        marginTop: 8,
                        border: '1px solid rgb(255 255 255 / 10%)',
                        height: 70,
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                      }}
                    >
                      <p>{properPrice.note}</p>
                    </div>
                  )}

                  <table
                    className="table custom-table"
                    style={{
                      border: '1px solid rgb(255 255 255 / 10%)',
                    }}
                  >
                    <thead>
                      <tr>
                        {Object.keys(properPrice.arguments).map((argument) => {
                          return (
                            <th key={argument} className="number-cell">
                              {argument}
                            </th>
                          );
                        })}
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        {Object.keys(properPrice.arguments).map((argument) => {
                          const value = properPrice.arguments[argument];
                          return (
                            <td
                              key={argument}
                              style={{ padding: 8, textAlign: 'right' }}
                              className="number-cell"
                            >
                              {!isNaN(Number(value)) && Number(value) > 100000000
                                ? `${(Number(value) / 100000000).toFixed(1)} 억원`
                                : Number(value).toLocaleString(0)}
                            </td>
                          );
                        })}
                      </tr>
                    </tbody>
                  </table>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </>
  );
};

ProperPriceDetails.propTypes = {
  ticker: PropTypes.object.isRequired,
  properPrices: PropTypes.array.isRequired,
};

export default ProperPriceDetails;
