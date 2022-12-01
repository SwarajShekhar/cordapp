import React, { useEffect, useState } from 'react';
import { Table, Space, Typography, Menu, Button, Spin, notification } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';

const { Title } = Typography;



const ActionColumnMenu = ({ dataid, onActionTaken }) => {
    const [loading, setLoading] = useState(false);

    const handleApprove = () => {
        updateProposal('APPROVED');
    }

    const handleReject = () => {
        updateProposal('REJECTED');
    }

    const updateProposal = async (action) => {
        try {
            console.log(`handle ${action} for: ${dataid}`);
            setLoading(true);
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            const body = `memberStateProposalIdentifier=${encodeURIComponent(dataid)}&memberStateProposalStatus=${encodeURIComponent(action)}`;
            const res = await fetch(`/memberProposalResponse`, { method: 'POST', headers, body });
            const txt = await res.text();
            setLoading(false);
            if (!res.ok) {
                throw new Error(`network response wast not ok. [${res.status} ${res.statusText}] - ${txt}`);
            }
            openNotification('Status had been updated');
        } catch (error) {
            console.log('failed to post request', error);
            openNotification(error.toString())
        }
        onActionTaken();
    }

    const openNotification = (description) => {
        notification.open({
            message: 'Member Proposal Response Status',
            description,
            onClick: () => {
                console.log('Notification Clicked!');
            },
        });
    };

    return (<Space>
        {loading ? <Spin indicator={<LoadingOutlined />} /> : null}
        <Button type='link' disabled={loading} onClick={handleApprove}>Approve</Button>
        <Button type='link' disabled={loading} onClick={handleReject}>Reject</Button>
    </Space>);
}

const MemberProposalList = ({ uri }) => {
    const [members, setMembers] = useState([]);
    const fetchMembersData = () => {
        // console.log('Fetching members data...');
        fetch(uri)
            .then(res => {
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
                return res.json();
            })
            .then(data => {
                console.log('received members list', data);
                const members = data.map((m, idx) => {
                    const { memberName, memberType, owner, responder, DEAID, DDDID, address, description, memberStateProposalStatus } = m.state.data;
                    return { key: 'm_' + idx, memberName, memberType, owner, responder, DEAID, DDDID, address, description, memberStateProposalStatus, linearId: m.state.data.linearId.id };
                });
                setMembers(members);
            })
            .catch(error => {
                console.error(error);
            });
    }

    const handleActionTaken = () => {
        fetchMembersData();
    }
    useEffect(() => {
        fetchMembersData();
    }, [uri]);

    const columns = [
        { title: 'DEAID', dataIndex: 'DEAID', key: 'DEAID' },
        { title: 'DDDID', dataIndex: 'DDDID', key: 'DDDID' },
        { title: 'Name', dataIndex: 'memberName', key: 'memberName' },
        { title: 'Type', dataIndex: 'memberType', key: 'memberType' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Responder', dataIndex: 'responder', key: 'responder' },
        { title: 'Address', dataIndex: 'address', key: 'address' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
        {
            title: 'Member State Proposal Status', dataIndex: 'memberStateProposalStatus', key: 'memberStateProposalStatus',
            sorter: (a, b) => {
                const aStatus = a.memberStateProposalStatus.toUpperCase();
                const bStatus = b.memberStateProposalStatus.toUpperCase();
                if (aStatus < bStatus) {
                    return -1;
                }
                if (aStatus > bStatus) {
                    return 1;
                }
                return 0;
            },
            sortDirections: ['descend'],
        },
    ];

    // console.log('propsal-list -> uri', uri);
    if (uri === '/memberProposal') {
        columns.push({
            title: 'Actions', key: 'actions', dataIndex: 'linearId',
            render: (data, record) => (record.memberStateProposalStatus === 'PROPOSED' ? <ActionColumnMenu dataid={data} onActionTaken={handleActionTaken} /> : null),
        })
    }

    return (<>
        <Table columns={columns} size='middle' dataSource={members} pagination={{ pageSize: 6 }}></Table>
    </>);
}

export default MemberProposalList;