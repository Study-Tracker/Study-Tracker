/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {Media, Table} from 'reactstrap';
import {
  Bell,
  CheckSquare,
  Edit,
  ExternalLink,
  File,
  FilePlus,
  FileText,
  Link,
  MessageCircle,
  Star,
  Trash2,
} from 'react-feather';
import {studyActions} from "../config/activityConstants";
import {StatusBadge} from "./status";
import {KeywordBadgeList} from "./keywords";
import {AssayTaskCard} from "./assayTasks";
import {relationshipTypes} from "../config/studyRelationshipConstants";

const dateFormat = require('dateformat');

const createMarkup = (content) => {
  return {__html: content};
};

const ActivityIcon = ({action}) => {
  switch (action) {
    case studyActions.NEW_ENTRY_TEMPLATE.value:
      return <Star size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.UPDATED_ENTRY_TEMPLATE.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.NEW_STUDY.value:
      return <Star size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.UPDATED_STUDY.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_STUDY.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.STUDY_STATUS_CHANGED.value:
      return <Bell size={36} className="align-middle text-info mr-4"/>;

    case studyActions.NEW_ASSAY.value:
      return <Star size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.UPDATED_ASSAY.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_ASSAY.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.ASSAY_STATUS_CHANGED.value:
      return <Bell size={36} className="align-middle text-info mr-4"/>;

    case studyActions.NEW_PROGRAM.value:
      return <Star size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.UPDATED_PROGRAM.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_PROGRAM.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.FILE_UPLOADED.value:
      return <FilePlus size={36} className="align-middle text-primary mr-4"/>;

    case studyActions.NEW_STUDY_CONCLUSIONS.value:
      return <FileText size={36} className="align-middle text-primary mr-4"/>;

    case studyActions.EDITED_STUDY_CONCLUSIONS.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_STUDY_CONCLUSIONS.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.NEW_COMMENT.value:
      return <MessageCircle size={36} className="align-middle text-info mr-4"/>;

    case studyActions.EDITED_COMMENT.value:
      return <MessageCircle size={36} className="align-middle text-info mr-4"/>;

    case studyActions.DELETED_COMMENT.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.NEW_STUDY_RELATIONSHIP.value:
      return <Link size={36} className="align-middle text-primary mr-4"/>;

    case studyActions.UPDATED_STUDY_RELATIONSHIP.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_STUDY_RELATIONSHIP.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.NEW_STUDY_EXTERNAL_LINK.value:
      return <ExternalLink size={36}
                           className="align-middle text-primary mr-4"/>;

    case studyActions.UPDATED_STUDY_EXTERNAL_LINK.value:
      return <Edit size={36} className="align-middle text-warning mr-4"/>;

    case studyActions.DELETED_STUDY_EXTERNAL_LINK.value:
      return <Trash2 size={36} className="align-middle text-danger mr-4"/>;

    case studyActions.ASSAY_TASK_ADDED.value:
      return <CheckSquare size={36} className="align-middle text-info mr-4"/>;

    case studyActions.ASSAY_TASK_UPDATED.value:
      return <CheckSquare size={36} className="align-middle text-info mr-4"/>;

    default:
      return <Bell size={36} className="align-middle text-info mr-4"/>;

  }
};

