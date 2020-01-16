import localforage from 'localforage';
import sortBy from 'lodash/sortBy';
import message from '../common/message';
import apiCall from "../utilities/apiCall";

function sortConnections(connections) {
  return sortBy(connections, [connection => connection.name.toLowerCase()]);
}

export const initialState = {
  selectedConnectionId: '',
  connections: [],
  connectionsLastUpdated: null,
  connectionsLoading: false
};

export async function initSelectedConnection(state) {
  const selectedConnectionId = await localforage.getItem(
    'selectedConnectionId'
  );
  if (typeof selectedConnectionId === 'string') {
    return {
      selectedConnectionId
    };
  }
}

export const selectConnectionId = (state, selectedConnectionId) => {
  localforage
    .setItem('selectedConnectionId', selectedConnectionId)
    .catch(error => message.error(error));
  return { selectedConnectionId };
};

export const deleteConnection = async (state, connectionId) => {
  const { connections } = state;
  const json = await apiCall('DELETE', '/api/databases/' + connectionId);
  if (json.error) {
    return message.error('Delete failed');
  }
  const filtered = connections.filter(c => c._id !== connectionId);
  return { connections: sortConnections(filtered) };
};

// Updates store (is not resonponsible for API call)
export const addUpdateConnection = async (state, connection) => {
  const { connections } = state;
  const found = connections.find(c => c._id === connection._id);
  if (found) {
    const mappedConnections = connections.map(c => {
      if (c._id === connection._id) {
        return connection;
      }
      return c;
    });
    return { connections: sortConnections(mappedConnections) };
  }
  return { connections: sortConnections([connection].concat(connections)) };
};

export const loadConnections = store => async (state) => {
  const { connections, connectionsLoading } = state;
  if (connectionsLoading) {
    return;
  }

  store.setState({ connectionsLoading: true });
  const { error, databases } = await apiCall('GET', '/api/databases/');
  if (error) {
    message.error(error);
  }
  const update = {
    connectionsLoading: false,
    connections: sortConnections(databases)
  };

  if (connections && connections.length === 1) {
    update.selectedConnectionId = connections[0].id;
  }

  store.setState(update);
};

export default {
  initialState,
  selectConnectionId,
  deleteConnection,
  addUpdateConnection,
  loadConnections
};
