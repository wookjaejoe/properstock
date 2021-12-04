import { makeAutoObservable } from 'mobx';
import ProperHttp from '../../../common/https/ProperHttp';

class UIStore {
  filter = {};
  formulaSymbol = '';
  properPriceList = [];
  scrollPos = null;
  goDetail = false;

  constructor() {
    makeAutoObservable(this);
  }

  init() {
    this.filter = {};
    this.formulaSymbol = '';
    this.properPriceList = [];
    this.showMoreCnt = 0;
    this.scrollPos = null;
  }

  *searchProperPrice() {
    this.properPriceList = yield ProperHttp.searchProperPrice({
      ...this.filter,
      formulaSymbol: this.formulaSymbol,
    });
  }

  clear() {
    this.filter = {};
    this.searchProperPrice();
  }

  changeType(type) {
    this.formulaSymbol = type.symbol;
    this.searchProperPrice();
  }

  setScrollStatus(x, y) {
    this.scrollPos = {
      x: x,
      y: y,
    };
  }

  changeFilter(key, item) {
    const preSelected = this.filter[key] || [];
    let currentSelected;
    if (preSelected.includes(item)) {
      currentSelected = preSelected.filter((p) => p !== item);
    } else {
      currentSelected = [...preSelected, item];
    }

    this.filter = {
      ...this.filter,
      [key]: currentSelected,
    };

    this.searchProperPrice();
  }

  setGoDetail(value) {
    this.goDetail = value;
  }
}

export default new UIStore();
