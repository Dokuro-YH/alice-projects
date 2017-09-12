import React, { Component } from 'react';
import axios from 'axios';

class Home extends Component {
  state = {
    data: {}
  }
  componentDidMount() {
    axios.get('/api/hello/sayHello')
      .then(response => this.setState({ data: response.data }))
  }
  render() {
    return (
      <div>
        <p>{this.state.data.id}</p>
        <p>{this.state.data.content}</p>
      </div>
    );
  }
}

export default Home;