import React, { useState } from 'react';
import { Breadcrumb, Layout } from 'antd';
import 'antd/dist/reset.css';
import './App.css';

import { AppHeader } from './components';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { APIEndPointContext, AuthContext } from './context';
import { HomeOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';

const { Content, Footer } = Layout;

function App() {
  const apiuri = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 'http://localhost:8082/api' : '/api';
  const [apiEndPoint, setApiEndPoint] = useState(apiuri);
  let [user, setUser] = React.useState(null);

  const navigate = useNavigate();
  // for quick change of user during development mode
  const changeBaseUri = ({ key }) => {
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
  let signin = (newUser, callback) => {
    const apiuri = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? `http://localhost:${newUser}/api` : '/api';
    setApiEndPoint(apiuri);
    setUser(newUser);
    callback();
  };

  let signout = (callback) => {
    setApiEndPoint(null);
    setUser(null);
    callback();
  };
  const value = { user, signin, signout };
  const location = useLocation();
  const segments = location.pathname.split('/').splice(1);

  const breadcrumbItems = [
    <Breadcrumb.Item key='home'>
      <Link to='/dashboard'><HomeOutlined /></Link>
    </Breadcrumb.Item>
  ].concat(segments.map((m, idx) => <Breadcrumb.Item key={idx}>{m}</Breadcrumb.Item>));
  return (<>
    <AuthContext.Provider value={value}>
      <APIEndPointContext.Provider value={{ baseUri: apiEndPoint, changeBaseUri }}>
        <Layout>
          {user ? (<>
            <AppHeader />
            <Content style={{ marginTop: 20, padding: '0 50px' }}>
              <div style={{ padding: '24px', backgroundColor: 'white' }}>
                <Breadcrumb>{breadcrumbItems}</Breadcrumb>
                <Outlet />
              </div>
            </Content>
          </>) : <Outlet />
          }
          <Footer style={{ textAlign: 'center' }}>&copy; 2022 Model N, Inc.</Footer>
        </Layout>
      </APIEndPointContext.Provider>
    </AuthContext.Provider>
  </>);
}

export default App;
