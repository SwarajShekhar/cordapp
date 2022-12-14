import { Form, Input, Typography, Button } from "antd";
import { useContext, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { APIEndPointContext } from "../../context";

const BidAwardCreate = () => {
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
                label="Bid Award ID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Bid Award ID!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="memberStateUUID"
                label="Member State UUID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the type of Member State UUID!',
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
            <Form.Item name="wholesalerId"
                label="Wholesaler ID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Wholesaler ID!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="startDate"
                label="Start Date"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Start Date!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="endDate"
                label="End Date"
                rules={[
                    {
                        required: true,
                        message: 'Please input the End Date!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="wacPrice"
                label="WAC Price"
                rules={[
                    {
                        required: true,
                        message: 'Please input the WAC Price!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="authorizedPrice"
                label="Authorized Price"
                rules={[
                    {
                        required: true,
                        message: 'Please input the Authorized Price!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="broadcastToMembers"
                label="Broadcast To Members"
                rules={[
                    {
                        required: true,
                        message: 'Please input Broadcast To Members!',
                    },
                ]}>
                <Input />
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

export default BidAwardCreate;