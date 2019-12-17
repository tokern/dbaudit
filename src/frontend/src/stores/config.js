import fetchJson from '../utilities/fetch-json.js';

export const refreshAppContext = async () => {
  const {
    config,
    adminRegistrationOpen,
    version
  } = await fetchJson('GET', '/api/bootstrap');
  if (!config) {
    return;
  }
  // Assign config.baseUrl to global
  // It doesn't change and is needed for fetch requests
  // This allows us to simplify the fetch() call
  window.BASE_URL = config.baseUrl;

  return {
    config,
    adminRegistrationOpen,
    version
  };
};

export default { refreshAppContext };
