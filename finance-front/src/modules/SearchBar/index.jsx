import { faSearch, faTimes } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useState } from 'react';
import { useCallback } from 'react/cjs/react.development';

const SearchBar = () => {
  const [searchText, setSearchText] = useState('');
  const handleChangeValue = useCallback((e) => {
    setSearchText(e.target.value);
  }, []);

  const handleClear = useCallback(() => {
    setSearchText('');
  }, []);

  return (
    <div className="searchbar__container">
      <FontAwesomeIcon className="search__icon" icon={faSearch}></FontAwesomeIcon>
      <input
        type="text"
        placeholder="종목명을 입력하세요."
        value={searchText}
        onChange={handleChangeValue}
      />
      <span>
        <FontAwesomeIcon
          className={`input-clear ${searchText.length > 0 ? 'active' : ''}`}
          icon={faTimes}
          onClick={handleClear}
        ></FontAwesomeIcon>
      </span>
    </div>
  );
};

export default SearchBar;
