<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Copyright 2020 the original author or authors
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="user" value="${pageContext.request.userPrincipal.name}"/>

<html>
<head>
    <meta charset="utf-8">
    <meta content="IE=edge" http-equiv="X-UA-Compatible">
    <meta content="width=device-width, initial-scale=1, shrink-to-fit=no" name="viewport">
    <link href="${contextPath}/static/favicon.ico" rel="shortcut icon"/>
    <title>Study Tracker</title>

    <link crossorigin href="//fonts.gstatic.com/" rel="preconnect">

    <link href="${contextPath}/static/css/theme.css" rel="stylesheet">
    <link href="${contextPath}/static/css/app.css" rel="stylesheet">

    <style>
        body {
            opacity: 0;
        }
    </style>

</head>
<body>

<noscript>You need to enable JavaScript to run this app.</noscript>

<div id="root"></div>

<script src="${contextPath}/static/js/bundle.js"></script>
<script type="application/javascript">
  var params = {};
  dtx.renderApp(params);
</script>

</body>
</html>