const ActivityMessage = ({activity}) => {
  switch (activity.eventType) {
    case studyActions.NEW_ENTRY_TEMPLATE.value:
      return (
        <>
          <p>
            <a href={"/user/" + activity.user.username}>
              {activity.user.displayName}
            </a>
            &nbsp;has created a new template:
          </p>
          <div className="bg-light text-secondary p-3">
            <h5>TemplateId: { activity.data.entryTemplate.templateId }</h5>
            <h3>Name: { activity.data.entryTemplate.name }</h3>
            <p>Active: { String(activity.data.entryTemplate.active) }</p>
          </div>
        </>
      );

    case studyActions.UPDATED_ENTRY_TEMPLATE.value:
      return (
        <>
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has edited a template:
          </p>
          <div className="bg-light text-secondary p-3">
            <h5>TemplateId: { activity.data.entryTemplate.templateId }</h5>
            <h3>Name: { activity.data.entryTemplate.name }</h3>
            <p>Active: { String(activity.data.entryTemplate.active) }</p>
          </div>
        </>
      );

    case studyActions.NEW_STUDY.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has created a new study:
            </p>
            <div className="bg-light text-secondary p-3">
              <h5><a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a></h5>
              <h3>{activity.data.study.name}</h3>
              <h5 className="text-muted">{activity.data.study.program}</h5>
              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.study.description)}/>
              <p>
                <KeywordBadgeList
                    keywords={activity.data.study.keywords || []}/>
              </p>
            </div>
          </React.Fragment>
      );

    case studyActions.UPDATED_STUDY.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has made an edit to a study:
            </p>
            <div className="bg-light text-secondary p-3">
              <h5><a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a></h5>
              <h3>{activity.data.study.name}</h3>
              <h5 className="text-muted">{activity.data.study.program}</h5>
              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.study.description)}/>
              <p>
                <KeywordBadgeList keywords={activity.data.study.keywords}/>
              </p>
            </div>
          </React.Fragment>
      );

    case studyActions.DELETED_STUDY.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed study: {activity.data.study.code}
          </p>
      );

    case studyActions.STUDY_STATUS_CHANGED.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has updated the status of study&nbsp;
            <a href={"/study/"
            + activity.data.study.code}>{activity.data.study.code}</a>
            &nbsp;from&nbsp;
            <StatusBadge status={activity.data.oldStatus}/>
            &nbsp;to&nbsp;
            <StatusBadge status={activity.data.newStatus}/>
          </p>
      );

    case studyActions.NEW_ASSAY.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has created a new assay:
            </p>

            <div className="bg-light text-secondary p-3">

              <h5>
                <a href={"/study/" + activity.data.assay.study + "/assay/"
                + activity.data.assay.code}>
                  {activity.data.assay.code}
                </a>
              </h5>

              <h3>{activity.data.assay.name}</h3>

              <h5 className="text-muted">
                {activity.data.assay.assayType.name}
              </h5>

              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.assay.description)}/>

            </div>
          </React.Fragment>
      );

    case studyActions.UPDATED_ASSAY.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has made an edit to an assay:
            </p>

            <div className="bg-light text-secondary p-3">

              <h5>
                <a href={"/study/" + activity.data.assay.study + "/assay/"
                + activity.data.assay.code}>
                  {activity.data.assay.code}
                </a>
              </h5>

              <h3>{activity.data.assay.name}</h3>

              <h5 className="text-muted">
                {activity.data.assay.assayType.name}
              </h5>

              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.assay.description)}/>

            </div>
          </React.Fragment>
      );

    case studyActions.DELETED_ASSAY.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed assay: {activity.data.assay.code}
          </p>
      );

    case studyActions.ASSAY_STATUS_CHANGED.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has updated the status of assay&nbsp;
            <a href={"/study/" + activity.data.study + "/assay/"
            + activity.data.assay.code}>{activity.data.assay.code}</a>
            &nbsp;from&nbsp;
            <StatusBadge status={activity.data.oldStatus}/>
            &nbsp;to&nbsp;
            <StatusBadge status={activity.data.newStatus}/>
          </p>
      );

    case studyActions.ASSAY_TASK_ADDED.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has added a new task to assay&nbsp;
              <a href={"/study/" + activity.data.assay.study + "/assay/"
              + activity.data.assay.code}>
                {activity.data.assay.code}
              </a>:
            </p>

            <AssayTaskCard task={activity.data.task}/>

          </React.Fragment>
      );

    case studyActions.ASSAY_TASK_UPDATED.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has updated a task in assay&nbsp;
              <a href={"/study/" + activity.data.assay.study + "/assay/"
              + activity.data.assay.code}>
                {activity.data.assay.code}
              </a>:
            </p>

            <AssayTaskCard task={activity.data.task}/>

          </React.Fragment>
      );

    case studyActions.NEW_PROGRAM.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has created a new assay:
            </p>

            <div className="bg-light text-secondary p-3">

              <h3>
                <a href={"/program/" + activity.data.program.id}>
                  {activity.data.program.name}
                </a>
              </h3>

              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.program.description)}/>

            </div>

          </React.Fragment>
      );

    case studyActions.UPDATED_PROGRAM.value:
      return (
          <React.Fragment>

            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has made an edit to a program:
            </p>

            <div className="bg-light text-secondary p-3">

              <h3>
                <a href={"/program/" + activity.data.program.id}>
                  {activity.data.program.name}
                </a>
              </h3>

              <div dangerouslySetInnerHTML={createMarkup(
                  activity.data.program.description)}/>

            </div>

          </React.Fragment>
      );

    case studyActions.DELETED_PROGRAM.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed program: {activity.data.assay.code}
          </p>
      );

    case studyActions.FILE_UPLOADED.value:
      if (activity.reference === "ASSAY") {
        return (
            <React.Fragment>
              <p>
                <a href={"/user/"
                + activity.user.username}>{activity.user.displayName}</a>
                &nbsp;has attached a new file to assay:&nbsp;
                <a href={"/study/"
                + activity.data.assay.study}>{activity.data.assay.study
                + "/assay/" + activity.data.assay.code}
                </a>
              </p>
              <div className="bg-light text-secondary p-3">
                <h3>
                  <a href={activity.data.file.url} target="_blank">
                    <File size={24}/>
                    &nbsp;
                    {activity.data.file.name}
                  </a>
                </h3>
              </div>
            </React.Fragment>

        );
      } else {
        return (
            <React.Fragment>
              <p>
                <a href={"/user/"
                + activity.user.username}>{activity.user.displayName}</a>
                &nbsp;has attached a new file to study:&nbsp;
                <a href={"/study/"
                + activity.data.study.code}>{activity.data.study.code}</a>
              </p>
              <div className="bg-light text-secondary p-3">
                <h3>
                  <a href={activity.data.file.url} target="_blank">
                    <File size={24}/>
                    &nbsp;
                    {activity.data.file.name}
                  </a>
                </h3>
              </div>
            </React.Fragment>

        );
      }
    case studyActions.NEW_STUDY_CONCLUSIONS.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has added new conclusions for study:&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>
            </p>
            <div className="bg-light font-italic text-secondary p-3"
                 dangerouslySetInnerHTML={createMarkup(
                     activity.data.conclusions.content)}/>
          </React.Fragment>
      );
    case studyActions.EDITED_STUDY_CONCLUSIONS.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has updated the conclusions for study:&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>
            </p>
            <div className="bg-light font-italic text-secondary p-3"
                 dangerouslySetInnerHTML={createMarkup(
                     activity.data.conclusions.content)}/>
          </React.Fragment>

      );
    case studyActions.DELETED_STUDY_CONCLUSIONS.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed the conclusions for study:&nbsp;
            <a href={"/study/"
            + activity.data.study.code}>{activity.data.study.code}</a>
          </p>
      );
    case studyActions.NEW_COMMENT.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has posted a new comment to study&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>:
            </p>
            <p className="bg-light font-italic text-secondary p-3">
              "{activity.data.comment.text}"
            </p>
          </React.Fragment>

      );
    case studyActions.EDITED_COMMENT.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has edited their comment to study:&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>
            </p>
            <p className="bg-light font-italic text-secondary p-3">
              "{activity.data.comment.text}"
            </p>
          </React.Fragment>
      );
    case studyActions.DELETED_COMMENT.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed their comment to study:&nbsp;
            <a href={"/study/"
            + activity.data.study.code}>{activity.data.study.code}</a>
          </p>
      );
    case studyActions.NEW_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has added a new study relationship:&nbsp;
            <a href={"/study/"
            + activity.data.sourceStudy.code}>{activity.data.sourceStudy.code}</a>
            &nbsp;{relationshipTypes[activity.data.relationship.type].label}&nbsp;
            <a href={"/study/"
            + activity.data.targetStudy.code}>{activity.data.targetStudy.code}</a>
          </p>
      );
    case studyActions.UPDATED_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has added a new study relationship:&nbsp;
            <a href={"/study/"
            + activity.data.sourceStudy.code}>{activity.data.sourceStudy.code}</a>
            &nbsp;{relationshipTypes[activity.data.relationship.type].label}&nbsp;
            <a href={"/study/"
            + activity.data.targetStudy.code}>{activity.data.targetStudy.code}</a>
          </p>
      );
    case studyActions.DELETED_STUDY_RELATIONSHIP.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed a study relationship for study:&nbsp;
            <a href={"/study/"
            + activity.data.study.code}>{activity.data.study.code}</a>
          </p>
      );
    case studyActions.NEW_STUDY_EXTERNAL_LINK.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has added a new external link for study:&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>
            </p>
            <p className="bg-light text-secondary p-3">
              <Link size={16}/>
              &nbsp;
              <a href={activity.data.link.url}
                 target="_blank">{activity.data.link.label}</a>
            </p>
          </React.Fragment>
      );
    case studyActions.UPDATED_STUDY_EXTERNAL_LINK.value:
      return (
          <React.Fragment>
            <p>
              <a href={"/user/"
              + activity.user.username}>{activity.user.displayName}</a>
              &nbsp;has edited an external link for study:&nbsp;
              <a href={"/study/"
              + activity.data.study.code}>{activity.data.study.code}</a>
            </p>
            <p className="bg-light text-secondary p-3">
              <Link size={16}/>
              &nbsp;
              <a href={activity.data.link.url}
                 target="_blank">{activity.data.link.label}</a>
            </p>
          </React.Fragment>
      );
    case studyActions.DELETED_STUDY_EXTERNAL_LINK.value:
      return (
          <p>
            <a href={"/user/"
            + activity.user.username}>{activity.user.displayName}</a>
            &nbsp;has removed an external link for study:&nbsp;
            <a href={"/study/"
            + activity.data.study.code}>{activity.data.study.code}</a>
          </p>
      );
    default:
      return (
          <p>
            {activity.user.displayName}
          </p>
      );
  }
};

