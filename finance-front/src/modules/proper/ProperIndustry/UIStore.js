import { makeAutoObservable } from 'mobx';
import ProperHttp from '../../../common/https/ProperHttp';

class UIStore {
  selectedIndustries = [];
  formulaSymbol = '';
  properPriceByIndustry = {};
  showMoreFlag = [];
  goDetail = false;
  scrollPos = null;
  constructor() {
    makeAutoObservable(this);
  }

  init() {
    this.scrollPos = null;
    this.selectedIndustries = [];
    this.formulaSymbol = '';
    this.properPriceByIndustry = {};
    this.showMoreFlag = [];
    this.goDetail = false;
  }

  *searchProperPrice() {
    this.properPriceByIndustry = yield ProperHttp.searchProperPriceByIndustry({
      industries: this.selectedIndustries,
      formulaSymbol: this.formulaSymbol,
    });
  }

  sumbit() {
    this.showMoreFlag = [];
    this.searchProperPrice();
  }

  clear() {
    this.selectedIndustries = [];
    this.searchProperPrice();
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

  changeFilter(item) {
    let currentSelected;
    if (this.selectedIndustries.includes(item)) {
      currentSelected = this.selectedIndustries.filter((p) => p !== item);
    } else {
      currentSelected = [...this.selectedIndustries, item];
    }

    this.selectedIndustries = currentSelected;

    this.searchProperPrice();
  }

  changeShowMore(key) {
    this.showMoreFlag = [...this.showMoreFlag, key];
  }

  setGoDetail(value) {
    this.goDetail = value;
  }
}

export default new UIStore();
