import { Form, Input, Select, Typography, Button } from "antd";
import { useContext, useState } from "react";
import { redirect, useNavigate } from 'react-router-dom';
import { APIEndPointContext } from "../../context";


const { Option } = Select;
const { Title, Text } = Typography;

const MemmberPrposalCreate = () => {
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
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            const fdata = [];
            for (const [key, value] of Object.entries(values)) {
                // console.log(`${key}: ${value}`);
                fdata.push(`${key}=${encodeURIComponent(value)}`);
            }

            const res = await fetch(`${baseUri}/memberProposal`, { method: 'POST', headers, body: fdata.join('&') });
            const txt = await res.text();
            setConfirmLoading(false);
            if (!res.ok) {
                throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
            }
            form.resetFields();
            // onCreate();
            navigate('/members/proposal');
        } catch (error) {
            console.log('failed to post request', error);
            if (error.hasOwnProperty('message')) {
                setFormErr(error.message);
            }
        }
    };

    const onFinishFailed = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    return (<div>
        <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 14 }} onFinish={onFinish} onFinishFailed={onFinishFailed} >
            <Form.Item name="memberName"
                label="Member Name"
                rules={[
                    {
                        required: true,
                        message: 'Please input the name of member!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="memberType"
                label="Member Type"
                rules={[
                    {
                        required: true,
                        message: 'Please input the type of member!',
                    },
                ]}>
                <Select placeholder='Select Member Type'>
                    <Option value='HOSPITAL'>Hospital</Option>
                </Select>
            </Form.Item>
            <Form.Item name="description"
                label="Description"
                rules={[
                    {
                        required: true,
                        message: 'Please input the description!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="DEAID"
                label="DEAID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the DEAID!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="DDDID"
                label="DDDID"
                rules={[
                    {
                        required: true,
                        message: 'Please input the DDDID!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="memberStatus"
                label="Member Status"
                rules={[
                    {
                        required: true,
                        message: 'Please input the member status!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="address"
                label="Address"
                rules={[
                    {
                        required: true,
                        message: 'Please input the address!',
                    },
                ]}>
                <Input />
            </Form.Item>
            <Form.Item name="memberStateProposalStatus"
                label="Member State Proposal Status"
                initialValue={'PROPOSED'}
                rules={[
                    {
                        required: true,
                        message: 'Please input the member state proposal status!',
                    },
                ]}>
                <Input disabled />
            </Form.Item>

            {formErr ? <Text type='danger'>{formErr}</Text> : null}

            <Form.Item wrapperCol={{ span: 12, offset: 6 }}>
                <Button type="primary" htmlType="submit" loading={confirmLoading}>
                    Submit
                </Button>
            </Form.Item>

        </Form>
    </div>);
}

export default MemmberPrposalCreate;