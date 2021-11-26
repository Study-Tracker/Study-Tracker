import React from 'react';
import {Breadcrumb} from "react-bootstrap";

export const Breadcrumbs = ({crumbs}) => {
  const steps = crumbs.map(c => (
    <Breadcrumb.Item key={"nav-breadcrumb-" + c.label} {...(!!c.url ? {href: c.url} : {active: true} )}>
      {c.label}
    </Breadcrumb.Item>
  ))
  return (
      <Breadcrumb>
        {steps}
      </Breadcrumb>
  )
}