import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';

const FilterContainer = ({ title, onChange, children }) => {
  const [expanded, setExpanded] = useState(true);
  const handleChangeFilter = () => {
    onChange();
  };

  const handleChangeExpand = () => {
    setExpanded((prev) => !prev);
  };

  return (
    <div className="card mb proper__filter">
      <p className="card__title" onClick={handleChangeExpand}>
        {title}
      </p>
      <FontAwesomeIcon
        className="expanded__icon"
        icon={expanded ? faChevronUp : faChevronDown}
        onClick={handleChangeExpand}
      />
      <div className={`search-area ${expanded ? 'show' : ''}`}>
        {children}
        <div className="search__button__area">
          <button type="button" className="btn btn-primary" onClick={handleChangeFilter}>
            조회
          </button>
          <button type="button" className="btn btn-outline-primary">
            초기화
          </button>
        </div>
      </div>
    </div>
  );
};

FilterContainer.propTypes = {
  children: PropTypes.node.isRequired,
  title: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
};

export default FilterContainer;
