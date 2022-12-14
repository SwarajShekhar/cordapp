import React, { useContext, useEffect, useState } from 'react';
import { Table, Space, Typography, Button, Spin, notification, Input, Form, Select } from 'antd';
import { LoadingOutlined, SearchOutlined } from '@ant-design/icons';
import { APIEndPointContext, AuthContext } from '../../context';
import { UserInfo } from '../../utils';
import { Link } from 'react-router-dom';
import { ROLES } from '../../roles';

const EditableCell = ({
    editing,
    dataIndex,
    title,
    inputType,
    selectOptions,
    record,
    index,
    children,
    ...restProps
}) => {
    const inputElem = inputType === 'select' ? <Select options={selectOptions} /> : <Input />
    return (
        <td {...restProps}>
            {editing ? (<Form.Item
                name={dataIndex}
                style={{ margin: 0, }}
                rules={[{ required: true, message: `Please Input ${title}!`, },]}>
                {inputElem}
            </Form.Item>)
                : (children)
            }
        </td>
    );
};

const MemberProposalList = ({ uri }) => {
    const auth = useContext(AuthContext);
    const [form] = Form.useForm();
    const [editingKey, setEditingKey] = useState('');
    const [loading, setLoading] = useState(false);
    const { baseUri } = useContext(APIEndPointContext);
    const [members, setMembers] = useState([]);
    const isEditing = (record) => record.key === editingKey;
    const openNotification = (description) => {
        notification.open({
            message: 'Member Proposal Response Status',
            description,
            onClick: () => {
                console.log('Notification Clicked!');
            },
        });
    };

    const edit = (record) => {
        form.setFieldsValue({
            address: '',
            description: '',
            ...record,
        });
        setEditingKey(record.key);
    };

    const cancel = () => {
        setEditingKey('');
    };

    const save = async (key) => {
        try {
            const values = await form.validateFields();
            console.log('save -> ', values, key);
            // dont make any changes to the proposal just, cancel the edit
            const { memberStateProposalStatus } = values;

            // cancel edit if Member State Prposal Status is not Approved or Rejected.
            if (memberStateProposalStatus !== 'APPROVED' && memberStateProposalStatus !== 'REJECTED') {
                cancel();
                return;
            }

            // extract the original data
            const newData = [...members];
            const index = newData.findIndex((item) => key === item.key);
            if (index > -1) {
                const item = newData[index];
                let newItem;
                if (memberStateProposalStatus === 'APPROVED') {
                    // update data from all editable fields
                    newItem = {
                        ...item,
                        ...values,
                        memberStateProposalStatus,
                        memberStateProposalIdentifier: item.linearId,
                    };

                } else if (memberStateProposalStatus === 'REJECTED') {
                    // only update memberStateProposalStatus
                    newItem = {
                        ...item,
                        memberStateProposalStatus,
                        memberStateProposalIdentifier: item.linearId,
                    };
                }
                console.log('new Item', newItem)
                setLoading(true);
                const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
                const fdata = [];
                for (const [key, value] of Object.entries(newItem)) {
                    fdata.push(`${key}=${encodeURIComponent(value)}`);
                }

                const res = await fetch(`${baseUri}/memberProposalResponse`, { method: 'POST', headers, body: fdata.join('&') });
                const txt = await res.text();

                if (!res.ok) {
                    throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
                }
                setLoading(false);
                openNotification('Status had been updated');

            } else {
                throw new Error('Could not find matching key');
            }
            setEditingKey('');
        } catch (err) {
            console.log('validate failed: ', err);
            openNotification(err.toString());
        }
        fetchMembersData();
    }

    const fetchMembersData = () => {
        // console.log('Fetching members data...');
        fetch(`${baseUri}${uri}`)
            .then(res => {
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
                return res.json();
            })
            .then(data => {
                console.log('received members list', data);
                const members = data.map((m, idx) => {
                    const { DDDID, DEAID, additionalInfo, address, description, endDate, internalName, linearId, memberIdIdentifier, memberName, memberStateProposalStatus, memberStatus, memberType, owner, responder, startDate } = m.state.data;
                    return { key: 'm_' + idx, memberName, memberType, owner: new UserInfo(owner).toString(), responder: new UserInfo(responder).toString(), DEAID, DDDID, address, description, memberStatus, memberStateProposalStatus, linearId: linearId.id, startDate, endDate, additionalInfo, internalName, memberIdIdentifier: memberIdIdentifier ? memberIdIdentifier.pointer.id : null };
                });
                setMembers(members);
            })
            .catch(error => {
                console.error(error);
            });
    }

    useEffect(() => {
        fetchMembersData();
    }, [uri]);

    const getColumnSearchProps = (dataIndex) => ({
        filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
            <div style={{ padding: 8, }} onKeyDown={(e) => e.stopPropagation()}>
                <Input
                    placeholder={`Search ${dataIndex}`}
                    value={selectedKeys[0]}
                    onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onPressEnter={() => { confirm(); }}
                    style={{ marginBottom: 8, display: 'block', }}
                />
                <Space>
                    <Button type='link' onClick={() => { clearFilters(); }}>Reset</Button>
                    <Button type="link" onClick={() => { confirm(); }}>Filter</Button>
                    <Button type='link' onClick={() => close()}>Close</Button>
                </Space>
            </div>
        ),
        filterIcon: (filtered) => (<SearchOutlined style={{ color: filtered ? '#1890ff' : undefined, }} />),
        onFilter: (value, record) => (record[dataIndex].toString().toLowerCase().includes(value.toLowerCase())),
    });

    const columns = [
        { title: 'Ledger Linear ID', dataIndex: 'linearId', key: 'linearId' },
        {
            title: 'Global Member Linear ID', dataIndex: 'memberIdIdentifier', key: 'memberIdIdentifier',
            render: (data, record) => (data ? <Link to={`/members/${data}`}>{data}</Link> : <span>{record.memberStateProposalStatus}</span>)
        },
        { title: 'DEA ID', dataIndex: 'DEAID', key: 'DEAID' },
        { title: 'GLN ID', dataIndex: 'DDDID', key: 'DDDID' },
        { title: 'Name', dataIndex: 'memberName', key: 'memberName', ...getColumnSearchProps('memberName') },
        { title: 'Type', dataIndex: 'memberType', key: 'memberType' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner', ...getColumnSearchProps('owner') },
        { title: 'Responder', dataIndex: 'responder', key: 'responder' },
        { title: 'Address', dataIndex: 'address', key: 'address', editable: true },
        { title: 'Description', dataIndex: 'description', key: 'description', editable: true, },
        { title: 'Member Status', dataIndex: 'memberStatus', key: 'memberStatus' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate' },
        { title: 'Additional Info', dataIndex: 'additionalInfo', key: 'additionalInfo' },
        { title: 'Internal Name', dataIndex: 'internalName', key: 'internalName' },
        {
            title: 'Ledger Status', dataIndex: 'memberStateProposalStatus', key: 'memberStateProposalStatus',
            filters: [
                { text: 'APPROVED', value: 'APPROVED' },
                { text: 'REJECTED', value: 'REJECTED' },
                { text: 'PROPOSED', value: 'PROPOSED' },
            ],
            onFilter: (value, record) => (record.memberStateProposalStatus === value),
            editable: true,
        },
    ];
    if (auth.user === ROLES.MODELN) {
        // add action column if only current user is with MODELN role..
        columns.push({
            title: 'Action', key: 'action', dataIndex: 'linearId', width: 150, align: 'center',
            // render: (data, record) => (record.memberStateProposalStatus === 'PROPOSED' ? <ActionColumnMenu dataid={data} onActionTaken={handleActionTaken} /> : null),
            render: (data, record) => {
                const editable = isEditing(record);
                return record.memberStateProposalStatus === 'PROPOSED' ? (editable ? (
                    <Space direction='vertical'>
                        {
                            loading ? <Spin indicator={<LoadingOutlined />} /> : (<>
                                <Typography.Link disabled={loading} onClick={() => save(record.key)} style={{ marginRight: 8, }}>Save</Typography.Link>
                                <Typography.Link disabled={loading} onClick={cancel}>Cancel</Typography.Link>
                            </>)
                        }
                    </Space>
                ) : (<Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>Update</Typography.Link>)) : null;
            },
        });
    }

    const mergedColumns = columns.map((col) => {
        if (!col.editable) {
            return col;
        }
        return {
            ...col,
            onCell: (record) => ({
                record,
                inputType: col.dataIndex === 'memberStateProposalStatus' ? 'select' : 'text',
                selectOptions: [{ value: 'APPROVED', label: 'Approve' }, { value: 'REJECTED', label: 'Reject' }, { value: 'PROPOSED', label: 'PROPOSED' }],
                dataIndex: col.dataIndex,
                title: col.title,
                editing: isEditing(record),
            }),
        };
    });

    return (<>
        <Form form={form} component={false}>
            <Table components={{
                body: { cell: EditableCell }
            }} columns={mergedColumns} size='middle' dataSource={members} pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items`, onChange: cancel, }}></Table>
        </Form>
    </>);
}

export default MemberProposalList;
