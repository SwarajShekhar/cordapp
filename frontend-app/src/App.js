import React, { useState } from 'react';
import { Breadcrumb, Layout } from 'antd';
import 'antd/dist/reset.css';
import './App.css';

import { AppHeader } from './components';
import { Outlet, useLocation, useNavigate } from 'react-router';
import { APIEndPointContext } from './context';
import { HomeOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';

const { Content, Footer } = Layout;

function App() {
  const apiuri = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 'http://localhost:8082/api' : '/api';
  const [apiEndPoint, setApiEndPoint] = useState(apiuri);
  const navigate = useNavigate();
  // for quick change of user during development mode
  const changeUser = ({ key }) => {
    let port = {
      modeln: '8080',
      wholesaler: '8084',
      gpo: '8086',
      manufacturer: '8082'
    }
    const rkey = key.substring('item-'.length);
    setApiEndPoint(`http://localhost:${port[rkey]}/api`);
    navigate('/');
  }

  const location = useLocation();
  const segments = location.pathname.split('/').splice(1);

  const breadcrumbItems = [
    <Breadcrumb.Item key='home'>
      <Link to='/'><HomeOutlined /></Link>
    </Breadcrumb.Item>
  ].concat(segments.map((m, idx) => <Breadcrumb.Item key={idx}>{m}</Breadcrumb.Item>));

  return (<>
    <APIEndPointContext.Provider value={apiEndPoint}>
      <Layout>
        <AppHeader onChangeUser={changeUser} />
        <Content style={{ marginTop: 20, padding: '0 50px' }}>
          <div style={{ padding: '24px', backgroundColor: 'white' }}>
            <Breadcrumb>{breadcrumbItems}</Breadcrumb>
            <Outlet />
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>&copy; 2022 Model N, Inc.</Footer>
      </Layout>
    </APIEndPointContext.Provider>
  </>);
}

export default App;
