import { Menu, Space, Typography } from "antd";
import { Outlet } from "react-router";
import { Link } from "react-router-dom";

const { Title } = Typography;

export const Members = () => {
    const items = [
        { key: 'mem-item-1', label: <Link to='/members/list'>List</Link> },
        { key: 'mem-item-2', label: <Link to='/members/proposal'>Proposal</Link> },
        { key: 'mem-item-3', label: <Link to='/members/proposalpending'>Pending</Link> },
        { key: 'mem-item-4', label: <Link to='/members/proposalcreate'>Create New Proposal</Link> },
    ];

    return (<>
        <Space direction='horizontal' size="middle" style={{ display: 'flex' }}>
            <Title>Members</Title>
            <Menu mode='horizontal' items={items} />
        </Space>
        <div style={{ marginTop: 20 }}>
            <Outlet />
        </div>
    </>);
}


export { default as MembersList } from './members-list';
export { default as MemberProposalList } from './proposal-list';
export { default as MemberPrposalCreate } from './proposal-create';