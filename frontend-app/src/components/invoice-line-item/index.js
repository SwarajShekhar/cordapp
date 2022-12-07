import { PlusCircleOutlined } from "@ant-design/icons";
import { Divider, Menu, Space, Typography } from "antd";
import { Link } from "react-router-dom";
import { Outlet } from "react-router";

const { Title } = Typography;
export const InvoiceLineItem = () => {
    const items = [
        { key: 'bid-item-1', label: <Link to='/invoicelineitem/list'>List</Link> },
        { key: 'bid-item-2', label: <Link to='/invoicelineitem/create'><PlusCircleOutlined /> Create New</Link> },
    ];

    return (<>
        <Space split={<Divider type="vertical" />}>
            <Title>Invoice Line Item</Title>
            <Menu mode='horizontal' items={items} disabledOverflow />
        </Space>
        <div style={{ marginTop: 20 }}>
            <Outlet />
        </div>
    </>);
}

export { default as InvoiceLineItemList } from './list';
export { default as InvoiceLineItemCreate } from './create';
export { default as InvoiceLineItemDetail } from './detail';
