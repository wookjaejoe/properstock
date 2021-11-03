import axios from 'axios';

const ProperHttp = {
  servideURL: 'http://home.jowookjae.in:6001',
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
  searchTickerList({
    market = [],
    industries = [],
    themes = [],
    formulaSymbol = '',
    limit = 0,
    searchText = '',
  }) {
    let parameter = '';
    if (market.length > 0) {
      parameter = parameter.concat(`market=${market.join(',')}&`);
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
  searchTop100(formulaSymbol) {
    return axios
      .get(`${this.servideURL}/proper/prices?limit=100&formulaSymbol=${formulaSymbol}`)
      .then((res) => res.data);
  },
  searchTickerByIndustry({ industries = [], formulaSymbol = '' }) {
    return this.searchTickerList({ industries: industries, formulaSymbol: formulaSymbol }).then(
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
  searchTickerByTheme({ themes = [], formulaSymbol = '' }) {
    return this.searchTickerList({ themes: themes, formulaSymbol: formulaSymbol }).then((data) => {
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
