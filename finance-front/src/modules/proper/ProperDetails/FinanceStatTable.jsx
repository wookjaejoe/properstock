import React from 'react';
import PropTypes from 'prop-types';

const FinanceStatTable = ({ stat }) => {
  return (
    <>
      {stat && (
        <div className="card" style={{ flexGrow: 1 }}>
          <p className="card__title" style={{ paddingTop: 0 }}>
            {stat.displayName}
          </p>
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
                    <th style={{ background: 'rgb(102 102 102 / 24%)', padding: 8 }}>{date}</th>
                    <td style={{ padding: 8, textAlign: 'right' }}>
                      {!isNaN(Number(value)) && Number(value).toLocaleString(0)}
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
};

export default FinanceStatTable;
