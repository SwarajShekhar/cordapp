import { Button, Descriptions, Divider, Space, Typography } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { APIEndPointContext } from '../../context';
import { UserInfo } from '../../utils';

const BidAwardDetail = () => {
    const params = useParams();
    const { baseUri } = useContext(APIEndPointContext);
    const [member, setMember] = useState(null);
    const [bidAward, setBidAward] = useState(null);

    const fetchData = async () => {
        try {
            const res = await fetch(`${baseUri}/bidAward/${params.linearId}`);
            const data = await res.json();
            const { bidAwardId, memberStateLinearPointer, productNDC, wholesalerId, startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName, linearId, owner } = data[0].state.data;
            const bidAward = {
                bidAwardId, memberStateLinearPointer: memberStateLinearPointer.pointer.id,
                productNDC, wholesalerId,
                startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName,
                linearId: linearId.id, owner: new UserInfo(owner).toString()
            };

            const mres = await fetch(`${baseUri}/members/${memberStateLinearPointer.pointer.id}`);
            const mdata = await mres.json();
            const { memberName, memberType } = mdata[0].state.data;
            const member = { memberName, memberType };
            setMember(member);
            setBidAward(bidAward);

        } catch (error) {
            console.log('bidawarddetail -> fetch data: ', error);
        }
    }
    useEffect(() => {
        fetchData();
    }, [baseUri, params.linearId])

    if (bidAward === null) {
        return <>Loading data!</>
    }

    return (<>
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0' }}>
            <Typography.Text>Ledger Linear ID: {bidAward.linearId}</Typography.Text>
            <Link to={`/invoicelineitem/create/${bidAward.linearId}`}>Create New Invoice Line</Link>
        </div>

        <Descriptions bordered>
            <Descriptions.Item label='Bid Award ID' span={3}>{bidAward.bidAwardId}</Descriptions.Item>
            <Descriptions.Item label='Product Name' span={1}>{bidAward.productNDC}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler ID' span={2}>{bidAward.wholesalerId}</Descriptions.Item>
            <Descriptions.Item label='WAC Price' span={1}>{bidAward.wacPrice}</Descriptions.Item>
            <Descriptions.Item label='Authorized Price' span={2}>{bidAward.authorizedPrice}</Descriptions.Item>
            <Descriptions.Item label='StartDate' span={1}>{bidAward.startDate}</Descriptions.Item>
            <Descriptions.Item label='End Date' span={2}>{bidAward.endDate}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler Party Name' span={1}>{bidAward.wholesalerPartyName}</Descriptions.Item>
            <Descriptions.Item label='Owner' span={2}>{bidAward.owner}</Descriptions.Item>
        </Descriptions>
        <Divider plain>Member Details</Divider>
        <Descriptions bordered>
            <Descriptions.Item label='Global Member Linear ID' span={3}><Link to={`/members/${bidAward.memberStateLinearPointer}`}>{bidAward.memberStateLinearPointer}</Link></Descriptions.Item>
            <Descriptions.Item label='Member Name'>{member.memberName}</Descriptions.Item>
            <Descriptions.Item label='Member Type'>{member.memberType}</Descriptions.Item>
        </Descriptions>
        <br />

    </>);
}

export default BidAwardDetail;
