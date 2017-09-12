import React, { Component } from 'react';
import { Button } from 'react-bootstrap';
import axios from 'axios';
import EmployeeTable from '../components/EmployeeTable';
import EmployeeModal from '../components/EmployeeModal';

class Employee extends Component {
  static API = '/api/employee';

  state = {
    showModal: false,
    isModify: false,
    employee: {},
    employees: [],
    links: {},
    pageIndex: 0,
    pageSize: 10,
    totalPages: 0,
  }

  async componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    const { pageIndex, pageSize } = this.state;
    return axios.get(`${Employee.API}?page=${pageIndex}&size=${pageSize}`)
      .then(this.handleLoadSuccess);
  }

  handleLoadSuccess = response => {
    this.setState({
      employees: response.data._embedded.employees,
      links: response.data._links,
      pageIndex: response.data.page.number,
      pageSize: response.data.page.size,
      totalPages: response.data.page.totalPages,
    });
    return response;
  }

  handleCloseModal = () => {
    this.setState({ isModify: false, employee: {}, showModal: false });
  }

  handleOpenModal = async () => {
    this.setState({ isModify: false, showModal: true });
  }

  handleSubmit = async data => {
    if (data._links.self) {
      await axios.put(data._links.self.href, data);
    } else {
      await axios.post(`${Employee.API}`, data);
    }
    this.handleCloseModal();
    this.loadData();
  }

  onNavgate = (pageIndex, e) => {
    e.preventDefault();
    this.setState({ pageIndex }, this.loadData);
  }

  onUpdate = employee => e => {
    e.preventDefault();
    this.setState({ employee, isModify: true, showModal: true });
  }

  onDelete = employee => e => {
    e.preventDefault();
    axios.delete(employee._links.self.href)
      .then(this.loadData)
      .then(response => {
        if (response.data._embedded.employees.length === 0 && this.state.pageIndex > 0) {
          this.setState({ pageIndex: this.state.pageIndex - 1 }, this.loadData);
        }
      });
  }

  render() {
    return (
      <div>
        <Button bsStyle="primary" onClick={this.handleOpenModal}>Create</Button>
        <EmployeeModal
          isModify={this.state.isModify}
          show={this.state.showModal}
          data={this.state.employee}
          onClose={this.handleCloseModal}
          onSubmit={this.handleSubmit}
        />
        <EmployeeTable
          employees={this.state.employees}
          links={this.state.links}
          pageIndex={this.state.pageIndex}
          totalPages={this.state.totalPages}
          onUpdate={this.onUpdate}
          onDelete={this.onDelete}
          onNavgate={this.onNavgate}
        />
      </div>
    );
  }
}

export default Employee;