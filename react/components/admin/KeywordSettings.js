import React from 'react';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  CardTitle,
  Col,
  FormGroup,
  Input,
  Label,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row
} from 'reactstrap';
import {Edit, Tag} from 'react-feather';
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import Select from 'react-select';
import swal from "sweetalert";

const emptyKeyword = {
  id: null,
  category: null,
  keyword: ''
};

export default class KeywordSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      showModal: false,
      keywords: [],
      categories: [],
      isLoaded: false,
      isError: false,
      selectedKeyword: emptyKeyword,
      categoryInput: "select"
    };
    this.toggleModal = this.toggleModal.bind(this);
    this.handleCategorySelect = this.handleCategorySelect.bind(this);
    this.handleInputUpdate = this.handleInputUpdate.bind(this);
    this.handleKeywordSubmit = this.handleKeywordSubmit.bind(this);
    this.toggleCategoryInput = this.toggleCategoryInput.bind(this);
  }

  toggleModal(keyword) {
    this.setState({
      selectedKeyword: keyword || emptyKeyword,
      showModal: !this.state.showModal,
      categoryInput: "select"
    })
  }

  toggleCategoryInput(e) {
    console.log(e.target.value);
    if (e.target.checked) {
      this.setState({
        categoryInput: e.target.value
      })
    }
  }

  componentDidMount() {
    fetch("/api/keyword/categories")
    .then(response => response.json())
    .then(json => {
      this.setState({
        categories: json,
        isLoaded: true
      })
    })
    .catch(e => {
      console.error(e);
      this.setState({
        isError: true,
        error: e
      })
    });
  }

  handleCategorySelect(category) {
    fetch("/api/keyword?category=" + category)
    .then(response => response.json())
    .then(keywords => {
      this.setState({
        selectedCategory: category,
        keywords: keywords
      })
    })
  }

  handleInputUpdate(input) {
    const keyword = this.state.selectedKeyword;
    this.setState({
      selectedKeyword: {
        ...keyword,
        ...input
      }
    })
  }

  handleKeywordSubmit() {

    let keyword = this.state.selectedKeyword;
    const url = "/api/keyword/" + (keyword.id || "");
    const method = !!keyword.id ? "PUT" : "POST";
    console.log(keyword);
    console.log(url);

    fetch(url, {
      method: method,
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(keyword)
    })
    .then(response => response.json())
    .then(json => {
      swal("Keyword saved",
          "Refresh the keywords table to view updated records. You must refresh the page before new categories will show up.",
          "success")
      .then(() => {
        this.toggleModal();
      })
    })
    .catch(e => {
      console.error(e);
      swal("Request failed",
          "Failed to save the keyword record. Try again or check to make sure this keyword has not already been registered.",
          "warning");
    });

  }

  render() {

    const columns = [
      {
        dataField: "keyword",
        text: "Keyword",
        sort: true,
        formatter: (c, d, i, x) => d.keyword,
        sortFunc: (a, b, order, dataField, rowA, rowB) => {
          if (rowA.keyword > rowB.keyword) {
            return order === "desc" ? -1 : 1;
          }
          if (rowB.keyword > rowA.keyword) {
            return order === "desc" ? 1 : -1;
          }
          return 0;
        },
      },
      {
        dataField: "controls",
        text: "Options",
        sort: false,
        formatter: (c, d, i, x) => {
          return (
              <React.Fragment>
                <a className="text-warning" title={"Edit keyword"}
                   onClick={() => this.toggleModal(d)}>
                  <Edit className="align-middle mr-1" size={18}/>
                </a>
              </React.Fragment>
          )
        }
      }
    ];

    const categoryOptions = this.state.categories
    .sort((a, b) => {
      if (a > b) {
        return 1;
      } else if (a < b) {
        return -1;
      } else {
        return 0;
      }
    })
    .map(c => {
      return {
        value: c,
        label: c
      }
    });

    return (
        <Card>
          <CardHeader>
            <CardTitle tag={"h5"} className={"mb-0"}>
              Keywords
              <span className="float-right">
                <Button color={"primary"}
                        onClick={() => this.toggleModal(true)}>
                  New Keyword
                  &nbsp;
                  <Tag className="feather align-middle ml-2 mb-1"/>
                </Button>
              </span>
            </CardTitle>
          </CardHeader>
          <CardBody>

            <Row>
              <Col xs={12} sm={6}>
                <FormGroup>
                  <Label>Select a keyword category</Label>
                  <Select
                      className="react-select-container"
                      classNamePrefix="react-select"
                      options={categoryOptions}
                      onChange={(selected) => this.handleCategorySelect(
                          selected.value)}
                  />
                </FormGroup>
              </Col>
            </Row>

            <Row>
              <Col xs={12}>
                {
                  !!this.state.selectedCategory
                      ? (
                          <ToolkitProvider
                              keyField="id"
                              data={this.state.keywords}
                              columns={columns}
                              search
                              exportCSV
                          >
                            {props => (
                                <div>
                                  <div className="float-right">
                                    <Search.SearchBar
                                        {...props.searchProps}
                                    />
                                  </div>
                                  <BootstrapTable
                                      bootstrap4
                                      keyField="id"
                                      bordered={false}
                                      pagination={paginationFactory({
                                        sizePerPage: 10,
                                        sizePerPageList: [10, 20, 40, 80]
                                      })}
                                      defaultSorted={[{
                                        dataField: "keyword",
                                        order: "asc"
                                      }]}
                                      {...props.baseProps}
                                  >
                                  </BootstrapTable>
                                </div>
                            )}
                          </ToolkitProvider>
                      ) : (
                          ""
                      )
                }
              </Col>
            </Row>

            <Modal
                isOpen={this.state.showModal}
                toggle={() => this.toggleModal()}
                size={"lg"}
            >
              <ModalHeader toggle={() => this.toggleModal()}>
                {
                  !!this.state.selectedKeyword && this.state.selectedKeyword.id
                      ? "Edit Keyword" : "New Keyword"
                }
              </ModalHeader>
              <ModalBody>
                {
                  !!this.state.showModal
                      ? <ModalInputs
                          categories={categoryOptions}
                          keyword={this.state.selectedKeyword}
                          handleUpdate={this.handleInputUpdate}
                          categoryInput={this.state.categoryInput}
                          toggleCategoryInput={this.toggleCategoryInput}
                      />
                      : ""
                }
              </ModalBody>
              <ModalFooter>
                <Button color="secondary" onClick={() => this.toggleModal()}>
                  Cancel
                </Button>
                <Button
                    color="primary"
                    onClick={this.handleKeywordSubmit}
                    disabled={!(!!this.state.selectedKeyword.keyword
                        && !!this.state.selectedKeyword.category)}
                >
                  Submit
                </Button>
              </ModalFooter>
            </Modal>

          </CardBody>
        </Card>
    )
  }

}

