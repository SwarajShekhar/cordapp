import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import ErrorPage from './error-page';
import { Members, MemberProposalList, MemberPrposalCreate, MembersList } from './components/members';
import { Membership } from './components/membership';
import { BidAward, BidAwardList, BidAwardCreate } from './components/bid-award';
import { InvoiceLineItem, InvoiceLineItemList, InvoiceLineItemCreate, InvoiceLineItemDetail } from './components/invoice-line-item';
import { DefaultPage } from './components/default-page';

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      { path: '', element: <DefaultPage /> },
      {
        path: 'members',
        element: <Members />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <MembersList /> },
          { path: 'proposal', element: <MemberProposalList uri='/memberProposal' /> },
          { path: 'proposalpending', element: <MemberProposalList uri='/memberProposal/pending' /> },
          { path: 'proposalcreate', element: <MemberPrposalCreate /> },
        ]
      },
      { path: 'membership', element: <Membership /> },
      {
        path: 'bidaward',
        element: <BidAward />,
        children: [
          { path: '', element: <Navigate to="list" /> },
          { path: 'list', element: <BidAwardList /> },
          { path: 'create', element: <BidAwardCreate /> },
        ],
      },
      {
        path: 'invoicelineitem',
        element: <InvoiceLineItem />,
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
