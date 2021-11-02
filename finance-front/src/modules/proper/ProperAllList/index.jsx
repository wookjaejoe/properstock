import {
  faAngleDoubleLeft,
  faAngleDoubleRight,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';

const ProperAllList = () => {
  return (
    <>
      <PageTitle title="적정주가 (전체)" />

      <PageContents>
        <div className="card mb">
          <p className="card__title">필터</p>
          <div className="search-contidion">
            <p className="condition_title">산업</p>
            <div className="condition__area">
              <TypeSelector></TypeSelector>
            </div>
          </div>
          <div className="search-contidion">
            <p className="condition_title">산업</p>
            <div className="condition__area">
              <div className="condition__item active">에너지</div>
              <div className="condition__item active">원자재</div>
              <div className="condition__item">산업재</div>
              <div className="condition__item">헬스케어</div>
            </div>
          </div>
          <div className="search-contidion">
            <p className="condition_title">산업</p>
            <div className="condition__area">
              <div className="condition__item active">에너지</div>
              <div className="condition__item active">원자재</div>
              <div className="condition__item">산업재</div>
              <div className="condition__item">헬스케어</div>
            </div>
          </div>
        </div>
        <div className="card">
          <p className="card__title">조회 목록</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>산업</th>
                <th>테마</th>
                <th>현재 가격</th>
                <th>적정 주가</th>
                <th>차액</th>
                <th>차액 비율</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>009520</td>
                <td>포스코엠텍</td>
                <td>
                  <span className="badge kosdaq">KOSDAQ</span>
                </td>
                <td>
                  <span>철강</span>
                </td>

                <td>
                  <span className="badge">비철금속</span>
                  <span className="badge">북한 광물자원개발</span>
                </td>
                <td>
                  <span>8,100</span>
                </td>
                <td>
                  <span>6,812</span>
                </td>
                <td>
                  <span className="font-red">-1,287</span>
                </td>
                <td>
                  <span className="font-red">-15%</span>
                </td>
              </tr>
              <tr>
                <td>009520</td>
                <td>포스코엠텍</td>
                <td>
                  <span className="badge kospi">KOSPI</span>
                </td>
                <td>
                  <span>철강</span>
                </td>

                <td>
                  <span className="badge">비철금속</span>
                  <span className="badge">북한 광물자원개발</span>
                </td>
                <td>
                  <span>8,100</span>
                </td>
                <td>
                  <span>6,812</span>
                </td>
                <td>
                  <span className="font-green">-1,287</span>
                </td>
                <td>
                  <span className="font-green">-15%</span>
                </td>
              </tr>
            </tbody>
          </table>
          <div className="pagination">
            <span>
              <FontAwesomeIcon icon={faAngleDoubleLeft} />
            </span>
            <span className="active">1</span>
            <span>2</span>
            <span>
              <FontAwesomeIcon icon={faAngleDoubleRight} />
            </span>
          </div>
        </div>
      </PageContents>
    </>
  );
};

export default ProperAllList;
