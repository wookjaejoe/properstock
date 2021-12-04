import React, { useCallback } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react-lite';

const FilterListItem = ({ title, options, values, border, onChange }) => {
  const handleSelectItem = useCallback(
    (item) => {
      onChange(item);
    },
    [onChange]
  );

  return (
    <div className="search-contidion">
      <p className="condition__title">{title}</p>
      <div className={`condition__area condition__list ${border ? 'condition-border' : ''}`}>
        {options.map((element, index) => {
          return (
            <div
              className={`condition__item ${
                values.includes(element) ? 'active' : ''
              } ${values.includes(element)}`}
              key={index}
              onClick={() => {
                handleSelectItem(element);
              }}
            >
              {element}
            </div>
          );
        })}
      </div>
    </div>
  );
};

FilterListItem.defaultProps = {
  options: [],
};

FilterListItem.propTypes = {
  title: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  values: PropTypes.array.isRequired,
  border: PropTypes.bool,
  onChange: PropTypes.func.isRequired,
};

export default FilterListItem;
