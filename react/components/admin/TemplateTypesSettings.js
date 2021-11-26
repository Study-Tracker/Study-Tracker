import React, {useEffect, useState} from 'react';

import {Button, Card, Dropdown} from 'react-bootstrap';
import {PlusCircle} from "react-feather";
import {Link} from 'react-router-dom';

import ToolkitProvider from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import cellEditFactory from 'react-bootstrap-table2-editor';

export const TemplateTypesSettings = () => {
    const [templateTypes, setTemplateTypes] = useState([]);

    const fetchTemplates = () => {
      fetch('/api/entryTemplate')
        .then(rs => rs.json())
        .then(json => setTemplateTypes(json))
        .catch(error => console.log(error));
    };

    const handleStatusChange = ({ id, statusToSet }) => {
      fetch(`/api/entryTemplate/${ id }/status?${ new URLSearchParams({ active: statusToSet }) }`, {
        method: 'POST',
      })
        .then(fetchTemplates)
        .catch(error => {
          console.log(error);
          swal('Something went wrong', 'Template status change failed.');
        });
    };

    const templateTableColumns = [
      {
        dataField: 'name',
        text: 'Name',
        sort: true,
        validator: (newValue) => {
          if (!newValue) {
            return {
              valid: false,
              message: 'Required field',
            }
          }
        },
      },
      {
        dataField: 'templateId',
        text: 'Template ID',
        validator: (newValue) => {
          if (!newValue) {
            return {
              valid: false,
              message: 'Required field',
            }
          }
        },
      },
      {
        dataField: 'active',
        text: 'Active',
        editable: false,
        formatter: (cell, row) => {
          const isActive = row.active
          const status = {
            text: isActive ? 'Active' : 'Inactive',
            color: isActive ? 'success' : 'warning',
            actionText: isActive ? 'Deactivate' : 'Activate',
          };

          return (
            <Dropwdown>
              <Dropdown.Toggle variant={status.color}>
                { status.text }
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Item onClick={ () => handleStatusChange({ id: row.id, statusToSet: !row.active }) }>
                  { status.actionText }
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropwdown>
          );
        },
      },
    ];

    useEffect(() => {
      fetchTemplates();
    }, []);

    return (
      <TemplateTypesList
        templateTypes={ templateTypes }
        templateTableColumns={ templateTableColumns }
      />
    )
}

export const TemplateTypesList = ({ templateTypes, templateTableColumns }) => {
  const updateTemplate = (updatedTemplate) => {
    fetch('/api/entryTemplate/', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        ...updatedTemplate,
      }),
    })
      .catch(error => {
        console.log(error);
        swal('Something went wrong', 'Template update failed.');
      });
  }

  return (
    <Card>
      <Card.Header>
        <Card.Title>
          ELN Entry Templates
          <span className="float-end">
            <Link to="/template-types/new">
              <Button variant="primary">
                New Template
                &nbsp;
                <PlusCircle className="feather align-middle ms-2 mb-1"/>
              </Button>
            </Link>
          </span>
        </Card.Title>
      </Card.Header>

      <Card.Body>
        <ToolkitProvider
          keyField="id"
          data={ templateTypes }
          columns={ templateTableColumns }
        >
          {props => (
            <div>
              <BootstrapTable
                bootstrap4
                keyField="id"
                bordered={false}
                pagination={paginationFactory({
                  sizePerPage: 10,
                  sizePerPageList: [10, 20, 40, 80]
                })}
                defaultSorted={[{
                  dataField: "name",
                  order: "asc"
                }]}
                cellEdit={ cellEditFactory({
                  mode: 'click',
                  afterSaveCell: (oldValue, newValue, row, column) => {
                    updateTemplate(row);
                  }
                }) }
                {...props.baseProps}
              >
              </BootstrapTable>
            </div>
          )}
        </ToolkitProvider>
      </Card.Body>
    </Card>
  );
}

export default TemplateTypesSettings;
