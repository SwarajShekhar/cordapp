import { Form, Input, Select, Typography, Button } from "antd";
import { useContext, useState } from "react";
import { redirect, useNavigate } from 'react-router-dom';
import { APIEndPointContext } from "../../context";

const { Option } = Select;
const { Title, Text } = Typography;

const InvoiceLineItemCreate = () => {
    const baseUri = useContext(APIEndPointContext);
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
            <Form.Item name="memberStateUniqueIdentifier"
                label="memberStateUniqueIdentifier"
                rules={[
                    {
                        required: true,
                        message: 'Please input the memberStateUniqueIdentifier!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="productNDC"
                label="productNDC"
                rules={[
                    {
                        required: true,
                        message: 'Please input the productNDC!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="invoiceId"
                label="invoiceId"
                rules={[
                    {
                        required: true,
                        message: 'Please input the invoiceId!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="invoiceDate"
                label="invoiceDate"
                rules={[
                    {
                        required: true,
                        message: 'Please input the invoiceDate!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="bidAwardUniqueIdentifier"
                label="bidAwardUniqueIdentifier"
                rules={[
                    {
                        required: true,
                        message: 'Please input the bidAwardUniqueIdentifier!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="consumer"
                label="consumer"
                rules={[
                    {
                        required: true,
                        message: 'Please input the member status!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="status"
                label="Member status"
                initialValue={'APPROVAL_NEEDED'}
                rules={[
                    {
                        required: true,
                        message: 'Please input the status!',
                    },
                ]}>
                <Input disabled />
            </Form.Item>

            {formErr ? <Text type='danger'>{formErr}</Text> : null}

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