import React, { useContext, useEffect, useRef, useState } from 'react';
import { Table, Space, Typography, Menu, Button, Spin, notification, Input, Divider } from 'antd';
import { LoadingOutlined, SearchOutlined } from '@ant-design/icons';
import { APIEndPointContext } from '../../context';
import { Link } from 'react-router-dom';
import { UserInfo } from '../../utils';


const ActionColumnMenu = ({ dataid, onActionTaken }) => {
    const baseUri = useContext(APIEndPointContext);
    const [loading, setLoading] = useState(false);

    const handleApprove = () => {
        updateProposal('APPROVED');
    }

    const handleReject = () => {
        updateProposal('REJECTED');
    }

    const updateProposal = async (action) => {
        try {
            console.log(`handle ${action} for: ${dataid}`);
            setLoading(true);
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            const body = `status=${encodeURIComponent(action)}`;
            const res = await fetch(`${baseUri}/invoiceLineItem/${dataid}`, { method: 'POST', headers, body });
            const txt = await res.text();
            setLoading(false);
            if (!res.ok) {
                throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
            }
            openNotification('Status had been updated');
        } catch (error) {
            console.log('failed to post request', error);
            openNotification(error.toString())
        }
        onActionTaken();
    }

    const openNotification = (description) => {
        notification.open({
            message: 'Member Proposal Response Status',
            description,
            onClick: () => {
                console.log('Notification Clicked!');
            },
        });
    };

    return (<Space split={<Divider type='vertical' />}>
        {
            loading ? <Spin indicator={<LoadingOutlined />} /> : <>
                <Button size='small' type='link' disabled={loading} onClick={handleApprove}>Approve</Button>
                <Button size='small' type='link' disabled={loading} onClick={handleReject}>Reject</Button>
            </>
        }
    </Space>);
}


const InvoiceLineItemList = () => {

    const baseUri = useContext(APIEndPointContext);
    const [invoiceLineItems, setInvoiceLineItems] = useState([]);
    // const [bidAwards, setBidAwards] = useState([]);

    const fetchData = async () => {
        try {
            const invoiceRes = await fetch(`${baseUri}/invoiceLineItem`);
            if (!invoiceRes.ok || invoiceRes.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
            const invoiceData = await invoiceRes.json();
            console.log('received invoicelineitem list', invoiceData);
            const bidRes = await fetch(`${baseUri}/bidAward`);
            const bidData = await bidRes.json();
            console.log('received bid award data', bidData);
            const bidAwards = bidData.map((m, idx) => {
                return { linearId: m.state.data.linearId.id, wacPrice: m.state.data.wacPrice, authorizedPrice: m.state.data.authorizedPrice }
            });

            const invoiceLineItems = invoiceData.map((m, idx) => {
                const { owner, consumer, productNDC, invoiceId, invoiceDate, status, manufacturer, wholesaler } = m.state.data;
                const linearId = m.state.data.linearId.id;
                const memberStateLinearPointer = m.state.data.memberStateLinearPointer.pointer.id;
                const bidAwardLinearPointer = m.state.data.bidAwardLinearPointer.pointer.id;
                // find corresponding bidAward
                const bidAward = bidAwards.find((b) => b.linearId === bidAwardLinearPointer);
                const bidAwardWacPrice = (bidAward) ? bidAward.wacPrice : null;
                const bidAwardAuthorizedPrice = (bidAward) ? bidAward.authorizedPrice : null;
                return { key: 'm_' + idx, linearId, memberStateLinearPointer, bidAwardLinearPointer, bidAwardWacPrice, bidAwardAuthorizedPrice, owner: new UserInfo(owner).toString(), consumer: new UserInfo(consumer).toString(), productNDC, invoiceId, invoiceDate, status, manufacturer: new UserInfo(manufacturer).toString(), wholesaler: new UserInfo(wholesaler).toString() }
            });
            setInvoiceLineItems(invoiceLineItems);
        } catch (error) {
            console.error(error);
        }
    }

    const handleActionTaken = () => {
        fetchData();
    }

    useEffect(() => {
        fetchData();
    }, []);

    const columns = [
        {
            title: 'linearId', dataIndex: 'linearId', key: 'linearId',
            render: (data, recrod) => {
                return <Link to={`/invoicelineitem/${data}`}>{data}</Link>
            }
        },
        {
            title: 'Member State Linear Pointer', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer',
            render: (data) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        {
            title: 'Bid Award Linear Pointer', dataIndex: 'bidAwardLinearPointer', key: 'bidAwardLinearPointer',
            render: (data) => (<Link to={`/bidaward/${data}`}>{data}</Link>)
        },
        { title: 'Bid Award WAC Price', dataIndex: 'bidAwardWacPrice', key: 'bidAwardWacPrice' },
        { title: 'Bid Award Authorized Price', dataIndex: 'bidAwardAuthorizedPrice', key: 'bidAwardAuthorizedPrice' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Consumer', dataIndex: 'consumer', key: 'consumer' },
        { title: 'Manufacturer', dataIndex: 'manufacturer', key: 'manufacturer' },
        { title: 'Wholesaler', dataIndex: 'wholesaler', key: 'wholesaler' },
        { title: 'Product NDC', dataIndex: 'productNDC', key: 'productNDC' },
        { title: 'Invoice Id', dataIndex: 'invoiceId', key: 'invoiceId' },
        { title: 'Invoice Date', dataIndex: 'invoiceDate', key: 'invoiceDate' },
        { title: 'Status', dataIndex: 'status', key: 'status' },
        {
            title: 'Actions', key: 'actions', dataIndex: 'linearId', width: 150, align: 'center',
            render: (data, record) => (record.status === 'APPROVAL_NEEDED' ? <ActionColumnMenu dataid={data} onActionTaken={handleActionTaken} /> : null),
        },
    ];
    return (<>
        <Table columns={columns} dataSource={invoiceLineItems} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}

export default InvoiceLineItemList;