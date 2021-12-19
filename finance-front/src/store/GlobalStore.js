import { makeAutoObservable, toJS } from 'mobx';
import { w3cwebsocket } from 'websocket';
import ProperHttp from '../common/https/ProperHttp';

class GlobalStore {
  industryNames = [];
  theme = [];
  formulas = [];
  tickers = {};
  scroll = null;
  scrollContainer = null;
  updown = {};
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
    this.initSocket();
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

  getFormulas(symbol) {
    let find = null;
    this.formulas.forEach((value) => {
      if (value.symbol === symbol) {
        find = value;
      }
    });
    return find;
  }

  initSocket() {
    try {
      const client = new w3cwebsocket('wss://ppst-ws.jowookjae.in');
      client.onopen = () => {
        console.log('WebSocket Client Connected');
      };
      client.onmessage = (message) => {
        const priceArr = JSON.parse(message.data);
        this.change(priceArr);
      };
    } catch (e) {
      console.log(e);
    }
  }

  clearUpDown() {
    this.updown = {};
  }

  getStatus(code) {
    return this.updown[code];
  }

  change(priceArr) {
    const tempUpdown = {};
    priceArr.forEach((value) => {
      const current = this.tickers[value.code];
      if (current) {
        if (current.price > value.price) {
          tempUpdown[value.code] = 'price-up';
        } else if (current.price < value.price) {
          tempUpdown[value.code] = 'price-down';
        } else {
          tempUpdown[value.code] = '';
        }
        current.price = value.price;
      }
    });
    this.updown = tempUpdown;
    setTimeout(() => {
      this.clearUpDown();
    }, 1000);
  }

  *searchTickersByCode() {
    this.tickers = yield ProperHttp.searchTickersByCode();
  }
}

export default new GlobalStore();