const ModalInputs = ({
  keyword,
  handleUpdate,
  categories,
  categoryInput,
  toggleCategoryInput
}) => {
  return (
      <Row>

        <Col xs={12} sm={4}>
          <FormGroup check className="mb-2">
            <Label check>
              <Input
                  type={"radio"}
                  name={"category-radio"}
                  value={"select"}
                  checked={categoryInput === "select"}
                  onChange={toggleCategoryInput}
              />
              {" "}Use existing category
            </Label>
          </FormGroup>
          <FormGroup check className="mb-2">
            <Label check>
              <Input
                  type={"radio"}
                  name={"category-radio"}
                  value={"input"}
                  checked={categoryInput === "input"}
                  onChange={toggleCategoryInput}
              />
              {" "}Create new category
            </Label>
          </FormGroup>
        </Col>

        <Col xs={12} sm={4}>
          <FormGroup style={{
            display: categoryInput === "select" ? "block" : "none"
          }}>
            <Label>Category</Label>
            <Select
                className="react-select-container"
                classNamePrefix="react-select"
                options={categories}
                defaultValue={{
                  label: keyword.category,
                  value: keyword.category
                }}
                onChange={(selected) => handleUpdate(
                    {category: selected.value})}
            />
          </FormGroup>
          <FormGroup style={{
            display: categoryInput === "input" ? "block" : "none"
          }}>
            <Label>Category</Label>
            <Input
                onChange={(e) => handleUpdate({category: e.target.value})}
            />
          </FormGroup>
        </Col>

        <Col xs={12} sm={4}>
          <FormGroup>
            <Label>Keyword</Label>
            <Input
                defaultValue={keyword.keyword}
                onChange={(e) => handleUpdate({keyword: e.target.value})}
            />
          </FormGroup>
        </Col>

      </Row>
  )
};