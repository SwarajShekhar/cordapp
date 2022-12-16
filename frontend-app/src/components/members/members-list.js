import React, { useContext, useEffect, useState } from 'react';
import { Button, Input, Space, Spin, Table } from 'antd';
import { APIEndPointContext } from '../../context';
import { UserInfo } from '../../utils';
import { Link } from 'react-router-dom';
import { LoadingOutlined, SearchOutlined } from '@ant-design/icons';

const MembersList = () => {
    const { baseUri } = useContext(APIEndPointContext);
    const [loading, setLoading] = useState([]);
    const [members, setMembers] = useState([]);
    const fetchMembersData = () => {
        // console.log('Fetching members data...');
        setLoading(true);
        fetch(`${baseUri}/members`)
            .then(res => {
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
                return res.json();
            })
            .then(data => {
                // console.log('received members list', data);
                const members = data.map((m, idx) => {
                    const { memberName, memberType, owner, DEAID, DDDID, address, description, status } = m.state.data;
                    const linearId = m.state.data.linearId.id;
                    return { key: 'm_' + idx, linearId, memberName, memberType, owner: new UserInfo(owner).toString(), DEAID, DDDID, address, description, status };
                });
                setMembers(members);
                setLoading(false);
            })
            .catch(error => {
                console.error(error);
                setLoading(false);
            });
    }

    useEffect(() => {
        fetchMembersData();
    }, []);

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
        {
            title: 'Ledger Linear ID', dataIndex: 'linearId', key: 'linearId',
            render: (data) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        { title: 'DEA ID', dataIndex: 'DEAID', key: 'DEAID' },
        { title: 'GLN ID', dataIndex: 'DDDID', key: 'DDDID' },
        { title: 'Name', dataIndex: 'memberName', key: 'memberName', ...getColumnSearchProps('memberName') },
        {
            title: 'Type', dataIndex: 'memberType', key: 'memberType',
            filters: [
                { text: 'HOSPITAL', value: 'HOSPITAL' },
                { text: 'PHARMACY', value: 'PHARMACY' },
            ],
            onFilter: (value, record) => (record.memberType === value),
        },
        { title: 'Status', dataIndex: 'status', key: 'status' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Address', dataIndex: 'address', key: 'address' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
    ];


    return (<>
        <Table columns={columns} loading={loading} dataSource={members} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}

export default MembersList;