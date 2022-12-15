import { useContext } from 'react';
import { Form, Typography, Select, Button, Input, Space } from "antd";
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { AuthContext } from '../context';
import AppTitle from '../components/app-title';

const LoginPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const auth = useContext(AuthContext);
    const [form] = Form.useForm();
    const from = location.state?.from?.pathname || "/dashboard";

    const currentPort = window.location.port; //; //
    const terminals = ([
        { ou: 'modeln', o: 'ModelN', port: '8080' },
        { ou: 'manufacturer', o: 'MANUFACTURER1', port: '8082' },
        { ou: 'wholesaler', o: 'WHOLESALER1', port: '8084' },
        { ou: 'gpo', o: 'GPO1', port: '8086' },
    ]).filter(t => (t.port === currentPort || currentPort === '3000'));

    const APP_TITLE = currentPort === '3000' ? 'FrontendApp' : terminals.find((t) => t.port === currentPort).ou;
    const onTerminalChange = (value) => {
        console.log('onTerminalChange', value);
        const terminal = terminals.find((t => t.ou === value));
        if (terminal) {
            form.setFieldsValue({ username: terminal.o });
        }
    }

    const itnitialValues = currentPort === '3000' ? null : { username: terminals[0].o, terminal: terminals[0].ou };

    const onFinish = (values) => {
        console.log('Success:', values);
        const terminal = terminals.find((t => t.ou === values.terminal));
        if (terminal) {
            // auth.signin(values.username, () => {
            auth.signin(terminal.port, () => {
                // Send them back to the page they tried to visit when they were redirected to the login page.
                navigate(from, { replace: true });
            });
        }
    };

    const onFinishFailed = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };

    return (
        <div style={{ margin: `100px auto`, width: 350, backgroundColor: 'white', padding: 20, boxShadow: '0 1px 2px 0 rgb(0 0 0 / 3%), 0 1px 6px -1px rgb(0 0 0 / 2%), 0 2px 4px 0 rgb(0 0 0 / 2%)' }}>
            <Space direction='vertical'>
                <AppTitle title={APP_TITLE}></AppTitle>
                <Typography.Paragraph >Please sign in contiue to: {from}</Typography.Paragraph>
            </Space>
            <Form form={form} layout='vertical' onFinish={onFinish}
                onFinishFailed={onFinishFailed} initialValues={itnitialValues}>
                <Form.Item label="Username"
                    name="username"
                    rules={[
                        {
                            required: true,
                            message: 'Please input your username!',
                        },
                    ]}>
                    <Input placeholder="Username" />
                </Form.Item>
                <Form.Item label='Connect to' name='terminal' rules={[
                    { required: true, message: 'Please select a node to connect to!' }
                ]}>
                    <Select onChange={onTerminalChange}>
                        {terminals.map((t, idx) => <Select.Option key={idx} value={t.ou}>{t.ou.toUpperCase()}</Select.Option>)}
                    </Select>
                </Form.Item>
                <Button type="primary" htmlType="submit" size='large' block>Log in</Button>
            </Form>
        </div>
    );

}

export default LoginPage;