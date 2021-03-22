import React from 'react';

export const ModelDetailsAttributeList = ({attributes}) => {

  let list = [];
  for (let key of Object.keys(attributes)) {
    list.push(
        <React.Fragment key={'attribute-' + key}>
          <h6 className="details-label">{key}</h6>
          <p>{attributes[key]}</p>
        </React.Fragment>
    )
  }

  return list;

}