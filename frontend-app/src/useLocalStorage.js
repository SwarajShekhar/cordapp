import { useState } from "react";

export const useLocalStorage = (keyName, defaultValue) => {
    const [storedValue, setStoredValue] = useState(() => {
        try {
            // TODO Decide between localstorage vs sessionStorage to store local persistent user data
            // const value = window.localStorage.getItem(keyName);
            const value = window.sessionStorage.getItem(keyName);
            if (value) {
                return JSON.parse(value);
            } else {
                window.sessionStorage.setItem(keyName, JSON.stringify(defaultValue));
                return defaultValue;
            }
        } catch (err) {
            return defaultValue;
        }
    });
    const setValue = (newValue) => {
        try {
            window.sessionStorage.setItem(keyName, JSON.stringify(newValue));
        } catch (err) { }
        setStoredValue(newValue);
    };
    return [storedValue, setValue];
};