import apiCall from "../utilities/apiCall";

export const refreshAppContext = async () => {
  const {
    config,
    adminRegistrationOpen,
    version
  } = await apiCall('GET', '/api/bootstrap');
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
