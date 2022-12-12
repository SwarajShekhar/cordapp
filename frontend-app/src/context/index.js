import { createContext } from 'react';

// export const APIEndPointContext = createContext('/api');
export const APIEndPointContext = createContext({
    baseUri: 'http://localhost:8080/api',
    changeBaseUri: null,
});
