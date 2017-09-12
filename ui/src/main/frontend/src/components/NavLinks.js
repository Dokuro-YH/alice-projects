import React from 'react';
import { Pagination } from 'react-bootstrap';

export default ({ links, pageIndex, totalPages, onNavgate }) => (
  <Pagination
    first={'first' in links}
    prev={'prev' in links}
    next={'next' in links}
    last={'last' in links}
    activePage={pageIndex + 1}
    items={totalPages}
    onSelect={(page, e) => onNavgate(page - 1, e)}
  />
);
