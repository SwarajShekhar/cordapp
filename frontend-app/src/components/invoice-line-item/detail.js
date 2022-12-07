import { Badge, Card, Descriptions, Divider, Space, Timeline, Typography } from "antd";
import { useContext, useEffect, useState } from "react";
import { useParams } from "react-router";
import { APIEndPointContext } from "../../context";

const InvoiceLineItemDetail = () => {
    const baseUri = useContext(APIEndPointContext);
    const [invoiceLineItem, setInvoiceLineItem] = useState(null);
    const [invoiceLineItems, setInvoiceLineItems] = useState([]);

    const params = useParams();
    const mapdata = (m, idx) => {
        const { owner, consumer, productNDC, invoiceId, invoiceDate, status } = m.state.data;
        const linearId = m.state.data.linearId.id;
        const memberStateLinearPointer = m.state.data.memberStateLinearPointer.pointer.id;
        const bidAwardLinearPointer = m.state.data.bidAwardLinearPointer.pointer.id;
        return { key: 'm_' + idx, linearId, memberStateLinearPointer, bidAwardLinearPointer, owner, consumer, productNDC, invoiceId, invoiceDate, status }
    }

    useEffect(() => {
        // fetch invoice line item details
        fetch(`${baseUri}/invoiceLineItem/${params.linearid}`)
            .then(res => res.json())
            .then(data => {
                const items = data.map(mapdata);
                if (items.length > 1) {
                    console.warn('expected an array with only one item for invoicelineitem details')
                }
                setInvoiceLineItem(items[0]);
            });
        // fetch history
        fetch(`${baseUri}/invoiceLineItem/${params.linearid}/all`)
            .then(res => res.json())
            .then(data => {
                const invoiceLineItems = data.map(mapdata);
                setInvoiceLineItems(invoiceLineItems);
                // console.log('received hisotry for invoicelineitem: ', params.linearid, data);
            });
    }, [])

    return (<>
        <Space direction="vertical">
            <Descriptions title={invoiceLineItem?.linearId} bordered column={4}>
                <Descriptions.Item label='memberStateLinearPointer' span={4}>{invoiceLineItem?.memberStateLinearPointer}</Descriptions.Item>
                <Descriptions.Item label='bidAwardLinearPointer' span={4}>{invoiceLineItem?.bidAwardLinearPointer}</Descriptions.Item>
                <Descriptions.Item label='owner' span={2}>{invoiceLineItem?.owner}</Descriptions.Item>
                <Descriptions.Item label='consumer' span={2}>{invoiceLineItem?.consumer}</Descriptions.Item>
                <Descriptions.Item label='invoiceId' span={2}>{invoiceLineItem?.invoiceId}</Descriptions.Item>
                <Descriptions.Item label='invoiceDate' span={2}>{invoiceLineItem?.invoiceDate}</Descriptions.Item>
                <Descriptions.Item label='productNDC' span={4}>{invoiceLineItem?.productNDC}</Descriptions.Item>
                <Descriptions.Item label='status' span={4}>
                    <Badge status={invoiceLineItem?.status === 'APPROVED' ? 'success' : 'processing'} text={invoiceLineItem?.status}></Badge>
                </Descriptions.Item>
            </Descriptions>
            <Typography.Title level={5}>History</Typography.Title>
            <Timeline>
                {
                    invoiceLineItems.map(item => (
                        <Timeline.Item key={item.key} color={item.status === 'APPROVED' ? 'green' : 'gray'}>
                            <p>owner: {item.owner}</p>
                            <p>consumer: {item.consumer}</p>
                            <p>{item.status}</p>
                        </Timeline.Item>
                    ))
                }
            </Timeline>
        </Space>
    </>);
}

export default InvoiceLineItemDetail;