import React from 'react';
import { Layout} from 'antd';
import 'antd/dist/reset.css';
import './App.css';

import { AppHeader } from './components';
import { Outlet } from 'react-router';

const { Content, Footer } = Layout;

function App() {
  return (<>
    <Layout>
      <AppHeader />
      <Content style={{ marginTop: 20, padding: '0 50px' }}>
        <div style={{ padding: '24px', background: '#fff' }}>
          <Outlet />
        </div>
      </Content>
      <Footer style={{ textAlign: 'center' }}>&copy; 2022 Model N, Inc.</Footer>
    </Layout>
  </>);
}

export default App;
