import { faCaretDown, faCaretUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useCallback, useState } from 'react';
import { useEffect } from 'react/cjs/react.development';
import ProperHttp from '../../common/https/ProperHttp';
import './Main.css';

const Main = () => {
  const [expanded, setExpanded] = useState({});
  const [data, setData] = useState([]);
  useEffect(() => {
    ProperHttp.searchFormulas().then((res) => setData(res));
  }, []);

  const handleExpand = useCallback((type) => {
    setExpanded((pre) => {
      return {
        ...pre,
        [type]: pre[type] !== undefined ? !pre[type] : true,
      };
    });
  }, []);

  return (
    <div className="main-area">
      <img src="/main.png"></img>
      <h2>
        <span>PROPERSTOCK</span>은 주식투자자들의 올바른 가치 투자를 위해 검증된 공식들로 산출한
        적정주가를 제공합니다.
      </h2>
      <div className="proper__desc__list">
        {data.map((formula, idx) => {
          return (
            <div className="card proper__desc" key={idx}>
              <p className="card__title">{formula.title}</p>
              <div className="card__contents">
                <p className="proper-short__desc">{formula.shortDescription}</p>
                <pre className={`proper-long__desc ${expanded[formula.symbol] ? 'active' : ''}`}>
                  {formula.longDescription}
                </pre>
                <div className="desc-expend" onClick={() => handleExpand(formula.symbol)}>
                  <FontAwesomeIcon icon={expanded[formula.symbol] ? faCaretUp : faCaretDown} />
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Main;
