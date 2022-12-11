/**
 * 
 * Utility functions...
 * 
 */

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