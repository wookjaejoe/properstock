import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import React, { useRef } from 'react';
import AsideNav from '../AsideNav';
import ProperAllList from '../proper/ProperAllList';
import ProperIndustry from '../proper/ProperIndustry';
import ProperTheme from '../proper/ProperTheme';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import ProperTop100 from '../proper/ProperTop100';
import Main from '../Main';
import SearchBar from '../SearchBar';
import ProperDetails from '../proper/ProperDetails';
import { observer } from 'mobx-react-lite';
import GlobalStore from '../../store/GlobalStore';

const Dashboard = observer(() => {
  const containerRef = useRef();
  React.useEffect(() => {
    if (containerRef.current) GlobalStore.init(containerRef.current);
  }, [containerRef]);

  return (
    <>
      <Router>
        <input type="checkbox" id="side-toggle__input" />
        <label htmlFor="side-toggle__input" className="side-toggle__icon">
          <FontAwesomeIcon icon={faBars} />
        </label>
        <AsideNav />
        <SearchBar />
        <div
          ref={containerRef}
          className="container"
          onScroll={(e) => GlobalStore.setScrollStatus(e.target.scrollLeft, e.target.scrollTop)}
        >
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
});

Dashboard.propTypes = {};

export default Dashboard;
