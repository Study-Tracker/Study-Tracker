/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Card, Col, Row} from "react-bootstrap";

const FrontPagePlaceholder = ({isAdmin}) => {
  return (
      <Card className="illustration-light flex-fill">
        <Card.Body>
          <Row className={"d-flex justify-content-center"}>
            <Col lg={12}>
              <div className={"align-self-center"}>
                <h1 className="display-5 illustration-text text-center mb-4">Welcome to Study Tracker!</h1>
                <p className="text-lg">
                  It looks like you are just getting started, so let's get you up to speed.
                  This is the landing page for your Study Tracker instance. Here you will
                  find a summary of your tenant, as well as a timeline of recent activity.
                  The navigation bar on the left of the page will take you to the various
                  areas of the application. The header bar at the top of the page will allow
                  you to access your profile & other settings, or find content with the
                  power search. Why not get started by <a href={"/studies/new"}>creating your first study?</a>
                </p>
                {
                  isAdmin && (
                     <p className="text-lg">
                       As an administrator, you can also access the <a href="/admin">Admin Dashboard</a> to
                       configure your tenant. Before someone can log-in, you will need to
                       &nbsp;<a href="/admin/users">register them as a new user</a>. You will also need to
                       &nbsp;<a href="/programs/new">create a program</a> before users can create new studies.
                     </p>
                  )
                }
              </div>
            </Col>
            <Col sm={8} md={6}>
              <img src="/static/images/clip/campaign-launch.png" alt="Getting started" className="img-fluid"/>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

export default FrontPagePlaceholder;