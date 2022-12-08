export const parseUserInfo = (user) => {
    return user.split(',').reduce((a, b) => {
        const [k, v] = b.trim().split('=');
        a[k.toLowerCase()] = v;
        return a;
    }, {});
}
