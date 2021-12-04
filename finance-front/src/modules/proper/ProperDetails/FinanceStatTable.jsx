import React from 'react';
import PropTypes from 'prop-types';

const FinanceStatTable = ({ stat, unit }) => {
  return (
    <>
      {stat && (
        <div className="card" style={{ flexGrow: 1 }}>
          <div
            className="card__title"
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              paddingTop: 0,
              paddingBottom: 4,
              alignItems: 'end',
            }}
          >
            <p style={{ fontSize: 12, fontWeight: 'normal' }}>{stat.displayName}</p>
            <p style={{ fontSize: 11, fontWeight: 'normal' }}>
              {unit && <span>(단위: 억원)</span>}
            </p>
          </div>
          <table
            className="table custom-table"
            style={{
              border: '1px solid rgb(255 255 255 / 10%)',
              width: '100%',
            }}
          >
            <tbody>
              {Object.keys(stat.data).map((date) => {
                const value = stat.data[date];
                return (
                  <tr key={date}>
                    <th style={{ background: 'rgb(102 102 102 / 24%)', padding: 8, width: 80 }}>
                      {date}
                    </th>
                    <td style={{ padding: 8, textAlign: 'right' }}>
                      {unit && <span>{(value / 100000000).toFixed(1)}</span>}

                      {!unit && !isNaN(Number(value)) && Number(value).toLocaleString(0)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
};

FinanceStatTable.propTypes = {
  stat: PropTypes.object.isRequired,
  unit: PropTypes.bool,
};

export default FinanceStatTable;
