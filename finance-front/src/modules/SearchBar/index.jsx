import { faSearch, faTimes } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useCallback, useEffect, useState } from 'react';
import ProperHttp from '../../common/https/ProperHttp';

const SearchBar = () => {
  const [searchText, setSearchText] = useState('');
  const [result, setResult] = useState({});
  const [show, setShow] = useState(false);
  const [formulas, setFormulas] = useState({});

  useEffect(() => {
    ProperHttp.searchFormulas().then((formulas) => {
      const map = {};
      formulas.forEach((formula) => (map[formula.symbol] = formula.title));
      setFormulas(map);
    });
  }, []);

  const handleChangeValue = useCallback((e) => {
    setSearchText(e.target.value);
  }, []);

  const handleClear = useCallback(() => {
    setSearchText('');
    setResult({});
    setShow(false);
  }, []);

  // ESC 키 클릭시 이벤트 처리
  useEffect(() => {
    function handleEsc(event) {
      if (event.keyCode === 27) {
        setResult({});
        setShow(false);
      }
    }
    document.addEventListener('keydown', handleEsc);
    return () => {
      document.removeEventListener('keydown', handleEsc);
    };
  }, []);

  const handleKeyEvent = useCallback(
    (event) => {
      if (event.keyCode === 13 && searchText.length > 0) {
        ProperHttp.searchProperPriceByName({ searchText: searchText }).then((result) => {
          setShow(true);
          setResult(result);
        });
      }
    },
    [searchText]
  );

  return (
    <div className="searchbar__container">
      <div className="searchbar">
        <FontAwesomeIcon className="search__icon" icon={faSearch}></FontAwesomeIcon>
        <input
          type="text"
          placeholder="종목 코드 또는 종목 명을 입력하세요."
          value={searchText}
          onChange={handleChangeValue}
          onKeyUp={handleKeyEvent}
        />
        <span>
          <FontAwesomeIcon
            className={`input-clear ${searchText.length > 0 ? 'active' : ''}`}
            icon={faTimes}
            onClick={handleClear}
          ></FontAwesomeIcon>
        </span>
      </div>
      <div className="search-result">
        {Object.keys(result).map((key) => {
          const tickerList = result[key] || [];
          return (
            <div className="result__item" key={key}>
              <div className="result__item__header">
                <div>
                  <span>[{tickerList[0].tickerCode}]</span>
                  <span>{key}</span>
                </div>
                <div>
                  <span className={`badge ${tickerList[0].tickerMarket.toLowerCase()}`}>
                    {tickerList[0].tickerMarket}
                  </span>
                </div>
              </div>

              <div className="result__item__proper__container">
                {tickerList.map((ticker, idx) => {
                  return (
                    <div className="result__item__proper" key={idx}>
                      <p>{formulas[ticker.formulaSymbol]}</p>
                      <div className="result__item__proper__result">
                        <p>
                          현재가: <span>{ticker.currentPrice.toLocaleString()}</span>
                        </p>
                        <p>
                          적정주가: <span> {parseInt(ticker.value).toLocaleString()}</span>
                        </p>
                        <p>
                          차액:
                          <span className={ticker.margin > 0 ? 'font-green' : 'font-red'}>
                            {parseInt(ticker.margin).toLocaleString()}
                          </span>
                        </p>
                        <p>
                          괴리율:
                          <span className={ticker.marginRate > 0 ? 'font-green' : 'font-red'}>
                            {parseInt(ticker.marginRate)}%
                          </span>
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })}

        {show && Object.keys(result).length === 0 && (
          <div className="no-result">
            <span>No Result</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchBar;
