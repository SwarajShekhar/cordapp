import { Divider, Menu, Space, Typography } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { Outlet, useLocation } from 'react-router';
import { APIEndPointContext } from '../../context';
import { parseUserInfo } from '../../utils';
const { Title } = Typography;

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

    return (<div style={{ paddingTop: 50 }}>
        <Title>{me.o}</Title>
        <p>OU={me.ou}, L={me.l}, C={me.c}</p>
        <p>{baseUri}</p>
    </div >)
}

export const ContentPage = ({ title, items }) => {
    const location = useLocation();
    const curNavkey = location.pathname.split('/')[2];
    /* const items = [
        { key: 'list', label: <Link to='/members/list'>List</Link> },
        { key: 'proposal', label: <Link to='/members/proposal'>Proposal</Link> },
        // { key: 'mem-item-3', label: <Link to='/members/proposalpending'>Pending</Link> },
        { key: 'proposalcreate', label: <Link to='/members/proposalcreate'><PlusCircleOutlined /> Add Member</Link> },
    ]; */

    return (<>
        <Space split={<Divider type="vertical" />}>
            <Typography.Title>{title}</Typography.Title>
            <Menu mode='horizontal' items={items} disabledOverflow selectedKeys={curNavkey} />
        </Space>
        <div style={{ marginTop: 20 }}>
            <Outlet />
        </div>
    </>);
}
