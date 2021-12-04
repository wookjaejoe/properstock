import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import React from 'react';
import AsideNav from '../AsideNav';
import ProperAllList from '../proper/ProperAllList';
import ProperIndustry from '../proper/ProperIndustry';
import ProperTheme from '../proper/ProperTheme';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import ProperTop100 from '../proper/ProperTop100';
import Main from '../Main';
import SearchBar from '../SearchBar';
import ProperDetails from '../proper/ProperDetails';

const Dashboard = () => {
  return (
    <>
      <Router>
        <input type="checkbox" id="side-toggle__input" />
        <label htmlFor="side-toggle__input" className="side-toggle__icon">
          <FontAwesomeIcon icon={faBars} />
        </label>
        <AsideNav />
        <SearchBar />
        <div className="container">
          <Switch>
            <Route exact path="/proper/all" component={ProperAllList} />
            <Route exact path="/proper/rank" component={ProperTop100} />
            <Route exact path="/proper/rank/industry" component={ProperIndustry} />
            <Route exact path="/proper/rank/theme" component={ProperTheme} />
            <Route exact path="/proper/:id" component={ProperDetails} />
            <Route exact path="/" component={Main} />
          </Switch>
        </div>
      </Router>
    </>
  );
};

Dashboard.propTypes = {};

export default Dashboard;
