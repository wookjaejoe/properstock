import { faChevronCircleDown } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import './TypeSelector.css';
import { observer } from 'mobx-react-lite';

const TypeSelector = ({ formulas, onChange }) => {
  const [show, setShow] = useState(false);
  const [selected, setSelected] = useState({});
  const selectorRef = useRef();

  useEffect(() => {
    setSelected(formulas[0]);
  }, [formulas]);

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
      console.log(type);
      setSelected(type);
      onChange(type);
      setShow(false);
    },
    [onChange]
  );

  return (
    <div className="type__button__selector" ref={selectorRef}>
      <div className="card card__button type__button__select" onClick={handleShow}>
        <div className="type__button">
          <p className="type__button__title">{selected?.title}</p>
          <div className="type__button__desc">{selected?.shortDescription}</div>
          <FontAwesomeIcon className="type__button__icon" icon={faChevronCircleDown} />
        </div>
      </div>
      <div className={`type__button__selector__list ${show ? 'active' : ''}`}>
        {formulas.map((formula, idx) => {
          return (
            <div className="card card__button" onClick={() => handleChangeType(formula)} key={idx}>
              <div className="type__button">
                <p className="type__button__title">{formula.title}</p>
                <div className="type__button__desc">{formula.shortDescription}</div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

TypeSelector.propTypes = {
  onChange: PropTypes.func.isRequired,
  formulas: PropTypes.array.isRequired,
};

export default TypeSelector;
