/**
 * 
 * Utility functions...
 * 
 */

import dayjs from 'dayjs';
import localizedFormat from 'dayjs/plugin/localizedFormat';

dayjs.extend(localizedFormat);

export const formatDateInfoShort = (data) => {
    return data ? dayjs(data).format('MMM DD, YYYY') : null;
}

export const formatDateInfo = (data) => {
    // return dayjs(data).format('LLL');
    return data ? dayjs(data).format('MMMM DD, YYYY hh:mm a ZZ') : null;
}

// parse user/node name details from string like "O=GPO1,L=London,C=GB,OU=gpo"
export const parseUserInfo = (user) => {
    return user.split(',').reduce((a, b) => {
        const [k, v] = b.trim().split('=');
        a[k.toLowerCase()] = v;
        return a;
    }, {});
}

export class UserInfo {
    constructor(userInfoStr) {
        userInfoStr.split(',').reduce((a, b) => {
            const [k, v] = b.trim().split('=');
            a[k.toLowerCase()] = v;
            return a;
        }, this);
    }

    toString() {
        // console.log('useinfo', this.ou, this.o);
        return this.o;
    }
}