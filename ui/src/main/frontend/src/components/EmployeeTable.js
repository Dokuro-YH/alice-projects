import React from 'react';
import NavLinks from '../components/NavLinks';

const EmployeeRow = ({ schema, employee, onUpdate, onDelete }) => (
  <tr>
    <td>{employee.name}</td>
    <td>{employee.age}</td>
    <td>{employee.description}</td>
    <td>
      <a role="button" onClick={onUpdate}>Update</a>
      <a role="button" style={{ marginLeft: 4 }} onClick={onDelete}>Delete</a>
    </td>
  </tr>
);

const EmployeeList = ({ employees, onUpdate, onDelete }) => (
  <table className="table">
    <thead>
      <tr>
        <th>Name</th>
        <th>Age</th>
        <th>Description</th>
        <th>Action</th>
      </tr>
    </thead>
    <tbody>
      {
        employees && employees.map(employee =>
          <EmployeeRow
            key={employee._links.self.href}
            employee={employee}
            onUpdate={onUpdate(employee)}
            onDelete={onDelete(employee)}
          />
        )
      }
    </tbody>
  </table>
);

export default ({ employees, links, pageIndex, totalPages, onUpdate, onDelete, onNavgate }) => (
  <div>
    <EmployeeList
      employees={employees}
      onUpdate={onUpdate}
      onDelete={onDelete}
    />
    <NavLinks
      links={links}
      pageIndex={pageIndex}
      totalPages={totalPages}
      onNavgate={onNavgate}
    />
  </div>
)