import {
  faArrowAltCircleLeft,
  faList,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import PropTypes from 'prop-types';

const PageTitle = ({ title, back }) => {
  return (
    <div className="page__title">
      <h4>
        <FontAwesomeIcon icon={faList} />
        <span>{title}</span>
      </h4>
      {back && (
        <div className="page__back">
          <FontAwesomeIcon icon={faArrowAltCircleLeft} />
        </div>
      )}
    </div>
  );
};

PageTitle.propTypes = {
  title: PropTypes.string.isRequired,
  back: PropTypes.bool,
};

export default PageTitle;
