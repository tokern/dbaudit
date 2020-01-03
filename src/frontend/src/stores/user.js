export const initialState = {
    token: undefined
};

export const setUserToken = (state, loginResponse) => {
    return {token: loginResponse.token, currentUser: loginResponse.user}
};

export const signOut = (state) => {
    return {token: undefined, currentUser: undefined}
};

export function getUserToken(store) {
    console.log(store);
    const { token } = store.getState();
    return { token: token}
}

export default {
    initialState,
    setUserToken,
    getUserToken,
    signOut
};
