import apiCall from "../utilities/apiCall";

export const initialState = {
    currentUser: undefined
};

export const setUser = (state, loginResponse) => {
    return {currentUser: loginResponse.user}
};

export const signOut = async state => {
    await apiCall("GET", "/api/users/logout");
    return {currentUser: undefined}
};

export function getUserToken(store) {
    console.log(store);
    const { token } = store.getState();
    return { token: token}
}

export default {
    initialState,
    setUser,
    getUserToken,
    signOut
};
