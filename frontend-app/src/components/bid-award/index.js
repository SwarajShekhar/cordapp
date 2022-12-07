import { PlusCircleOutlined } from "@ant-design/icons";
import { Divider, Menu, Space, Typography } from "antd";
import { Link } from "react-router-dom";
import { Outlet } from "react-router";

const { Title } = Typography;
export const BidAward = () => {
    const items = [
        { key: 'bid-item-1', label: <Link to='/bidaward/list'>List</Link> },
        { key: 'bid-item-2', label: <Link to='/bidaward/create'><PlusCircleOutlined /> Create New</Link> },
    ];

    return (<>
        <Space split={<Divider type="vertical" />}>
            <Title>Bid Award</Title>
            <Menu mode='horizontal' items={items} disabledOverflow />
        </Space>
        <div style={{ marginTop: 20 }}>
            <Outlet />
        </div>
    </>);
}

export { default as BidAwardList } from './list';
export { default as BidAwardCreate } from './create';
