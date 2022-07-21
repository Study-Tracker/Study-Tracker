import {Alert, Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import React, {useState} from "react";

const SignInForm = ({ssoOptions, isError, message}) => {

  const [auth, setAuth] = useState({});

  const handleInputChange = (data) => {
    setAuth({
      ...auth,
      ...data
    });
  }

  return (
      <Container fluid className="animated fadeIn">
        <Row className="justify-content-center">
          <Col xs={12} sm={10} md={8} lg={6} xl={4}>

            <div className="text-center mt-4">
              <h2>Welcome to Study Tracker</h2>
              <p className="lead">Please sign-in to continue</p>
            </div>

            <Card>
              <Card.Body>
                <div className="m-sm-4">

                  <div className="text-center mb-4">
                    <img
                        src={"/static/images/circle-logo-light-blue-gradient.png"}
                        className="img-fluid"
                        width="130"
                        height="130"
                        alt="Study Tracker"
                    />
                  </div>

                  <Form action={"/auth/login"} method={"post"}>

                    <Form.Group>
                      <Form.Label>Username or email</Form.Label>
                      <Form.Control
                          size="lg"
                          type="text"
                          name="username"
                          placeholder="Enter your username or email address"
                          onChange={e => handleInputChange(
                              {username: e.target.value})}
                      />
                    </Form.Group>

                    <Form.Group>
                      <Form.Label>Password</Form.Label>
                      <Form.Control
                          size="lg"
                          type="password"
                          name="password"
                          placeholder="Enter your password"
                          onChange={e => handleInputChange(
                              {password: e.target.value})}
                      />
                    </Form.Group>

                    <div className="text-center mt-3">
                      <small>
                        <a href="/auth/passwordresetrequest">
                          Forgot password?
                        </a>
                      </small>
                    </div>

                    {
                      !!message
                          ? (
                              <div className="text-center mt-3">
                                <Alert variant="success" className="p-3">
                                  {message}
                                </Alert>
                              </div>
                          )
                          : ''
                    }

                    {
                      isError
                          ? (
                              <div className="text-center mt-3">
                                <Alert variant="danger" className="p-3">
                                  Failed to sign you in. Please check your
                                  credentials and try again.
                                </Alert>
                              </div>
                          )
                          : ''
                    }

                    <div className="text-center mt-3">
                      <Button
                          size="lg"
                          variant="primary"
                          type="submit"
                          style={{paddingLeft: 50, paddingRight: 50}}
                      >
                        Sign In
                      </Button>
                    </div>

                    {
                      ssoOptions.isSuccess
                      && ssoOptions.data.sso
                      && ssoOptions.data.sso.okta
                          ? (
                              <div className="text-center mt-3">
                                <Button
                                    href={ssoOptions.data.sso.okta}
                                    size={"lg"}
                                    variant="outline-primary"
                                >
                                  Sign in with Okta
                                </Button>
                              </div>
                          ) : ''
                    }

                  </Form>
                </div>
              </Card.Body>
            </Card>

          </Col>
        </Row>
      </Container>
  )

}

export default SignInForm;