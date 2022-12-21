import { Descriptions } from "antd";
import { useContext, useEffect, useState } from "react";
import { useParams } from "react-router";
import { APIEndPointContext } from "../../context";
import { formatDateInfo, UserInfo } from "../../utils";

const MemberDetail = () => {
    const params = useParams();
    const { baseUri } = useContext(APIEndPointContext);
    const [member, setMember] = useState(null);

    useEffect(() => {
        fetch(`${baseUri}/members/${params.memberId}`)
            .then(res => res.json())
            .then(data => {
                const { DDDID, DEAID, address, description, linearId, memberName, memberType, owner, status, eventDate } = data[0].state.data;
                const member = {
                    DDDID, DEAID, address, description, linearId: linearId.id, memberName, memberType, owner: new UserInfo(owner).toString(), status,
                    eventDate: formatDateInfo(eventDate)
                }
                setMember(member);
            })
    }, []);

    if (member === null) return <>Loading data!</>;
    return (<>
        <Descriptions title={member.memberName} bordered column={2}>
            <Descriptions.Item label='Ledger Linear ID' span={2}>{member.linearId}</Descriptions.Item>
            <Descriptions.Item label='Member Name'>{member.memberName}</Descriptions.Item>
            <Descriptions.Item label='Member Type'>{member.memberType}</Descriptions.Item>
            <Descriptions.Item label='GLN ID'>{member.DDDID}</Descriptions.Item>
            <Descriptions.Item label='DEA ID'>{member.DEAID}</Descriptions.Item>
            <Descriptions.Item label='Status'>{member.status}</Descriptions.Item>
            <Descriptions.Item label='Owner'>{member.owner}</Descriptions.Item>
            <Descriptions.Item label='Address' span={2}>{member.address}</Descriptions.Item>
            <Descriptions.Item label='Description' span={2}>{member.description}</Descriptions.Item>
            <Descriptions.Item label='Event Date'>{member.eventDate}</Descriptions.Item>
        </Descriptions>
    </>);
}
export default MemberDetail;
