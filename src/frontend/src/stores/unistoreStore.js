import createStore from 'unistore';
import connections from './connections';
import queries from './queries';
import schema from './schema';
import tags from './tags';
import user from './user';
import localStorageAdapter from "unissist/dist/localStorageAdapter";
import persistStore from "unissist";

const unistoreStore = createStore({
  ...queries.initialState,
  ...schema.initialState,
  ...connections.initialState,
  ...tags.initialState,
  ...user.initialState
});

const adapter = localStorageAdapter();
persistStore(unistoreStore, adapter);

export default unistoreStore;
