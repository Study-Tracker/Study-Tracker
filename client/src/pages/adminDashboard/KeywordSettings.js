import React, {useEffect, useState} from "react";
import {Button, Card, Col, Dropdown, Form, Modal, Row} from 'react-bootstrap';
import {Tag} from 'react-feather';
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import Select from 'react-select';
import swal from "sweetalert";
import {SettingsLoadingMessage} from "../../common/loading";
import {SettingsErrorMessage} from "../../common/errors";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";
import axios from "axios";

const emptyKeyword = {
  id: null,
  category: {
    id: null,
    name: null
  },
  keyword: ''
};

const KeywordSettings = props => {

  const [state, setState] = useState({
    isModalOpen: false,
    keywords: [],
    categories: [],
    isLoaded: false,
    isError: false,
    selectedKeyword: emptyKeyword,
    categoryInput: "select"
  });

  const showModal = (keyword) => {
    if (keyword) {
      setState(prevState => ({
        ...prevState,
        selectedKeyword: Object.prototype.hasOwnProperty.call(keyword, "keyword") ? keyword : emptyKeyword,
        isModalOpen: true,
        categoryInput: "select"
      }))
    } else {
      setState(prevState => ({
        ...prevState,
        isModalOpen: false
      }));
    }
  };

  const toggleCategoryInput = (e) => {
    console.debug(e.target.value);
    if (e.target.checked) {
      setState(prevState => ({
        ...prevState,
        categoryInput: e.target.value
      }));
    }
  };

  useEffect(() => {
    axios.get("/api/keyword-category")
    .then(response => {
      setState(prevState => ({
        ...prevState,
        categories: response.data,
        isLoaded: true
      }));
    })
    .catch(e => {
      console.error(e);
      setState(prevState => ({
        ...prevState,
        isError: true,
        error: e
      }));
    });
  }, []);

  const handleCategorySelect = (categoryId) => {
    axios.get("/api/keyword?categoryId=" + categoryId)
    .then(response => {
      const category = state.categories.find(c => c.id === categoryId);
      setState(prevState => ({
        ...prevState,
        selectedCategory: category,
        keywords: response.data
      }));
    })
  };

  const handleInputUpdate = (input) => {
    const keyword = state.selectedKeyword;
    setState(prevState => ({
      ...prevState,
      selectedKeyword: {
        ...keyword,
        ...input
      }
    }));
  };

  const handleKeywordSubmit = () => {

    let keyword = state.selectedKeyword;
    const url = "/api/keyword/" + (keyword.id || "");
    const method = !!keyword.id ? "put" : "post";
    console.log(keyword);
    console.log(url);

    axios({
      url: url,
      method: method,
      data: keyword
    })
    .then(response => {
      swal("Keyword saved",
          "Refresh the keywords table to view updated records. You must refresh the page before new categories will show up.",
          "success")
      .then(() => {
        showModal();
      })
    })
    .catch(e => {
      console.error(e);
      swal("Request failed",
          "Failed to save the keyword record. Try again or check to make sure this keyword has not already been registered.",
          "warning");
    });

  }

  const categoryOptions = state.categories
  .sort((a, b) => {
    if (a.name > b.name) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else {
      return 0;
    }
  })
  .map(c => {
    return {
      value: c.id,
      label: c.name
    }
  });

  let content = <SettingsLoadingMessage/>
  if (state.isLoaded) {
    content = <KeywordsTable
        keywords={state.keywords}
        categoryOptions={categoryOptions}
        handleCategorySelect={handleCategorySelect}
        selectedCategory={state.selectedCategory}
        showModal={showModal}
    />
  } else if (!!state.isError) {
    content = <SettingsErrorMessage/>;
  }

  return (
      <Card>
        <Card.Header>
          <Card.Title tag={"h5"} className={"mb-0"}>
            Keywords
            <span className="float-end">
              <Button color={"primary"}
                      onClick={() => showModal(true)}>
                New Keyword
                &nbsp;
                <Tag className="feather align-middle ms-2 mb-1"/>
              </Button>
            </span>
          </Card.Title>
        </Card.Header>
        <Card.Body>

          {content}

          <Modal
              show={state.isModalOpen}
              onHide={() => showModal()}
              size={"lg"}
          >
            <Modal.Header closeButton>
              {
                !!state.selectedKeyword && state.selectedKeyword.id
                    ? "Edit Keyword" : "New Keyword"
              }
            </Modal.Header>
            <Modal.Body className="m-3">
              {
                !!state.isModalOpen
                    ? <ModalInputs
                        categories={categoryOptions}
                        keyword={state.selectedKeyword}
                        handleUpdate={handleInputUpdate}
                        categoryInput={state.categoryInput}
                        toggleCategoryInput={toggleCategoryInput}
                    />
                    : ""
              }
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => showModal()}>
                Cancel
              </Button>
              <Button
                  variant="primary"
                  onClick={handleKeywordSubmit}
                  disabled={!(!!state.selectedKeyword.keyword
                      && !!state.selectedKeyword.category)}
              >
                Submit
              </Button>
            </Modal.Footer>
          </Modal>

        </Card.Body>
      </Card>
  );

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
      text: "",
      sort: false,
      formatter: (c, d, i, x) => {
        return (
            <React.Fragment>
              <Dropdown>
                <Dropdown.Toggle variant={"outline-primary"}>
                  {/*<FontAwesomeIcon icon={faBars} />*/}
                  &nbsp;Options&nbsp;
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => showModal(d)}>
                    <FontAwesomeIcon icon={faEdit}/>
                    &nbsp;&nbsp;
                    Edit keyword
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
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
                  label: keyword.category.name,
                  value: keyword.category.id
                }}
                onChange={(selected) => handleUpdate(
                    {category: {"id": selected.value, "name": selected.label}})}
            />
          </Form.Group>
          <Form.Group hidden={categoryInput !== "input"}>
            <Form.Label>Category</Form.Label>
            <Form.Control
                type={"text"}
                onChange={(e) => handleUpdate({category: {"name": e.target.value}})}
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

ModalInputs.propTypes = {
  keyword: PropTypes.object.isRequired
}

export default KeywordSettings;