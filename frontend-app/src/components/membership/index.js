import { SearchOutlined } from "@ant-design/icons";
import { Button, Input, Space, Table } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link } from 'react-router-dom';
import { APIEndPointContext } from "../../context";
import { formatDateInfoShort, UserInfo } from "../../utils";

export const MembershipList = () => {

    const { baseUri } = useContext(APIEndPointContext);
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);

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
            title: 'Global Member Linear ID', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer',
            render: (data, record) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        { title: 'Global Member Name', dataIndex: 'memberName', key: 'memberName', ...getColumnSearchProps('memberName') },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Receiver', dataIndex: 'receiver', key: 'receiver' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate' },
    ];

    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await fetch(`${baseUri}/membership`);
            const data = await res.json();
            console.log('received membership data', data);
            const mres = await fetch(`${baseUri}/members`);
            const mdata = await mres.json();
            // console.log('received members data', mdata);
            const members = mdata.map((m, idx) => {
                return { linearId: m.state.data.linearId.id, memberName: m.state.data.memberName }
            });
            // console.log(members);
            const memberships = data.map((d, idx) => {
                const memberStateLinearPointer = d.state.data.memberStateLinearPointer.pointer.id;
                const member = members.find((member) => (member.linearId === memberStateLinearPointer));
                return {
                    key: 'm_' + idx,
                    linearId: d.state.data.linearId.id,
                    memberStateLinearPointer,
                    memberName: member?.memberName,
                    owner: new UserInfo(d.state.data.owner).toString(),
                    receiver: new UserInfo(d.state.data.receiver).toString(),
                    startDate: formatDateInfoShort(d.state.data.startDate),
                    endDate: formatDateInfoShort(d.state.data.endDate),
                };
            });
            setData(memberships);
        } catch (error) {
            console.error(error);
        }
        setLoading(false);
    }

    useEffect(() => {
        fetchData();
    }, []);

    return (<>
        <Table loading={loading} columns={columns} dataSource={data} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}