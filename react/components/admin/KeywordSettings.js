import React from 'react';
import {Button, Card, Col, Form, Modal, Row} from 'react-bootstrap';
import {Edit, Tag} from 'react-feather';
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import Select from 'react-select';
import swal from "sweetalert";
import {SettingsLoadingMessage} from "../loading";
import {SettingsErrorMessage} from "../errors";

const emptyKeyword = {
  id: null,
  category: null,
  keyword: ''
};

export default class KeywordSettings extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      isModalOpen: false,
      keywords: [],
      categories: [],
      isLoaded: false,
      isError: false,
      selectedKeyword: emptyKeyword,
      categoryInput: "select"
    };
    this.showModal = this.showModal.bind(this);
    this.handleCategorySelect = this.handleCategorySelect.bind(this);
    this.handleInputUpdate = this.handleInputUpdate.bind(this);
    this.handleKeywordSubmit = this.handleKeywordSubmit.bind(this);
    this.toggleCategoryInput = this.toggleCategoryInput.bind(this);
  }

  showModal(keyword) {
    if (!!keyword) {
      this.setState({
        selectedKeyword: keyword || emptyKeyword,
        isModalOpen: true,
        categoryInput: "select"
      })
    } else {
      this.setState({
        isModalOpen: false
      })
    }
  }

  toggleCategoryInput(e) {
    console.debug(e.target.value);
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
        this.showModal();
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

    let content = <SettingsLoadingMessage />
    if (this.state.isLoaded) {
      content = <KeywordsTable
          keywords={this.state.keywords}
          categoryOptions={categoryOptions}
          handleCategorySelect={this.handleCategorySelect}
          selectedCategory={this.state.selectedCategory}
          showModal={this.showModal}
      />
    } else if (!!this.state.isError) {
      content = <SettingsErrorMessage />;
    }

    return (
        <Card>
          <Card.Header>
            <Card.Title tag={"h5"} className={"mb-0"}>
              Keywords
              <span className="float-end">
                <Button color={"primary"}
                        onClick={() => this.showModal(true)}>
                  New Keyword
                  &nbsp;
                  <Tag className="feather align-middle ms-2 mb-1"/>
                </Button>
              </span>
            </Card.Title>
          </Card.Header>
          <Card.Body>

            { content }

            <Modal
                show={this.state.isModalOpen}
                onHide={() => this.showModal()}
                size={"lg"}
            >
              <Modal.Header closeButton>
                {
                  !!this.state.selectedKeyword && this.state.selectedKeyword.id
                      ? "Edit Keyword" : "New Keyword"
                }
              </Modal.Header>
              <Modal.Body className="m-3">
                {
                  !!this.state.isModalOpen
                      ? <ModalInputs
                          categories={categoryOptions}
                          keyword={this.state.selectedKeyword}
                          handleUpdate={this.handleInputUpdate}
                          categoryInput={this.state.categoryInput}
                          toggleCategoryInput={this.toggleCategoryInput}
                      />
                      : ""
                }
              </Modal.Body>
              <Modal.Footer>
                <Button variant="secondary" onClick={() => this.showModal()}>
                  Cancel
                </Button>
                <Button
                    variant="primary"
                    onClick={this.handleKeywordSubmit}
                    disabled={!(!!this.state.selectedKeyword.keyword
                        && !!this.state.selectedKeyword.category)}
                >
                  Submit
                </Button>
              </Modal.Footer>
            </Modal>

          </Card.Body>
        </Card>
    )
  }

}

const KeywordsTable = ({
  keywords,
  categoryOptions,
  handleCategorySelect,
  selectedCategory,
  showModal
}) => {

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
                 onClick={() => showModal(d)}>
                <Edit className="align-middle me-1" size={18}/>
              </a>
            </React.Fragment>
        )
      }
    }
  ];

  return (
      <React.Fragment>
        <Row>
          <Col xs={12} sm={6}>
            <Form.Group>
              <Form.Label>Select a keyword category</Form.Label>
              <Select
                  className="react-select-container"
                  classNamePrefix="react-select"
                  options={categoryOptions}
                  onChange={(selected) => handleCategorySelect(
                      selected.value)}
              />
            </Form.Group>
          </Col>
        </Row>

        <Row>
          <Col xs={12}>
            {
              !!selectedCategory
                  ? (
                      <ToolkitProvider
                          keyField="id"
                          data={keywords}
                          columns={columns}
                          search
                          exportCSV
                      >
                        {props => (
                            <div>
                              <div className="float-end">
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
      </React.Fragment>
  )
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
          <Form.Group className="mb-2">
            <Form.Check
                label={"Use existing category"}
                type={"radio"}
                name={"category-radio"}
                value={"select"}
                checked={categoryInput === "select"}
                onChange={toggleCategoryInput}
            />
          </Form.Group>
          <Form.Group className="mb-2">
            <Form.Check
                label={"Create new category"}
                type={"radio"}
                name={"category-radio"}
                value={"input"}
                checked={categoryInput === "input"}
                onChange={toggleCategoryInput}
            />
          </Form.Group>
        </Col>

        <Col xs={12} sm={4}>
          <Form.Group hidden={categoryInput !== "select"}>
            <Form.Label>Category</Form.Label>
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
          </Form.Group>
          <Form.Group hidden={categoryInput !== "input"}>
            <Form.Label>Category</Form.Label>
            <Form.Control
                type={"text"}
                onChange={(e) => handleUpdate({category: e.target.value})}
            />
          </Form.Group>
        </Col>

        <Col xs={12} sm={4}>
          <Form.Group>
            <Form.Label>Keyword</Form.Label>
            <Form.Control
                type={"text"}
                defaultValue={keyword.keyword}
                onChange={(e) => handleUpdate({keyword: e.target.value})}
            />
          </Form.Group>
        </Col>

      </Row>
  )
};