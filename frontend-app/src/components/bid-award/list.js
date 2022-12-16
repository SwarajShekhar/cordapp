import { SearchOutlined } from "@ant-design/icons";
import { Button, Input, Space, Table } from "antd";
import { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { APIEndPointContext } from "../../context";
import { UserInfo } from "../../utils";

const BidAwardList = () => {
    const [bidawards, setBidawards] = useState([]);
    const { baseUri } = useContext(APIEndPointContext);
    const [loading, setLoading] = useState(false);

    const fetchData = () => {
        setLoading(true);
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
                setLoading(false);
            })
            .catch(error => {
                console.error(error);
                setLoading(false);
            });
    }

    useEffect(() => {
        fetchData();
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
            render: (data) => (<Link to={`/bidaward/${data}`}>{data}</Link>)
        },
        { title: 'Bid Award ID', dataIndex: 'bidAwardId', key: 'bidAwardId' },
        { title: 'Start Date', dataIndex: 'startDate', key: 'startDate', align: 'center' },
        { title: 'End Date', dataIndex: 'endDate', key: 'endDate', align: 'center' },
        {
            title: 'Global Member Linear ID', dataIndex: 'memberStateLinearPointer', key: 'memberStateLinearPointer',
            render: (data) => (<Link to={`/members/${data}`}>{data}</Link>), ...getColumnSearchProps('memberStateLinearPointer')
        },
        { title: 'Owner', dataIndex: 'owner', key: 'owner' },
        { title: 'Product Name', dataIndex: 'productNDC', key: 'productNDC', ...getColumnSearchProps('productNDC') },
        { title: 'WAC Price', dataIndex: 'wacPrice', key: 'wacPrice', align: 'center' },
        { title: 'Authorized Price', dataIndex: 'authorizedPrice', key: 'authorizedPrice', align: 'center' },
        { title: 'Wholesaler ID', dataIndex: 'wholesalerId', key: 'wholesalerId', align: 'center' },
        { title: 'Wholesaler Party Name', dataIndex: 'wholesalerPartyName', key: 'wholesalerPartyName', align: 'center' },
        // { title: '', dataIndex: '', key: '' },
    ];
    return (<>
        <Table loading={loading} columns={columns} dataSource={bidawards} size='middle' pagination={{ pageSize: 10, showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} items` }}></Table>
    </>);
}

export default BidAwardList;