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

const membersMenuItems = [
  { key: 'list', label: <Link to='/members/list'>List</Link> },
  { key: 'proposal', label: <Link to='/members/proposal'>Proposal</Link> },
  // { key: 'proposalpending', label: <Link to='/members/proposalpending'>Pending</Link> },
  { key: 'proposalcreate', label: <Link to='/members/proposalcreate'><PlusCircleOutlined /> Add Member</Link> },
];

const membershipMenuItems = [
  { key: 'list', label: <Link to='/membership/list'>List</Link> }
];

const bidAwarditems = [
  { key: 'list', label: <Link to='/bidaward/list'>List</Link> },
  { key: 'create', label: <Link to='/bidaward/create'><PlusCircleOutlined /> Create New</Link> },
];

const invoiceLineMenuitems = [
  { key: 'list', label: <Link to='/invoicelineitem/list'>List</Link> },
  { key: 'create', label: <Link to='/invoicelineitem/create'><PlusCircleOutlined /> Create New</Link> },
];

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      { path: '', element: <DefaultPage /> },
      {
        path: 'members',
        element: <ContentPage title='Members' items={membersMenuItems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <MembersList /> },
          { path: ':memberId', element: <MemberDetail /> },
          { path: 'proposal', element: <MemberProposalList uri='/memberProposal' /> },
          { path: 'proposalpending', element: <MemberProposalList uri='/memberProposal/pending' /> },
          { path: 'proposalcreate', element: <MemberPrposalCreate /> },
        ]
      },
      {
        path: 'membership',
        element: <ContentPage title='Membership' items={membershipMenuItems} />,
        children: [
          { path: '', element: <Navigate to='list' /> },
          { path: 'list', element: <MembershipList /> },
        ]
      },
      {
        path: 'bidaward',
        element: <ContentPage title='Bid Award' items={bidAwarditems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <BidAwardList /> },
          { path: 'create', element: <BidAwardCreate /> },
          { path: ':linearId', element: <BidAwardDetail /> },
        ],
      },
      {
        path: 'invoicelineitem',
        element: <ContentPage title='Invoice Line Item' items={invoiceLineMenuitems} />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <InvoiceLineItemList /> },
          { path: 'create', element: <InvoiceLineItemCreate /> },
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
