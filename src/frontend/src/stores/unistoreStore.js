import createStore from 'unistore';
import connections from './connections';
import queries from './queries';
import schema from './schema';
import tags from './tags';
import user from './user';
import localStorageAdapter from "unissist/dist/localStorageAdapter";
import persistStore from "unissist";
import devtools    from 'unistore/devtools'

const initialState = {
  ...queries.initialState,
  ...schema.initialState,
  ...connections.initialState,
  ...tags.initialState,
  ...user.initialState
};

let unistoreStore = process.env.NODE_ENV === 'production' ?  createStore(initialState) : devtools(createStore(initialState));
const adapter = localStorageAdapter();
persistStore(unistoreStore, adapter);
export default unistoreStore;
