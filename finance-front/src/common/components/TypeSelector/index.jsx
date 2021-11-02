import { faChevronCircleDown } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import './TypeSelector.css';

const TypeSelector = ({ onChange }) => {
  const [show, setShow] = useState(false);
  const selectorRef = useRef();

  // 컴포넌트 외부 클릭시 이벤트 처리
  useEffect(() => {
    function handleClickOutside(event) {
      if (selectorRef.current && !selectorRef.current.contains(event.target)) {
        setShow(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // ESC 키 클릭시 이벤트 처리
  useEffect(() => {
    function handleEsc(event) {
      if (event.keyCode === 27) {
        setShow(false);
      }
    }
    document.addEventListener('keydown', handleEsc);
    return () => {
      document.removeEventListener('keydown', handleEsc);
    };
  }, []);

  const handleShow = useCallback(() => {
    setShow((prev) => !prev);
  }, []);

  const handleChangeType = useCallback(
    (type) => {
      onChange(type);
      setShow(false);
    },
    [onChange]
  );

  return (
    <div className="type__button__selector" ref={selectorRef}>
      <div className="card card__button" onClick={handleShow}>
        <div className="type__button">
          <p className="type__button__title">EPS × PER</p>
          <div className="type__button__desc">
            순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한
            대중적으로 통용되는 가치평가 방법이다.
          </div>
          <FontAwesomeIcon className="type__button__icon" icon={faChevronCircleDown} />
        </div>
      </div>
      <div className={`type__button__selector__list ${show ? 'active' : ''}`}>
        <div className="card card__button" onClick={() => handleChangeType('EPSROE')}>
          <div className="type__button">
            <p className="type__button__title">EPS × ROE</p>
            <div className="type__button__desc">
              주가지수는 경제 성장률 + 물가 상승률로서 EPSPER 공식의 추정 PER을 ROE를 통해 적정
              PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식이다.
            </div>
          </div>
        </div>
        <div className="card card__button" onClick={() => handleChangeType('EPSPER')}>
          <div className="type__button">
            <p className="type__button__title">EPS × PER</p>
            <div className="type__button__desc">
              순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한
              대중적으로 통용되는 가치평가 방법이다.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

TypeSelector.propTypes = {
  onChange: PropTypes.func.isRequired,
};

export default TypeSelector;
