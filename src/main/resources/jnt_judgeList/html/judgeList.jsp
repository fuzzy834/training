<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources type="javascript" resources="jquery.min.js"/>

<c:set var="sortBy" value="judgeFirstName"/>
<c:set var="order" value="desc"/>

<c:if test="${not empty param.sortBy}">
    <c:set var="sortBy" value="${param.sortBy}"/>
    <c:set var="order" value="${param.order}"/>
</c:if>

<jcr:jqom var="judges">
    <query:selector nodeTypeName="jcr:judgeInfo"/>
    <query:descendantNode path="/sites/bger/contents/judges"/>
    <query:sortBy propertyName="${sortBy}" order="${order}"/>
</jcr:jqom>

<table id="judge_table" class="base">
    <thead>
    <tr>
        <th valign="top">Name</th>
        <th valign="top">Vorname</th>
        <th valign="top">Amts-antritt</th>
        <th valign="top">Amts-austritt</th>
        <th valign="top">Abteilung</th>
        <th valign="top">Kanton</th>
        <th valign="top">Partei</th>
        <th valign="top">Geburtsjahr/Todesjahr</th>
    </tr>
    <tr>
        <th>
            <a href="${renderContext.mainResource.node.url}?sortBy=judgeFirstName&order=asc"><img
                    src="<c:url value="${url.currentModule}/images/asc.gif"/>" width="15" height="15" border="0"
                    alt="sort asc"/><img src="<c:url value="${url.currentModule}/images/spacer.gif"/>" width="3"
                                         height="1" alt=""/></a>
            <a href="${renderContext.mainResource.node.url}?sortBy=judgeFirstName&order=desc"><img
                    src="<c:url value="${url.currentModule}/images/desc.gif"/>" width="15" height="15" border="0"
                    alt="sort desc"/></a>
        </th>
        <th></th>
        <th>
            <a href="${renderContext.mainResource.node.url}?sortBy=yearStarted&order=asc"><img
                    src="<c:url value="${url.currentModule}/images/asc.gif"/>" width="15" height="15" border="0"
                    alt="sort asc"/><img src="<c:url value="${url.currentModule}/images/spacer.gif"/>" width="3"
                                         height="1" alt=""/></a>
            <a href="${renderContext.mainResource.node.url}?sortBy=yearStarted&order=desc"><img
                    src="<c:url value="${url.currentModule}/images/desc.gif"/>" width="15" height="15" border="0"
                    alt="sort desc"/></a>
        </th>
        <th></th>
        <th></th>
        <th>
            <a href="${renderContext.mainResource.node.url}?sortBy=county&order=acc"><img
                    src="<c:url value="${url.currentModule}/images/asc.gif"/>" width="15" height="15" border="0"
                    alt="sort asc"/><img src="<c:url value="${url.currentModule}/images/spacer.gif"/>" width="3"
                                         height="1" alt=""/></a>
            <a href="${renderContext.mainResource.node.url}?sortBy=county&order=desc"><img
                    src="<c:url value="${url.currentModule}/images/desc.gif"/>" width="15" height="15" border="0"
                    alt="sort desc"/></a>
        </th>
        <th></th>
        <th></th>
    </tr>
    </thead>
    <tbody>

    <c:url var="link" value="${renderContext.mainResource.node.url}"/>
    <c:url var="trimmedLink" value="${fn:substring(link,0,fn:length(link)-5 )}"/>

    <c:forEach items="${judges.nodes}" var="judge">

    <c:set var="nodeInstance" value="${judge}"/>

    <c:set var="firstName" value="${judge.properties['judgeFirstName'].string}"/>
    <c:set var="lastName" value="${judge.properties['judgeLastName'].string}"/>
    <c:set var="started" value="${judge.properties['yearStarted'].long}"/>
    <c:set var="left" value="${judge.properties['yearLeft'].long}"/>
    <c:set var="court" value="${judge.properties['court'].string}"/>
    <c:set var="county" value="${judge.properties['county'].string}"/>
    <c:set var="party" value="${judge.properties['party'].string}"/>
    <c:set var="birth" value="${judge.properties['birth'].long}"/>
    <c:set var="death" value="${judge.properties['death'].long}"/>

    <tr>
        <td valign="top"><a href=""
                            onclick="window.open(
                                    '<c:url
                                    value="${trimmedLink}/judge-view.html?name=${judge.properties['j:nodename'].string}"/>',
                                    'Media',
                                    'resizable=yes,toolbar=no,status=no,menubar=no,scrollbars=yes,width=1100,height=680');"
                            class="color">${firstName}</a></td>
        <td valign="top">${lastName}</td>
        <td valign="top">${started}</td>
        <td valign="top">${left}</td>
        <td valign="top">${court}</td>
        <td valign="top">${county}</td>
        <td valign="top">${party}</td>
        <td valign="top"><c:out value="${birth}-${death}"/></td>
    </tr>
    </tbody>
    </c:forEach>
</table>

<button type="button" id="switch_names">Switch All</button>

<script>
       $('#switch_names').click(function (event) {
        $.ajax
        ({
            url: "<c:url value='${url.base}${nodeInstance.parent.path}'/>.switch.do",
            success: function () {
                location.reload();
            }
        });
    });
</script>

