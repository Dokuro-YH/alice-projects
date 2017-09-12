import React from 'react';

const buttonStyle = {
  border: 'none',
  background: 'none',
  padding: 0,
  margin: 0,
  outline: 0,
};

export default props => (
  <li>
    <a role="button">
      <form action={props.action} method={props.method}>
        <button style={buttonStyle} type="submit">{props.children}</button>
      </form>
    </a>
  </li>
);
