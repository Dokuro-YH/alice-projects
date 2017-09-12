import React, { Component } from 'react';
import {
  Modal,
  Form,
  FormGroup,
  Col,
  FormControl,
  ControlLabel,
  Button
} from 'react-bootstrap';

class EmployeeModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: props.data || {}
    }
  }

  componentWillReceiveProps(nextProps) {
    if ('data' in nextProps) {
      this.setState({ data: nextProps.data });
    }
  }

  handleChange = (e) => {
    const target = e.target;
    const isNumber = target.type === 'number';
    const name = target.id;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    this.setState({
      data: {
        ...this.state.data,
        [name]: isNumber ? Number(value) : value
      }
    });
  }

  triggerSubmit = e => {
    e.preventDefault();
    const onSubmit = this.props.onSubmit;
    if (onSubmit) {
      onSubmit(this.state.data);
    }
  }

  render() {
    const { show, isModify, onClose } = this.props;
    const { data } = this.state;
    return (
      <Modal show={show} onHide={onClose}>
        <Modal.Header closeButton>
          <Modal.Title>{isModify ? 'Update' : 'Create'} employee</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form horizontal onSubmit={this.triggerSubmit}>
            <FormGroup controlId="name">
              <Col componentClass={ControlLabel} sm={2}>Name</Col>
              <Col sm={10}>
                <FormControl type="text" onChange={this.handleChange} value={data.name || ''} />
              </Col>
            </FormGroup>
            <FormGroup controlId="age">
              <Col componentClass={ControlLabel} sm={2}>Age</Col>
              <Col sm={10}>
                <FormControl type="number" onChange={this.handleChange} value={data.age || ''} />
              </Col>
            </FormGroup>
            <FormGroup controlId="description">
              <Col componentClass={ControlLabel} sm={2}>Description</Col>
              <Col sm={10}>
                <FormControl type="text" onChange={this.handleChange} value={data.description || ''} />
              </Col>
            </FormGroup>
            <FormGroup>
              <Col smOffset={2} sm={10}>
                <Button bsStyle="primary" type="submit">Save</Button></Col>
            </FormGroup>
          </Form>
        </Modal.Body>
      </Modal>
    );
  }
}

export default EmployeeModal;
