import { DownOutlined, UserOutlined, UserSwitchOutlined } from "@ant-design/icons";
import { Col, Dropdown, Layout, Menu, Row, Button, Divider, Space, message, Typography } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link } from 'react-router-dom';
import { APIEndPointContext } from "../context";
import { parseUserInfo } from "../utils";

const { Header } = Layout;

const AppHeader = ({ onChangeUser }) => {
    const isDevMode = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development');
    const baseUri = useContext(APIEndPointContext);
    const items = [
        { key: 'item-2', label: <Link to="/members">Members</Link> },
        // { key: 'item-3', label: 'Membership', disabled: true },
        { key: 'item-4', label: <Link to="/bidaward">Bid Award</Link> },
        { key: 'item-5', label: <Link to='/invoicelineitem'>Invoice Line Item</Link> },
    ];
    const [peers, setPeers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);


    const onClick = ({ key }) => {
        // peers.find((peer) => peer.label)
        // message.info(`Click on item ${key}`);
        onChangeUser(key);
    };

    useEffect(() => {
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
                        {currentUser ? (currentUser.ou === 'modeln' ? <img src={`/modeln-logo-2.png`} style={{ maxHeight: 25 }} /> : <Typography.Text style={{ color: '#0B588E', textTransform: 'capitalize', fontSize: 32 }} strong>{currentUser?.ou}</Typography.Text>) : null}
                    </div>
                    <Menu mode='horizontal' items={items} />
                </Col>
                <Col>
                    {
                        isDevMode ? (<Dropdown menu={{ items: peers, onClick: onChangeUser }}>
                            <a onClick={(e) => e.preventDefault()}><Space><UserOutlined />{currentUser?.o} <DownOutlined /></Space></a>
                        </Dropdown>) : <Space><UserOutlined />{currentUser?.o}</Space>
                    }
                </Col>
            </Row>
        </Header>
    </>);
}

export default AppHeader;