import { Form, Input, Typography, Button } from "antd";
import { useContext, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { APIEndPointContext } from "../../context";

const InvoiceLineItemCreate = () => {
    const { baseUri } = useContext(APIEndPointContext);
    const [formErr, setFormErr] = useState(null);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();

    const onFinish = async (values) => {
        console.log('Success:', values);
        try {
            setFormErr(null);
            setConfirmLoading(true);
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            const fdata = [];
            for (const [key, value] of Object.entries(values)) {
                fdata.push(`${key}=${encodeURIComponent(value)}`);
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
        navigate('/invoicelineitem/list');
    }

    return (<>
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 14 }} onFinish={onFinish} onFinishFailed={onFinishFailed} >
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
                <Input />
            </Form.Item>
            <Form.Item name="memberStateUniqueIdentifier"
                label="Member State Unique Identifier"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Member State Unique Identifier!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="productNDC"
                label="Product Name"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Product Name!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="consumer"
                label="Manufacturer Party"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Manufacturer Party!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="bidAwardUniqueIdentifier"
                label="Bid Award Linear ID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Bid Award Linear ID!',
                    },
                ]}>
                <Input />
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