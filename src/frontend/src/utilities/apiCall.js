import axios from 'axios';
import createAuthRefreshInterceptor from 'axios-auth-refresh';

const axiosInstance = axios.create({
  baseURL: window.BASE_URL || ''
});

let jwtToken = null;

// Function that will be called to refresh authorization
const refreshAuthLogic = failedRequest => axiosInstance.get('/api/users/refreshJWT', {
  skipAuthRefresh: true,
  withCredentials: true
}).then(tokenRefreshResponse => {
  jwtToken = tokenRefreshResponse.data.token;
  failedRequest.response.config.headers['Authorization'] = 'Bearer ' + jwtToken;
  return Promise.resolve();
});

function getAuthToken() {
  if (jwtToken) {
    return `${jwtToken}`;
  }
  return null;
}

axiosInstance.interceptors.request.use(request => {
  const token = getAuthToken();
  if (token) {
    request.headers.Authorization = `Bearer ${token}`;
  }
  return request;
});

// Instantiate the interceptor (you can chain it as it returns the axios instance)
createAuthRefreshInterceptor(axiosInstance, refreshAuthLogic, {skipWhileRefreshing: false});

function apiCall(method, url, data) {
  return axiosInstance({
    method: method,
    url: url,
    data: data
  }).then(function(response) {
    return response.data
  }).catch(function (error) {
    if (error.response) {
      return {
        error: error.response.statusText
      }
    } else if (error.request) {
      return {
        error: error.request
      }
    } else {
      return {
        error: error.message
      }
    }
  })
}

export function setToken (token) {
  jwtToken = token;
}

export default apiCall;
