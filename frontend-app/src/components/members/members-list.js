import React, { useContext, useEffect, useState } from 'react';
import { Table } from 'antd';
import { APIEndPointContext } from '../../context';
import { UserInfo } from '../../utils';
import { Link } from 'react-router-dom';

const MembersList = () => {
    const baseUri = useContext(APIEndPointContext);

    const [members, setMembers] = useState([]);
    const fetchMembersData = () => {
        // console.log('Fetching members data...');
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
            })
            .catch(error => {
                console.error(error);
            });
    }

    useEffect(() => {
        fetchMembersData();
    }, []);

    const columns = [
        {
            title: 'linearId', dataIndex: 'linearId', key: 'linearId',
            render: (data) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        { title: 'DEAID', dataIndex: 'DEAID', key: 'DEAID' },
        { title: 'DDDID', dataIndex: 'DDDID', key: 'DDDID' },
        { title: 'Name', dataIndex: 'memberName', key: 'memberName' },
        { title: 'Type', dataIndex: 'memberType', key: 'memberType' },
        { title: 'Status', dataIndex: 'status', key: 'status' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Address', dataIndex: 'address', key: 'address' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
    ];


    return (<>
        <Table columns={columns} dataSource={members} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}

export default MembersList;