import { faAngleDoubleLeft, faAngleDoubleRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React from 'react';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';

const ProperAllList = () => {
  const handleChangeFilter = (value) => {
    console.log(value);
  };
  return (
    <>
      <PageTitle title="적정주가 (전체)" />
      <PageContents>
        <FilterContainer title="필터" onChange={handleChangeFilter}>
          <FilterListItem title="마켓" items={['KOSDAQ', 'KOSPI']}></FilterListItem>
          <FilterListItem title="업종" items={['철강', '철강']} border={true}></FilterListItem>
          <FilterListItem
            title="테마"
            items={[
              '비철금속',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
              '북한 광물자원개발',
            ]}
            border={true}
          ></FilterListItem>
        </FilterContainer>
        <TypeSelector onChange={handleChangeFilter}></TypeSelector>
        <div className="card mt">
          <p className="card__title">조회 목록</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
