import React, { useEffect, useState } from 'react';
import { Redirect } from 'react-router-dom';
import { connect } from 'unistore/react';
import Button from './common/Button';
import Input from './common/Input';
import message from './common/message';
import Spacer from './common/Spacer';
import fetchJson from './utilities/fetch-json.js';

function Register({ adminRegistrationOpen }) {
  const [displayName, setDisplayName] = useState('');
  const [userEmail, setUserEmail] = useState('');
  const [password, setPassword] = useState('');
  const [orgName, setOrgName] = useState('');
  const [orgSlug, setOrgSlug] = useState('');
  const [redirect, setRedirect] = useState(false);

  useEffect(() => {
    document.title = 'Tokern - Register';
  }, []);

  const register = async e => {
    e.preventDefault();
    const json = await fetchJson('POST', '/api/bootstrap/register', {
      userEmail,
      password,
      displayName,
      orgName,
      orgSlug
    });
    if (json.error) {
      return message.error(json.error);
    }
    setRedirect(true);
  };

  if (redirect) {
    return <Redirect to="/" />;
  }

  return (
    <div style={{ width: '300px', textAlign: 'center', margin: '100px auto' }}>
      <form onSubmit={register}>
        <h1>Tokern Bastion</h1>
        {adminRegistrationOpen && (
          <div>
            <h2>Admin registration open</h2>
            <p>
              Welcome to Tokern Bastion! There are no organizations and admins defined.
              Please register an organization and administrator using the form below.
            </p>
          </div>
        )}
        <Input
            name="Organization"
            type="text"
            placeholder="Organization"
            onChange={e => setOrgName(e.target.value)}
            required
        />
        <Spacer />
        <Input
            name="Organization Slug"
            type="url"
            placeholder="URL"
            onChange={e => setOrgSlug(e.target.value)}
            required
        />
        <Spacer />
        <Input
            name="Display Name"
            type="text"
            placeholder="Display Name"
            onChange={e => setDisplayName(e.target.value)}
            required
        />
        <Spacer />
        <Input
          name="email"
          type="email"
          placeholder="Email address"
          onChange={e => setUserEmail(e.target.value)}
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
        <Button style={{ width: '100%' }} htmlType="submit" type="primary">
          Sign up
        </Button>
      </form>
    </div>
  );
}

export default connect(['adminRegistrationOpen'])(Register);
