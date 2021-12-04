import { makeAutoObservable } from 'mobx';
import ProperHttp from '../common/https/ProperHttp';

class GlobalStore {
  industryNames = [];
  theme = [];
  formulas = [];
  tickers = [];
  scroll = null;
  scrollContainer = null;

  constructor() {
    makeAutoObservable(this);
  }

  init(scrollContainer) {
    this.searchFormulas();
    this.searchIndustryNames();
    this.searchThemeNames();
    this.searchTickersByCode();
    this.setScrollStatus(0, 0);
    this.scrollContainer = scrollContainer;
  }

  setScrollStatus(x, y) {
    this.scroll = {
      x: x,
      y: y,
    };
  }

  restoreScroll(scroll) {
    this.setScrollStatus(scroll.x, scroll.y);
    if (this.scrollContainer) {
      this.scrollContainer.scrollTo(scroll.x, scroll.y);
    }
  }

  *searchIndustryNames() {
    this.industryNames = yield ProperHttp.searchIndustryNames();
  }

  *searchThemeNames() {
    this.theme = yield ProperHttp.searchThemeNames();
  }

  *searchFormulas() {
    this.formulas = yield ProperHttp.searchFormulas();
  }

  *searchTickersByCode() {
    this.tickers = yield ProperHttp.searchTickersByCode();
  }
}

export default new GlobalStore();
