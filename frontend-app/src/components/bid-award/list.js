import { Table } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { APIEndPointContext } from "../../context";
import { UserInfo } from "../../utils";

const BidAwardList = () => {
    const [bidawards, setBidawards] = useState([]);
    const { baseUri } = useContext(APIEndPointContext);

    const fetchData = () => {
        fetch(`${baseUri}/bidAward`)
            .then(res => {
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to get server response');
                return res.json();
            })
            .then(data => {
                console.log('received bidaward list', data);
                const bidawards = data.map((m, idx) => {
                    const { authorizedPrice, bidAwardId, endDate, owner, productNDC, startDate, wacPrice, wholesalerId, wholesalerPartyName } = m.state.data;
                    const linearId = m.state.data.linearId.id;
                    const memberStateLinearPointer = m.state.data.memberStateLinearPointer.pointer.id;
                    return { key: 'm_' + idx, authorizedPrice, bidAwardId, endDate, linearId, memberStateLinearPointer, owner: new UserInfo(owner).toString(), productNDC, startDate, wacPrice, wholesalerId, wholesalerPartyName };
                });
                setBidawards(bidawards);
            })
            .catch(error => {
                console.error(error);
            });
    }

    useEffect(() => {
        fetchData();
    }, []);

    const columns = [
        {
            title: 'Ledger Linear ID', dataIndex: 'linearId', key: 'linearId',
            render: (data) => (<Link to={`/bidaward/${data}`}>{data}</Link>)
        },
        { title: 'Bid Award ID', dataIndex: 'bidAwardId', key: 'bidAwardId' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate', align: 'center' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate', align: 'center' },
        {
            title: 'Global Member Linear ID', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer',
            render: (data) => (<Link to={`/members/${data}`}>{data}</Link>)
        },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Product Name', dataIndex: 'productNDC', key: 'productNDC' },
        { title: 'WAC Price', dataIndex: 'wacPrice', key: 'wacPrice', align: 'center' },
        { title: 'Authorized Price', dataIndex: 'authorizedPrice', key: 'authorizedPrice', align: 'center' },
        { title: 'Wholesaler ID', dataIndex: 'wholesalerId', key: 'wholesalerId', align: 'center' },
        { title: 'Wholesaler Party Name', dataIndex: 'wholesalerPartyName', key: 'wholesalerPartyName', align: 'center' },
        // { title: '', dataIndex: '', key: '' },
    ];
    return (<>
        <Table columns={columns} dataSource={bidawards} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}

export default BidAwardList;