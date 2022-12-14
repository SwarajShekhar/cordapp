import { Descriptions } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { APIEndPointContext } from '../../context';
import { UserInfo } from '../../utils';

const BidAwardDetail = () => {
    const params = useParams();
    const { baseUri } = useContext(APIEndPointContext);
    const [bidAward, setBidAward] = useState(null);

    useEffect(() => {
        fetch(`${baseUri}/bidAward/${params.linearId}`)
            .then(res => res.json())
            .then(data => {
                const { bidAwardId, memberStateLinearPointer, productNDC, wholesalerId, startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName, linearId, owner } = data[0].state.data;

                const bidAward = {
                    bidAwardId, memberStateLinearPointer: memberStateLinearPointer.pointer.id,
                    productNDC, wholesalerId: new UserInfo(wholesalerId).toString(),
                    startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName: new UserInfo(wholesalerPartyName).toString(),
                    linearId: linearId.id, owner: new UserInfo(owner).toString()
                };
                setBidAward(bidAward)
            });

    }, [baseUri, params.linearId])

    if (bidAward === null) {
        return <>Loading data!</>
    }

    return (<>
        <Descriptions title={bidAward.linearId} bordered>
            <Descriptions.Item label='Bid Award ID' span={3}>{bidAward.bidAwardId}</Descriptions.Item>
            <Descriptions.Item label='Global Member Linear ID' span={3}><Link to={`/members/${bidAward.memberStateLinearPointer}`}>{bidAward.memberStateLinearPointer}</Link></Descriptions.Item>
            <Descriptions.Item label='Product Name' span={1}>{bidAward.productNDC}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler ID' span={2}>{bidAward.wholesalerId}</Descriptions.Item>
            <Descriptions.Item label='WAC Price' span={1}>{bidAward.wacPrice}</Descriptions.Item>
            <Descriptions.Item label='Authorized Price' span={2}>{bidAward.authorizedPrice}</Descriptions.Item>
            <Descriptions.Item label='StartDate' span={1}>{bidAward.startDate}</Descriptions.Item>
            <Descriptions.Item label='End Date' span={2}>{bidAward.endDate}</Descriptions.Item>
            <Descriptions.Item label='Wholesaler Party Name' span={1}>{bidAward.wholesalerPartyName}</Descriptions.Item>
            <Descriptions.Item label='Owner' span={2}>{bidAward.owner}</Descriptions.Item>
        </Descriptions>
    </>);
}

export default BidAwardDetail;
