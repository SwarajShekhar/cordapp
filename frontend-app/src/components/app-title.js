import { Typography } from "antd";

const AppTitle = ({ title }) => {
    if (!title) return <Typography.Title style={{ color: '#01A6DE' }}>AppTitle</Typography.Title>;
    return (title.toLowerCase() === 'modeln' ? <img src={`/modeln-logo-2.png`} alt='Model N Logo' style={{ maxHeight: 25 }} />
        : <Typography.Text style={{ color: '#01A6DE', textTransform: 'capitalize', fontSize: 32 }} strong>{title}</Typography.Text>);
}

export default AppTitle;
