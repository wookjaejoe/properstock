import axios from 'axios';

const ProperHttp = {
  servideURL: 'http://home.jowookjae.in:6001',
  searchFormulas() {
    return axios.get(`${this.servideURL}/proper/formulas`).then((res) => res.data);
  },
};

export default ProperHttp;
