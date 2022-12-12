import { Table, Tooltip, Typography } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link } from 'react-router-dom';
import { APIEndPointContext } from "../../context";
import { UserInfo } from "../../utils";


export const MembershipList = () => {

    const baseUri = useContext(APIEndPointContext);
    const [data, setData] = useState([]);

    const columns = [
        { title: 'Linear Id', dataIndex: 'linearId', key: 'linearId' },
        {
            title: 'Member State Linear Pointer', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer',
            render: (data, record) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Receiver', dataIndex: 'receiver', key: 'receiver' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate' },
    ];

    useEffect(() => {
        fetch(`${baseUri}/membership`)
            .then(res => res.json())
            .then(data => {
                console.log('received membership data', data);
                const memberships = data.map((d, idx) => {
                    return {
                        key: 'm_' + idx,
                        linearId: d.state.data.linearId.id,
                        memberStateLinearPointer: d.state.data.memberStateLinearPointer.pointer.id,
                        owner: new UserInfo(d.state.data.owner).toString(),
                        receiver: new UserInfo(d.state.data.receiver).toString(),
                        startDate: d.state.data.startDate,
                        endDate: d.state.data.endDate,
                    };
                });
                setData(memberships);
            })
    }, []);

    return (<>
        <Table columns={columns} dataSource={data} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}