import GoogleIcon from 'mdi-react/GoogleIcon';
import React, { useEffect, useState } from 'react';
import { Link, Redirect } from 'react-router-dom';
import { connect } from 'unistore/react';
import Button from './common/Button';
import Input from './common/Input';
import message from './common/message';
import Spacer from './common/Spacer';
import {setUser} from "./stores/user";
import apiCall, {setToken} from "./utilities/apiCall";

function SignIn({ config, currentUser, setUser }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [redirect, setRedirect] = useState(false);


  useEffect(() => {
    document.title = 'Tokern Bastion - Sign In';
  }, []);

  const signIn = async e => {
    e.preventDefault();

    const response = await apiCall('post', '/api/users/login', {
      email,
      password
    });

    console.log(response);

    if (response.error) {
      return message.error('Username or password incorrect');
    }

    setToken(response.token);
    setUser(response);
    setRedirect(true);
  };

  if (redirect || currentUser) {
    return <Redirect push to="/" />;
  }

  if (!config) {
    return;
  }

  const localForm = (
    <form onSubmit={signIn}>
      <Input
        name="email"
        type="email"
        placeholder="Email address"
        onChange={e => setEmail(e.target.value)}
        required
      />
      <Spacer />
      <Input
        name="password"
        type="password"
        placeholder="Password"
        onChange={e => setPassword(e.target.value)}
        required
      />
      <Spacer size={2} />
      <Button
        style={{ width: '100%' }}
        onClick={signIn}
        htmlType="submit"
        type="primary"
      >
        Sign in
      </Button>
      <Spacer />
      <Link
        style={{
          display: 'inline-block',
          width: '100%',
          textAlign: 'center'
        }}
        to="/signup"
      >
        Sign Up
      </Link>

      {config.smtpConfigured ? (
        <Link to="/forgot-password">Forgot Password</Link>
      ) : null}
    </form>
  );

  // TODO FIXME XXX Button inside anchor is bad
  const googleForm = (
    <div>
      <a href={config.baseUrl + '/auth/google'}>
        <Button type="primary">
          <GoogleIcon />
          Sign in with Google
        </Button>
      </a>
    </div>
  );

  const samlForm = (
    <div>
      <a href={config.baseUrl + '/auth/saml'}>Sign in with ADFS</a>
    </div>
  );

  return (
    <div style={{ width: '300px', textAlign: 'center', margin: '100px auto' }}>
      <h1>Tokern Bastion</h1>
      {config.localAuthConfigured && localForm}
      {config.googleAuthConfigured && googleForm}
      {config.samlConfigured && samlForm}
    </div>
  );
}

export default connect(
  ['config', 'currentUser'],
  store => ({
    setUser
  })
)(SignIn);
