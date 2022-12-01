import { Col, Layout, Menu, Row } from "antd";
import { useEffect, useState } from "react";
import { Link } from 'react-router-dom';

const { Header } = Layout;

const AppHeader = () => {

    const items = [
        { key: 'item-2', label: <Link to="/members">Members</Link> },
        { key: 'item-3', label: <Link to="/membership">Membership</Link> },
        { key: 'item-4', label: <Link to="/bidaward">Bid Award</Link> },
        { key: 'item-5', label: <Link to='/invoicelineitem'>Invoice Line Item</Link> },
    ];

    const [me, setMe] = useState('');  

    useEffect(() => {
        fetch(`/me`)
            .then(res => {
                console.log(res);
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to load response');
                return res.json();
            })
            .then(data => {
                console.log('received response for me:', data);
                setMe(data.me);
            })
            .catch(error => {
                console.log(error);
            });
    }, []);

    return (<>
        <Header>
            <Row>
                <Col flex='auto'>
                    <div style={{ float: 'left', margin: '0px 24px 16px 0' }}>
                        <Link to='/'>
                            <img src={'/modeln-logo.png'} style={{ maxHeight: 20 }} />
                        </Link>
                    </div>
                    <Menu mode='horizontal' theme='dark' items={items} />
                </Col>
                <Col>
                    <span style={{ color: 'white' }}>Node: {me}</span>
                </Col>
            </Row>
        </Header>
    </>);
}

export default AppHeader;