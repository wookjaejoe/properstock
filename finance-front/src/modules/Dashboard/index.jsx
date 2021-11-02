import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import React from 'react';
import AsideNav from '../AsideNav';
import ProperAllList from '../proper/ProperAllList';
// import ProperTop100 from '../proper/ProperTop100';

const Dashboard = () => {
  return (
    <>
      <input type="checkbox" id="side-toggle__input" />
      <label htmlFor="side-toggle__input" className="side-toggle__icon">
        <FontAwesomeIcon icon={faBars} />
      </label>
      <AsideNav />

      <div className="container">
        {/* <ProperTop100 /> */}
        <ProperAllList />
      </div>
    </>
  );
};

Dashboard.propTypes = {};

export default Dashboard;
