import React from 'react';
import { Route, Switch } from "react-router-dom";
import AppliedRoute from "./components/AppliedRoute";
import asyncComponent from "./components/AsyncComponent";

const AsyncHome = asyncComponent(() => import('./containers/Home.js'));
const AsyncEmployee = asyncComponent(() => import('./containers/Employee.js'));
const AsyncNotFound = asyncComponent(() => import('./containers/NotFound.js'));


export default ({ childProps }) => (
  <Switch>
    <AppliedRoute path="/" exact component={AsyncHome} props={childProps} />
    <AppliedRoute path="/employee" component={AsyncEmployee} props={childProps} />
    <Route component={AsyncNotFound} />
  </Switch>
);
