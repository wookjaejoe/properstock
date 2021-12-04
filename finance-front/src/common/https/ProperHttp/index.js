import axios from 'axios';

const ProperHttp = {
  servideURL: 'https://home.jowookjae.in:9443',
  searchTickersByCode() {
    return axios
      .get(`${this.servideURL}/tickers`)
      .then((res) => res.data)
      .then((tickers) => {
        const tickerByCode = {};
        tickers.forEach((ticker) => {
          tickerByCode[ticker.code] = ticker;
        });
        return tickerByCode;
      });
  },
  searchFormulas() {
    return axios.get(`${this.servideURL}/proper/formulas`).then((res) => res.data);
  },
  searchThemes() {
    return axios.get(`${this.servideURL}/themes`).then((res) => res.data);
  },
  searchThemeNames() {
    return this.searchThemes().then((res) => res.map((theme) => theme.name));
  },
  searchIndustries() {
    return axios.get(`${this.servideURL}/industries`).then((res) => res.data);
  },
  searchIndustryNames() {
    return this.searchIndustries().then((res) => res.map((industry) => industry.name));
  },
  searchDetails(code) {
    return axios.get(`${this.servideURL}/ticker-details/${code}`).then((res) => res.data);
  },
  searchProperPrice({
    market = [],
    industries = [],
    themes = [],
    formulaSymbol = '',
    limit = 0,
    searchText = '',
  }) {
    let parameter = '';
    if (market.length > 0) {
      parameter = parameter.concat(`markets=${market.join(',')}&`);
    }
    if (industries.length > 0) {
      parameter = parameter.concat(`industries=${industries.join(',')}&`);
    }

    if (themes.length > 0) {
      parameter = parameter.concat(`themes=${themes.join(',')}&`);
    }

    if (formulaSymbol.length > 0) {
      parameter = parameter.concat(`formulaSymbol=${formulaSymbol}&`);
    }

    if (limit > 0) {
      parameter = parameter.concat(`limit=${limit}&`);
    }

    if (searchText.length > 0) {
      parameter = parameter.concat(`searchText=${searchText}&`);
    }

    return axios.get(`${this.servideURL}/proper/prices?${parameter}`).then((res) => res.data);
  },
  searchProperPriceByName(param) {
    return this.searchProperPrice(param).then((data) => {
      const group = {};

      data.forEach((ticker) => {
        const item = group[ticker.tickerName] || [];
        group[ticker.tickerName] = [...item, ticker];
      });
      return group;
    });
  },
  searchProperPriceTop100(formulaSymbol) {
    return axios
      .get(`${this.servideURL}/proper/prices?limit=100&formulaSymbol=${formulaSymbol}`)
      .then((res) => res.data);
  },
  searchProperPriceByIndustry({ industries = [], formulaSymbol = '' }) {
    return this.searchProperPrice({ industries: industries, formulaSymbol: formulaSymbol }).then(
      (data) => {
        const res = {};
        data.forEach((ticker) => {
          const industries = res[ticker.tickerIndustry] || [];
          res[ticker.tickerIndustry] = [...industries, ticker];
        });
        return res;
      }
    );
  },
  searchProperPriceByTheme({ themes = [], formulaSymbol = '' }) {
    return this.searchProperPrice({ themes: themes, formulaSymbol: formulaSymbol }).then((data) => {
      const res = {};
      data.forEach((ticker) => {
        ticker.tickerThemes.forEach((theme) => {
          const themes = res[theme] || [];
          res[theme] = [...themes, ticker];
        });
      });
      return res;
    });
  },
};

export default ProperHttp;
