import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'unistore/react';
import { Redirect } from 'react-router-dom';

function Authenticated({ children, adminRegistrationOpen, currentUser}) {
  if (adminRegistrationOpen) {
    return <Redirect to={{pathname: '/register'}}/>;
  }
  if (currentUser === undefined) {
    return <Redirect to={{ pathname: '/signin' }} />;
  }

  return children;
}

Authenticated.propTypes = {
  admin: PropTypes.bool
};

export default connect(
  ['adminRegistrationOpen', 'currentUser'],
)(Authenticated);
