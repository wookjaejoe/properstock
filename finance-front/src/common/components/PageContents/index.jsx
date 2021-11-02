import React from 'react';
import PropTypes from 'prop-types';

const PageContents = (props) => {
  return <div className="contents"> {props.children} </div>;
};

PageContents.propTypes = {
  children: PropTypes.node.isRequired,
};

export default PageContents;
