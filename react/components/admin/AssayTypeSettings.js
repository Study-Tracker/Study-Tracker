import React from 'react';
import {Button, Card, CardBody, CardHeader, CardTitle} from "reactstrap";
import {history} from "../../App";
import {CheckCircle, Edit, PlusCircle, Trash} from "react-feather";
import ToolkitProvider from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";

class AssayTypeSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      assayTypes: []
    }
  }

  componentDidMount() {
    fetch("/api/assaytype")
    .then(response => response.json())
    .then(json => {
      this.setState({
        assayTypes: json
      })
    })
    .catch(error => {
      console.error(error);
      this.setState({
        isError: true,
        error: error
      });
    });

  }

  render() {
    return (
        <React.Fragment>
          <AssayTypeListCard assayTypes={this.state.assayTypes}/>
        </React.Fragment>
    )
  }

}

export default AssayTypeSettings;

const columns = [
  {
    dataField: "name",
    text: "Name",
    sort: true,
    headerStyle: {width: '20%'},
    formatter: (c, d, i, x) => <a href={"/assaytypes/" + d.id}>{d.name}</a>,
    sortFunc: (a, b, order, dataField, rowA, rowB) => {
      if (rowA.name > rowB.name) {
        return order === "desc" ? -1 : 1;
      }
      if (rowB.name > rowA.name) {
        return order === "desc" ? 1 : -1;
      }
      return 0;
    }
  },
  {
    dataField: "description",
    text: "Description",
    sort: false,
    headerStyle: {width: '40%'},
    formatter: (c, d, i, x) => d.description
  },
  {
    dataField: "active",
    text: "Status",
    sort: false,
    headerStyle: {width: '20%'},
    formatter: (c, d, i, x) => {
      return !!d.active
          ? <span className="badge badge-success">Active</span>
          : <span className="badge badge-warning">Inactive</span>
    }
  },
  {
    dataField: "controls",
    text: "Options",
    sort: false,
    headerStyle: {width: '20%'},
    formatter: (c, d, i, x) => {
      return (
          <React.Fragment>
            <a className="text-warning" title={"Edit assay type"}
               onClick={() => history.push("/assaytypes/" + d.id + "/edit")}>
              <Edit className="align-middle mr-1" size={18}/>
            </a>
            {
              !!d.active
                  ? (
                      <a className="text-danger" title={"Disable assay type"}
                         onClick={() => console.log("click")}>
                        <Trash className="align-middle mr-1" size={18}/>
                      </a>
                  )
                  : (
                      <a className="text-info" title={"Enable assay type"}
                         onClick={() => console.log("click")}>
                        <CheckCircle className="align-middle mr-1" size={18}/>
                      </a>
                  )
            }
          </React.Fragment>
      )
    }
  }
];

const AssayTypeListCard = ({assayTypes}) => {

  return (
      <Card>
        <CardHeader>
          <CardTitle tag="h5" className="mb-0">
            Assay Types
            <span className="float-right">
                  <Button
                      color={"primary"}
                      onClick={() => history.push("/assaytypes/new")}
                  >
                    New Assay Type
                    &nbsp;
                    <PlusCircle className="feather align-middle ml-2 mb-1"/>
                  </Button>
                </span>
          </CardTitle>
        </CardHeader>
        <CardBody>
          <ToolkitProvider
              keyField="id"
              data={assayTypes}
              columns={columns}
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
                      {...props.baseProps}
                  >
                  </BootstrapTable>
                </div>
            )}
          </ToolkitProvider>
        </CardBody>
      </Card>
  )
}