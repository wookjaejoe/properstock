import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChartLine,
  faList,
  faListOl,
  faLayerGroup,
} from '@fortawesome/free-solid-svg-icons';

const AsideNav = () => {
  return (
    <aside>
      <div className="side-nav">
        <div className="logo">
          <img className="logo-normal" src="logo.png"></img>
          <img className="logo-simple" src="simple_logo.png"></img>
        </div>
        <div className="navigation">
          <ul className="section">
            <li>
              <div className="section__title">
                <FontAwesomeIcon icon={faChartLine} />
                <span className="section__label">적정주가</span>
              </div>

              <ul className="menu section__menu">
                <li className="active">
                  <a href="#">
                    <FontAwesomeIcon icon={faList} />
                    <span className="section__label">전체</span>
                  </a>
                </li>
                <li>
                  <a href="#">
                    <FontAwesomeIcon icon={faListOl} />
                    <span className="section__label">랭킹 TOP 100</span>
                  </a>
                </li>
                <li>
                  <a href="#">
                    <FontAwesomeIcon icon={faLayerGroup} />
                    <span className="section__label">섹터 별 랭킹</span>
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </aside>
  );
};

export default AsideNav;
