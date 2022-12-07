import React, { useState } from 'react';
import { ConfigProvider, Layout, message, notification, theme } from 'antd';
import 'antd/dist/reset.css';
import './App.css';

import { AppHeader } from './components';
import { Outlet } from 'react-router';
import { APIEndPointContext } from './context';

const { Content, Footer } = Layout;

function App() {
  const apiuri = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 'http://localhost:8082/api' : '/api';
  const [apiEndPoint, setApiEndPoint] = useState(apiuri);

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
  }

  return (<>
    <APIEndPointContext.Provider value={apiEndPoint}>
      <Layout>
        <AppHeader onChangeUser={changeUser} />
        <Content style={{ marginTop: 20, padding: '0 50px' }}>
          <div style={{ padding: '24px', backgroundColor: 'white' }}>
            <Outlet />
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>&copy; 2022 Model N, Inc.</Footer>
      </Layout>
    </APIEndPointContext.Provider>
  </>);
}

export default App;
