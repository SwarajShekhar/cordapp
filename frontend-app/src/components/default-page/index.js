import { EditOutlined, EllipsisOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Card, Divider, Menu, Space, Typography } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { Outlet, useLocation, Navigate, useMatch, matchPath, Link } from 'react-router-dom';
import { APIEndPointContext, AuthContext } from '../../context';
import { ROLES } from '../../roles';
import { parseUserInfo } from '../../utils';

export const DefaultPage = () => {
    const [me, setMe] = useState('');
    const { baseUri } = useContext(APIEndPointContext);

    useEffect(() => {
        fetch(`${baseUri}/me`)
            .then(res => {
                // console.log(res);
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to load response');
                return res.json();
            })
            .then(data => {
                // console.log('received response for me:', data);
                setMe(parseUserInfo(data.me));
            })
            .catch(error => {
                console.log(error);
            });
    }, [baseUri]);

    return (<>
        <Card
            style={{ width: 300 }}
            actions={[
                <SettingOutlined key="setting" />,
                <EditOutlined key="edit" />,
                <EllipsisOutlined key="ellipsis" />,
            ]}>
            <Card.Meta
                avatar={<Avatar src='https://picsum.photos/50' />}
                title={me.o}
                description={<span>O={me.o}, OU={me.ou}, L={me.l}, C={me.c}<br />
                    API endpoint: {baseUri}</span>}
            />
        </Card>
    </>);
}

export const ContentPage = ({ title, items, children }) => {

    const location = useLocation();
    const curNavkey = location.pathname.split('/')[2];
    /* const items = [
        { key: 'list', label: <Link to='/members/list'>List</Link> },
        { key: 'proposal', label: <Link to='/members/proposal'>Proposal</Link> },
        // { key: 'mem-item-3', label: <Link to='/members/proposalpending'>Pending</Link> },
        { key: 'proposalcreate', label: <Link to='/members/proposalcreate'><PlusCircleOutlined /> Add Member</Link> },
    ]; */

    let auth = useContext(AuthContext);
    if (!auth.user) {
        // Redirect them to the /login page, but save the current location
        return <Navigate to="/" state={{ from: location }} replace />;
    }

    const navItems = items ? items.filter(item => {
        if (item.permissions) {
            return item.permissions.indexOf(auth.user) > -1;
        }
        return true;
    }) : [];

    //console.log('matchPath', !!matchPath('/members/*', location.pathname), (auth.user), (auth.user === ROLES.MODELN));
    if (!!matchPath('/members/*', location.pathname) && auth.user === ROLES.MODELN) {
        // FIX hardcoded change of label for /members/proposal for modeln view
        navItems[1].label = <Link to='/members/proposal'>Proposal</Link>
    }

    return (<>
        <Space split={<Divider type="vertical" />}>
            <Typography.Title>{title}</Typography.Title>
            <Menu mode='horizontal' items={navItems} disabledOverflow selectedKeys={curNavkey} />
        </Space>
        <section>
            {children ? children : <Outlet />}
        </section>
    </>);
}
