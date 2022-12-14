import { UserOutlined } from "@ant-design/icons";
import { Col, Layout, Menu, Row, Space, Button } from "antd";
import { useContext, useEffect, useState } from "react";
import { useLocation, useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { APIEndPointContext, AuthContext } from "../context";
import { ROLES } from "../roles";
import { parseUserInfo } from "../utils";
import AppTitle from "./app-title";

const { Header } = Layout;

const AppHeader = () => {
    const auth = useContext(AuthContext);
    const navigate = useNavigate();
    const location = useLocation();
    const topNavKey = location.pathname.split('/')[1];
    // console.log('appHeader - ', topNavKey);
    // const isDevMode = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development');
    const { baseUri, changeBaseUri } = useContext(APIEndPointContext);
    const items = [
        { key: 'dashboard', label: <Link to="/dashboard">Dashboard</Link> },
        { key: 'members', label: <Link to="/members">Members</Link> },
        { key: 'membership', label: <Link to="/membership">Membership</Link> },
        { key: 'bidaward', label: <Link to="/bidaward">Bid Award</Link>, permissions: [ROLES.MODELN, ROLES.MANUFACTURER, ROLES.WHOLESALER] },
        { key: 'invoicelineitem', label: <Link to='/invoicelineitem'>Invoice Line Item</Link>, permissions: [ROLES.MANUFACTURER, ROLES.WHOLESALER] },
    ];

    const navItems = items.filter((item) => {
        if (item.permissions) {
            return item.permissions.indexOf(auth.user) > -1;
        }
        return true;
    });
    // const [peers, setPeers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);

    useEffect(() => {
        /*
        fetch(`${baseUri}/peers`)
            .then(res => res.json())
            .then(data => {
                const peers = data.peers.map((m, idx) => {
                    const peer = parseUserInfo(m);
                    // return { key: `item-${idx}`, label: peer.o, }
                    return { key: `item-${peer.ou.toLowerCase()}`, label: peer.o, }
                });
                setPeers(peers);
            });
        */
        // fetch(`${baseUri}/addresses`);
        fetch(`${baseUri}/me`)
            .then(res => {
                // console.log(res);
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to load response');
                return res.json();
            })
            .then(data => {
                // console.log('received response for me:', data);
                setCurrentUser(parseUserInfo(data.me));
            })
            .catch(error => {
                console.log(error);
            });
    }, [baseUri]);
    return (<>
        <Header style={{ backgroundColor: 'white', color: '#333', boxShadow: '0 1px 2px 0 rgb(0 0 0 / 3%), 0 1px 6px -1px rgb(0 0 0 / 2%), 0 2px 4px 0 rgb(0 0 0 / 2%)' }}>
            <Row>
                <Col flex='auto'>
                    <div style={{ float: 'left', margin: '0px 24px 16px 0' }}>
                        <AppTitle title={currentUser?.ou} />
                    </div>
                    <Menu mode='horizontal' items={navItems} selectedKeys={topNavKey} />
                </Col>
                <Col>
                    <Space>
                        <UserOutlined />
                        {currentUser?.o}
                        <Button type='link' onClick={() => {
                            auth.signout(() => navigate("/"));
                        }}>Sign out</Button>
                    </Space>
                </Col>
            </Row>
        </Header>
    </>);
}

export default AppHeader;