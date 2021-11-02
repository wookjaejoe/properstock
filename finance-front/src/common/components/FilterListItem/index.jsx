import React, { useCallback, useState } from 'react';
import PropTypes from 'prop-types';

const FilterListItem = ({ title, items, border }) => {
  const [selected, setSelected] = useState([]);

  const handleSelectItem = useCallback((item) => {
    setSelected((prev) => {
      if (prev.includes(item)) {
        return prev.filter((p) => p !== item);
      } else {
        return [...prev, item];
      }
    });
  }, []);

  const renderItem = useCallback(
    (element, index) => {
      return (
        <div
          className={`condition__item ${
            selected.includes(element) ? 'active' : ''
          } ${selected.includes(element)}`}
          key={index}
          onClick={() => {
            handleSelectItem(element);
          }}
        >
          {element}
        </div>
      );
    },
    [items, selected]
  );

  return (
    <div className="search-contidion">
      <p className="condition__title">{title}</p>
      <div className={`condition__area condition__list ${border ? 'condition-border' : ''}`}>
        {items.map(renderItem)}
      </div>
    </div>
  );
};
FilterListItem.defaultProps = {
  items: [],
};
FilterListItem.propTypes = {
  title: PropTypes.string.isRequired,
  items: PropTypes.array.isRequired,
  border: PropTypes.bool,
};

export default FilterListItem;
