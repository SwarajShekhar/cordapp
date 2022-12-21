import { Form, Input, Typography, Button, Select, Space, Row, Tooltip, DatePicker } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from 'react-router-dom';
import { APIEndPointContext } from "../../context";

const InvoiceLineItemCreate = () => {
    const params = useParams();
    const { baseUri } = useContext(APIEndPointContext);
    const [formErr, setFormErr] = useState(null);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [form] = Form.useForm();
    const [memInfo, setMemInfo] = useState(null);
    const navigate = useNavigate();
    const disableItems = (params && params.bidawardid) ? true : false;
    const onFinish = async (values) => {
        console.log('Success:', values);
        try {
            setFormErr(null);
            setConfirmLoading(true);
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            const fdata = [];
            for (const [key, value] of Object.entries(values)) {
                const val = (key === 'invoiceDate') ? value.format('YYYY-MM-DDTHH:mm:ss.SSS[z]') : value;
                fdata.push(`${key}=${encodeURIComponent(val)}`);
            }

            const res = await fetch(`${baseUri}/invoiceLineItem`, { method: 'POST', headers, body: fdata.join('&') });
            const txt = await res.text();
            setConfirmLoading(false);
            if (!res.ok) {
                throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
            }
            form.resetFields();
            // onCreate();
            navigate('/invoicelineitem/list');
        } catch (error) {
            console.log('failed to post request', error);
            if (error.hasOwnProperty('message')) {
                setFormErr(error.message);
            }
        }
    }

    const onFinishFailed = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    const handleCancel = () => {
        form.resetFields();
        navigate(-1);
    }

    const fetchBidAwardData = async () => {
        if (!params.bidawardid) {
            return;
        }
        try {
            console.log('fetchBidAwardData', params.bidawardid)
            const res = await fetch(`${baseUri}/bidAward/${params.bidawardid}`);
            const data = await res.json();
            const memberStateUniqueIdentifier = data[0].state.data.memberStateLinearPointer.pointer.id;
            // get bidaward info
            form.setFieldsValue({
                productNDC: data[0].state.data.productNDC,
                memberStateUniqueIdentifier,
                bidAwardUniqueIdentifier: data[0].state.data.linearId.id,
                consumer: data[0].state.data.owner,
                // invoiceDate: dayjs('2023-01-01T10:10:10.111z')
            });
            // get members info
            const memRes = await fetch(`${baseUri}/members/${memberStateUniqueIdentifier}`);
            const memData = await memRes.json();
            // console.log('members data', memData);
            setMemInfo(memData[0].state.data);
        } catch (error) {
            console.log('something went wrong', error);
        }
    }

    useEffect(() => {
        fetchBidAwardData();
    }, [])

    return (<>
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 14 }} onFinish={onFinish} onFinishFailed={onFinishFailed} autoComplete='off'>
            <Form.Item name="invoiceId"
                label="Invoice ID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Invoice ID!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="invoiceDate"
                label="Invoice Date"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Invoice Date!',
                    },
                ]}>
                <DatePicker />
            </Form.Item>
            <Form.Item label="Member State Unique Identifier" required>
                <Space>
                    <Form.Item name="memberStateUniqueIdentifier"
                        noStyle
                        rules={[
                            {
                                required: true,
                                message: 'Please input the Member State Unique Identifier!',
                            },
                        ]}>
                        <Input disabled={disableItems} />
                    </Form.Item>
                    {
                        memInfo ? (<Tooltip title={`${memInfo?.memberName}, ${memInfo?.address}`}>
                            <Link to={`/members/${memInfo?.linearId.id}`}>Details</Link>
                        </Tooltip>) : null
                    }

                </Space>
            </Form.Item>

            <Form.Item name="productNDC"
                label="Product Name"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Product Name!',
                    },
                ]}>
                <Input disabled={disableItems} />
            </Form.Item>
            <Form.Item name="consumer"
                label="Manufacturer Party"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Manufacturer Party!',
                    },
                ]}>
                <Input disabled={disableItems} />
            </Form.Item>
            <Form.Item name="bidAwardUniqueIdentifier"
                label="Bid Award Linear ID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Bid Award Linear ID!',
                    },
                ]}>
                <Input disabled={disableItems} />
            </Form.Item>
            <Form.Item name="quantity"
                label="Quantity"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Quantity!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="status"
                label="Ledger status"
                initialValue={'APPROVAL_NEEDED'}
                rules={[
                    {
                        required: true,
                        message: 'Please input the status!',
                    },
                ]}>
                <Input disabled />
            </Form.Item>

            {formErr ? <Typography.Text type='danger'>{formErr}</Typography.Text> : null}

            <Form.Item wrapperCol={{ span: 12, offset: 6 }}>
                <Button type="primary" htmlType="submit" loading={confirmLoading}>
                    Submit
                </Button>
                <Button type="link" onClick={handleCancel}>Cancel</Button>
            </Form.Item>

        </Form>
    </>);
}

export default InvoiceLineItemCreate;