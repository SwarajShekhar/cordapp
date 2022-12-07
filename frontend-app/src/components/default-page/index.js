import { Typography } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { APIEndPointContext } from '../../context';
import { parseUserInfo } from '../../utils';
const { Title } = Typography;

export const DefaultPage = () => {
    const [me, setMe] = useState('');
    const baseUri = useContext(APIEndPointContext);

    useEffect(() => {
        fetch(`${baseUri}/me`)
            .then(res => {
                // console.log(res);
                if (!res.ok || res.headers.get('content-type').toLowerCase().indexOf('application/json') === -1) throw new Error('Failed to load response');
                return res.json();
            })
            .then(data => {
                // console.log('received response for me:', data);
                setMe(parseUserInfo(data.me));
            })
            .catch(error => {
                console.log(error);
            });
    }, [baseUri]);

    return (<div style={{ paddingTop: 50 }}>
        <Title>{me.o}</Title>
        <p>OU={me.ou}, L={me.l}, C={me.c}</p>
        <p>{baseUri}</p>
    </div >)
}