import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChartLine, faList, faListOl, faLayerGroup } from '@fortawesome/free-solid-svg-icons';
import { NavLink } from 'react-router-dom';

const AsideNav = () => {
  return (
    <aside>
      <div className="side-nav">
        <div className="logo">
          <img className="logo-normal" src="/logo.png"></img>
          <img className="logo-simple" src="/simple_logo.png"></img>
        </div>
        <div className="navigation">
          <ul className="section">
            <li>
              <div className="section__title">
                <FontAwesomeIcon icon={faChartLine} />
                <span className="section__label">적정주가</span>
              </div>

              <ul className="menu section__menu">
                <li>
                  <NavLink exact to="/proper/all" activeClassName="active">
                    <FontAwesomeIcon icon={faList} />
                    <span className="section__label">전체</span>
                  </NavLink>
                </li>
                <li>
                  <NavLink exact to="/proper/rank" activeClassName="active">
                    <FontAwesomeIcon icon={faListOl} />
                    <span className="section__label">랭킹 TOP 100</span>
                  </NavLink>
                </li>
                <li>
                  <NavLink exact to="/proper/rank/industry" activeClassName="active">
                    <FontAwesomeIcon icon={faLayerGroup} />
                    <span className="section__label">업종 별 랭킹</span>
                  </NavLink>
                </li>
                <li>
                  <NavLink exact to="/proper/rank/theme" activeClassName="active">
                    <FontAwesomeIcon icon={faLayerGroup} />
                    <span className="section__label">테마 별 랭킹</span>
                  </NavLink>
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
