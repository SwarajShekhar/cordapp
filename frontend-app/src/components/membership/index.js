import { Table, Typography } from "antd";
import { useContext, useEffect, useState } from "react";
import { APIEndPointContext } from "../../context";


export const MembershipList = () => {

    const baseUri = useContext(APIEndPointContext);
    const [data, setData] = useState([]);

    const columns = [
        { title: 'Linear Id', dataIndex: 'linearId', key: 'linearId' },
        { title: 'Member State Linear Pointer', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer' },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Receiver', dataIndex: 'receiver', key: 'receiver' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate' },
    ];

    useEffect(() => {
        fetch(`${baseUri}/membership`)
            .then(res => res.json())
            .then(data => {
                console.log(data);
                const memberships = data.map((d, idx) => {
                    return {
                        ...d.state.data,
                        key: 'm_' + idx,
                        linearId: d.state.data.linearId.id,
                        memberStateLinearPointer: d.state.data.memberStateLinearPointer.pointer.id
                    };
                });
                setData(memberships);
            })
    }, []);

    return (<>
        <Table columns={columns} dataSource={data} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}