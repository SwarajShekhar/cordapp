import { Form, Input, Select, Typography, Button } from "antd";
import { useContext, useState } from "react";
import { redirect, useNavigate } from 'react-router-dom';
import { APIEndPointContext } from "../../context";

const { Option } = Select;
const { Title, Text } = Typography;

const BidAwardCreate = () => {
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

            const res = await fetch(`${baseUri}/bidAward`, { method: 'POST', headers, body: fdata.join('&') });
            const txt = await res.text();
            setConfirmLoading(false);
            if (!res.ok) {
                throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
            }
            form.resetFields();
            // onCreate();
            navigate('/bidaward/list');
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
        navigate('/bidaward/list');
    }

    return (<>
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 14 }} onFinish={onFinish} onFinishFailed={onFinishFailed} >
            <Form.Item name="bidAwardId"
                label="bidAwardId"
                rules={[
                    {
                        required: true,
                        message: 'Please input the bidAwardId!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="memberStateUUID"
                label="memberStateUUID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the type of memberStateUUID!',
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
            <Form.Item name="wholesalerId"
                label="wholesalerId"
                rules={[
                    {
                        required: true,
                        message: 'Please input the wholesalerId!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="startDate"
                label="startDate"
                rules={[
                    {
                        required: true,
                        message: 'Please input the startDate!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="endDate"
                label="endDate"
                rules={[
                    {
                        required: true,
                        message: 'Please input the endDate!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="wacPrice"
                label="wacPrice"
                rules={[
                    {
                        required: true,
                        message: 'Please input the wacPrice!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="authorizedPrice"
                label="authorizedPrice"
                rules={[
                    {
                        required: true,
                        message: 'Please input the authorizedPrice!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="broadcastToMembers"
                label="broadcastToMembers"
                rules={[
                    {
                        required: true,
                        message: 'Please input the broadcastToMembers!',
                    },
                ]}>
                <Input />
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

export default BidAwardCreate;