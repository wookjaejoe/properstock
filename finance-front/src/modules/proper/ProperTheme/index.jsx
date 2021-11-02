import React from 'react';
import FilterContainer from '../../../common/components/FilterContainer';
import FilterListItem from '../../../common/components/FilterListItem';
import PageContents from '../../../common/components/PageContents';
import PageTitle from '../../../common/components/PageTitle';
import TypeSelector from '../../../common/components/TypeSelector';

const ProperTheme = () => {
  const handleChangeType = () => {};
  const handleChangeFilter = () => {};
  return (
    <>
      <PageTitle title="적정주가 (테마 별 랭킹)" />
      <PageContents>
        <FilterContainer title="필터" onChange={handleChangeFilter}>
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
        <TypeSelector onChange={handleChangeType}></TypeSelector>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">북한 광물자원개발</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
        <div className="card mt">
          <p className="card__title">비철금속</p>
          <table className="table custom-table">
            <thead>
              <tr>
                <th>종목 코드</th>
                <th>종목 명</th>
                <th>마켓</th>
                <th>업종</th>
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
          <div className="more-action">더 보기</div>
        </div>
      </PageContents>
    </>
  );
};

export default ProperTheme;
