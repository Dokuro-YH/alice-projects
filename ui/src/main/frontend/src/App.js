import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Nav, Navbar } from "react-bootstrap";
import axios from 'axios';
import Routes from './Routes';
import RouteNavItem from './components/RouteNavItem';
import FormNavItem from './components/FormNavItem';
import './App.css';

class App extends Component {
  state = {
    info: {}
  }

  componentDidMount() {
    this.loadAppInfo();
  }

  loadAppInfo = async () => {
    const { data } = await axios.get('/info');
    this.setState({ info: data });
    document.title = data.title || document.title;
  }

  handleLogout = (e) => {
    e.preventDefault();
    axios.post('/logout');
  }

  render() {
    const { info } = this.state;
    return (
      <div className="App">
        <Navbar fluid collapseOnSelect>
          <Navbar.Header>
            <Navbar.Brand>
              <Link to="/">Brand</Link>
            </Navbar.Brand>
            <Navbar.Toggle />
          </Navbar.Header>
          <Navbar.Collapse>
            <Nav>
              <RouteNavItem href="/employee">Employee</RouteNavItem>
            </Nav>
            <Nav pullRight>
              <FormNavItem action="/logout" method="post">Logout</FormNavItem>
            </Nav>
          </Navbar.Collapse>
        </Navbar>
        <div className="content container">
          <Routes childProps={this.props} />
        </div>
        <footer>
          <p>Powered by Yanhai ©2017.</p>
          <p>Current version: {info.build && info.build.version}</p>
        </footer>
      </div>
    );
  }
}

export default withRouter(App);