export const StudyTimelineActivity = ({activity}) => {
  return (
      <Media>

        <ActivityIcon action={activity.eventType}/>

        <Media body>

          <small className="float-right text-navy">
            {dateFormat(new Date(activity.date), 'mm/dd/yy @ h:MM TT')}
          </small>

          <p className="mb-2">
            <strong>
              {
                studyActions.hasOwnProperty(activity.eventType)
                    ? studyActions[activity.eventType].label
                    : "New Activity"
              }
            </strong>
          </p>

          <ActivityMessage activity={activity}/>

        </Media>
      </Media>
  )
};

export const Timeline = ({activities}) => {
  console.log(activities);
  if (!activities || activities.length === 0) {
    return <h5 className="text-muted">There is no activity to display</h5>;
  }
  let flag = false;
  const elements = activities
  .filter(a => studyActions.hasOwnProperty(a.eventType)
      && studyActions[a.eventType].visible)
  .sort((a, b) => {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else {
      return 0;
    }
  })
  .map(a => {
    let hr = '';
    if (flag) {
      hr = <hr/>;
    }
    flag = true;
    return (
        <React.Fragment key={'activity-' + a.id}>
          {hr}
          <StudyTimelineActivity activity={a}/>
        </React.Fragment>
    );
  });
  return (
      <React.Fragment>
        {elements}
      </React.Fragment>
  );

};

export const ActivityTable = ({activity}) => {
  const rows = activity
  .sort((a, b) => {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else {
      return 0;
    }
  })
  .map(a => {
    return (
        <tr key={'activity-' + a.date}>
          <td>{a.user.displayName}</td>
          <td>{a.eventType}</td>
          <td>{new Date(a.date).toLocaleString()}</td>
        </tr>
    );
  });
  return (
      <Table striped>
        <thead>
        <tr>
          <th>User</th>
          <th>Action</th>
          <th>Date</th>
        </tr>
        </thead>
        <tbody>
        {rows}
        </tbody>
      </Table>
  )
};