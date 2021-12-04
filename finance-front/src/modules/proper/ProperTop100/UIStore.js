import { makeAutoObservable } from 'mobx';
import ProperHttp from '../../../common/https/ProperHttp';

class UIStore {
  properPriceTop100 = [];
  formulaSymbol = '';
  goDetail = false;
  scrollPos = null;

  constructor() {
    makeAutoObservable(this);
  }

  init() {
    this.scrollPos = null;
    this.properPriceTop100 = [];
    this.formulaSymbol = '';
    this.showMoreFlag = [];
    this.goDetail = false;
  }

  *searchProperPrice() {
    this.properPriceTop100 = yield ProperHttp.searchProperPriceTop100(this.formulaSymbol);
  }

  changeType(type) {
    this.showMoreFlag = [];
    this.formulaSymbol = type.symbol;
    this.searchProperPrice();
  }

  setScrollStatus(x, y) {
    this.scrollPos = {
      x: x,
      y: y,
    };
  }

  setGoDetail(value) {
    this.goDetail = value;
  }
}

export default new UIStore();
