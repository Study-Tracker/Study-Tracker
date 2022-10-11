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

const AdminDashboardPlaceholder = () => {
  return (
      <Card className="illustration-light flex-fill">
        <Card.Body>
          <Row>
            <Col sm={6} className={"d-flex"}>
              <div className={"align-self-center"}>
                <h1 className="display-5 illustration-text text-center mb-4">Admin Dashboard</h1>
                <p className="text-lg">
                  Use these tools to customize your Study Tracker experience.
                  Manage users, configure integrations, and tailor your tenant
                  to fit your team's needs.
                </p>
              </div>
            </Col>
            <Col sm={6}>
              <img src="/static/images/clip/settings.png" alt="File Sync" className="img-fluid"/>
            </Col>
          </Row>
        </Card.Body>
      </Card>
  )
}

export default AdminDashboardPlaceholder;