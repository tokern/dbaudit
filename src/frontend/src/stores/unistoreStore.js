import createStore from 'unistore';
import connections from './connections';
import queries from './queries';
import schema from './schema';
import tags from './tags';
import user from './user';

const unistoreStore = createStore({
  ...queries.initialState,
  ...schema.initialState,
  ...connections.initialState,
  ...tags.initialState,
  ...user.initialState
});

export default unistoreStore;
