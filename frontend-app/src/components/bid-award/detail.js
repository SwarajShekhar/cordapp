import { Button, Descriptions, Divider, Space, Typography } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { APIEndPointContext, AuthContext } from '../../context';
import { ROLES } from '../../roles';
import { formatDateInfo, UserInfo } from '../../utils';

const BidAwardDetail = () => {
    const params = useParams();
    const { baseUri } = useContext(APIEndPointContext);
    const [member, setMember] = useState(null);
    const [bidAward, setBidAward] = useState(null);
    const auth = useContext(AuthContext);

    const fetchData = async () => {
        try {
            const res = await fetch(`${baseUri}/bidAward/${params.linearId}`);
            const data = await res.json();
            const { bidAwardId, memberStateLinearPointer, productNDC, wholesalerId, startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName, linearId, owner } = data[0].state.data;
            const bidAward = {
                bidAwardId, memberStateLinearPointer: memberStateLinearPointer.pointer.id,
                productNDC, wholesalerId,
                startDate: formatDateInfo(startDate), wacPrice, authorizedPrice, endDate: formatDateInfo(endDate), wholesalerPartyName,
                linearId: linearId.id, owner: new UserInfo(owner).toString()
            };

            const mres = await fetch(`${baseUri}/members/${memberStateLinearPointer.pointer.id}`);
            const mdata = await mres.json();
            const { memberName, memberType, DDDID, DEAID, address, description } = mdata[0].state.data;
            const member = { memberName, memberType, DDDID, DEAID, address, description };
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
        <Descriptions bordered column={2}>
            <Descriptions.Item label='Ledger Linear ID' span={2}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>{bidAward.linearId}</span>
                    {auth.user === ROLES.WHOLESALER ? <Link to={`/invoicelineitem/create/${bidAward.linearId}`}>Create New Invoice Line</Link> : null}
                </div>
            </Descriptions.Item>
            <Descriptions.Item label='Bid Award ID' span={2}>{bidAward.bidAwardId}</Descriptions.Item>
            <Descriptions.Item label='Product Name'>{bidAward.productNDC}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler ID'>{bidAward.wholesalerId}</Descriptions.Item>
            <Descriptions.Item label='WAC Price'>{bidAward.wacPrice}</Descriptions.Item>
            <Descriptions.Item label='Authorized Price'>{bidAward.authorizedPrice}</Descriptions.Item>
            <Descriptions.Item label='StartDate'>{bidAward.startDate}</Descriptions.Item>
            <Descriptions.Item label='End Date'>{bidAward.endDate}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler Party Name'>{bidAward.wholesalerPartyName}</Descriptions.Item>
            <Descriptions.Item label='Owner'>{bidAward.owner}</Descriptions.Item>
        </Descriptions>
        <Divider orientation='left' plain>Global Member Linear ID: <Link to={`/members/${bidAward.memberStateLinearPointer}`}>{bidAward.memberStateLinearPointer}</Link></Divider>
        <Descriptions bordered column={2}>
            <Descriptions.Item label='Member Name'>{member.memberName}</Descriptions.Item>
            <Descriptions.Item label='Member Type'>{member.memberType}</Descriptions.Item>
            <Descriptions.Item label='GLN ID'>{member.DDDID}</Descriptions.Item>
            <Descriptions.Item label='DEA ID'>{member.DEAID}</Descriptions.Item>
            <Descriptions.Item label='Address'>{member.address}</Descriptions.Item>
            <Descriptions.Item label='Description'>{member.description}</Descriptions.Item>
        </Descriptions>
        <br />

    </>);
}

export default BidAwardDetail;
