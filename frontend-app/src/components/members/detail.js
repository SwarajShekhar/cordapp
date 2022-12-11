import { Descriptions } from "antd";
import { useContext, useEffect, useState } from "react";
import { useParams } from "react-router";
import { APIEndPointContext } from "../../context";
import { UserInfo } from "../../utils";

const MemberDetail = () => {
    const params = useParams();
    const baseUri = useContext(APIEndPointContext);
    const [member, setMember] = useState(null);

    useEffect(() => {
        fetch(`${baseUri}/members/${params.memberId}`)
            .then(res => res.json())
            .then(data => {
                const { DDDID, DEAID, address, description, linearId, memberName, memberType, owner, status } = data[0].state.data;
                const member = { DDDID, DEAID, address, description, linearId: linearId.id, memberName, memberType, owner: new UserInfo(owner).toString(), status }
                setMember(member);
            })
    }, []);

    if (member === null) return <>Loading data!</>;
    return (<>
        <Descriptions title={member.memberName} bordered column={2}>
            <Descriptions.Item label='Liner ID' span={2}>{member.linearId}</Descriptions.Item>
            <Descriptions.Item label='Member Name'>{member.memberName}</Descriptions.Item>
            <Descriptions.Item label='Member Type'>{member.memberType}</Descriptions.Item>
            <Descriptions.Item label='DDDID'>{member.DDDID}</Descriptions.Item>
            <Descriptions.Item label='DEAID'>{member.DEAID}</Descriptions.Item>
            <Descriptions.Item label='Status'>{member.status}</Descriptions.Item>
            <Descriptions.Item label='Owner'>{member.owner}</Descriptions.Item>
            <Descriptions.Item label='Address' span={2}>{member.address}</Descriptions.Item>
            <Descriptions.Item label='Description' span={2}>{member.description}</Descriptions.Item>
        </Descriptions>
    </>);
}
export default MemberDetail;
