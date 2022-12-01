import React, { useEffect, useState } from 'react';
import { Table } from 'antd';

const MembersList = () => {
    const [members, setMembers] = useState([]);
    const fetchMembersData = () => {
        // console.log('Fetching members data...');
        fetch('/members')
            .then(res => {
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
                return res.json();
            })
            .then(data => {
                // console.log('received members list', data);
                const members = data.map((m, idx) => {
                    const { memberName, memberType, owner, DEAID, DDDID, address, description, status } = m.state.data;
                    return { key: 'm_' + idx, memberName, memberType, owner, DEAID, DDDID, address, description, status };
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
        <Table columns={columns} dataSource={members} size='middle' pagination={{ pageSize: 6 }}></Table>
    </>);
}

export default MembersList;