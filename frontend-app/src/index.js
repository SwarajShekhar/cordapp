import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { createBrowserRouter, RouterProvider, Navigate, Link } from "react-router-dom";
import ErrorPage from './error-page';
import { MemberDetail, MemberProposalList, MemberPrposalCreate, MembersList } from './components/members';
import { MembershipList } from './components/membership';
import { BidAwardList, BidAwardCreate, BidAwardDetail } from './components/bid-award';
import { InvoiceLineItemList, InvoiceLineItemCreate, InvoiceLineItemDetail } from './components/invoice-line-item';
import { DefaultPage, ContentPage } from './components/default-page';
import { PlusCircleOutlined } from '@ant-design/icons';
import { LoginPage } from './pages';
import { ROLES } from './roles';

const membersMenuItems = [
  { key: 'list', label: <Link to='/members/list'>Global List</Link> },
  { key: 'proposal', label: <Link to='/members/proposal'>My List</Link> },
  { key: 'proposalcreate', label: <Link to='/members/proposalcreate'><PlusCircleOutlined /> Add Member</Link>, permissions: [ROLES.GPO] },
];

const bidAwardMenuItems = [
  { key: 'list', label: <Link to='/bidaward/list'>List</Link>, permissions: [ROLES.MODELN, ROLES.MANUFACTURER, ROLES.WHOLESALER] },
  { key: 'create', label: <Link to='/bidaward/create'><PlusCircleOutlined /> Create New</Link>, permissions: [ROLES.MANUFACTURER] },
];

const invoiceLineMenuItems = [
  { key: 'list', label: <Link to='/invoicelineitem/list'>List</Link> },
  { key: 'create', label: <Link to='/invoicelineitem/create'><PlusCircleOutlined /> Create New</Link>, permissions: [ROLES.WHOLESALER] },
];

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      { path: '', element: <LoginPage /> },
      { path: 'dashboard', element: <ContentPage title='Dashboard'><DefaultPage /></ContentPage> },
      {
        path: 'members',
        element: <ContentPage title='Members' items={membersMenuItems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <MembersList /> },
          { path: ':memberId', element: <MemberDetail /> },
          { path: 'proposal', element: <MemberProposalList uri='/memberProposal' /> },
          // { path: 'proposalpending', element: <MemberProposalList uri='/memberProposal/pending' /> },
          { path: 'proposalcreate', element: <MemberPrposalCreate /> },
        ]
      },
      {
        path: 'membership',
        element: <ContentPage title='Membership'><MembershipList /></ContentPage>,
      },
      {
        path: 'bidaward',
        element: <ContentPage title='Bid Award' items={bidAwardMenuItems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <BidAwardList /> },
          { path: 'create', element: <BidAwardCreate /> },
          { path: ':linearId', element: <BidAwardDetail /> },
        ],
      },
      {
        path: 'invoicelineitem',
        element: <ContentPage title='Invoice Line Item' items={invoiceLineMenuItems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <InvoiceLineItemList /> },
          { path: 'create', element: <InvoiceLineItemCreate /> },
          { path: 'create/:bidawardid', element: <InvoiceLineItemCreate /> },
          { path: ':linearid', element: <InvoiceLineItemDetail /> },
        ],
      },
    ]
  }
]);


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
