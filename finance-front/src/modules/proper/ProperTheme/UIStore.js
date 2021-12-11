import { makeAutoObservable } from 'mobx';
import ProperHttp from '../../../common/https/ProperHttp';

class UIStore {
  selectedThemes = [];
  formulaSymbol = '';
  _properPriceByTheme = {};
  goDetail = false;
  scrollPos = null;
  constructor() {
    makeAutoObservable(this);
  }

  init() {
    this.scrollPos = null;
    this.selectedThemes = [];
    this.formulaSymbol = '';
    this._properPriceByTheme = {};
    this.goDetail = false;
  }

  *searchProperPrice() {
    this._properPriceByTheme = yield ProperHttp.searchProperPriceByTheme({
      themes: this.selectedThemes,
      formulaSymbol: this.formulaSymbol,
    });
  }

  get properPriceByTheme() {
    if (this.selectedThemes.length === 0) {
      return this._properPriceByTheme;
    } else {
      const filtered = {};

      this.selectedThemes.forEach((key) => {
        if (this._properPriceByTheme[key]) {
          filtered[key] = this._properPriceByTheme[key];
        }
      });
      return filtered;
    }
  }

  clear() {
    this.selectedThemes = [];
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

  changeFilter(item) {
    let currentSelected;
    if (this.selectedThemes.includes(item)) {
      currentSelected = this.selectedThemes.filter((p) => p !== item);
    } else {
      currentSelected = [...this.selectedThemes, item];
    }

    this.selectedThemes = currentSelected;

    this.searchProperPrice();
  }

  setGoDetail(value) {
    this.goDetail = value;
  }
}

export default new UIStore();
