import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'unistore/react';
import { refreshAppContext } from './stores/config';
import { Redirect } from 'react-router-dom';
import { getUserToken } from "./stores/user";

function Authenticated({ children, adminRegistrationOpen, token, refreshAppContext }) {
  if (adminRegistrationOpen) {
    return <Redirect to={{pathname: '/register'}}/>;
  }
  if (token === undefined) {
    return <Redirect to={{ pathname: '/signin' }} />;
  }

  return children;
}

Authenticated.propTypes = {
  admin: PropTypes.bool
};

export default connect(
  ['adminRegistrationOpen', 'token'],
  store => ({
    refreshAppContext,
    getUserToken
  })
)(Authenticated);
