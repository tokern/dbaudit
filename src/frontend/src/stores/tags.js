import message from '../common/message';
import apiCall from "../utilities/apiCall";

export const initialState = {
  availableTags: []
};

export const loadTags = async state => {
  const { error, tags } = await apiCall('GET', '/api/tags');
  if (error) {
    message.error(error);
  }
  return { availableTags: tags };
};

export default { initialState, loadTags };
