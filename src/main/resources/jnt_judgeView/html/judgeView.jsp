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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources type="javascript" resources="jquery.min.js"/>

<jcr:jqom var="judges">
    <query:selector nodeTypeName="jcr:judgeInfo"/>
    <query:equalTo propertyName="j:nodename" value="${param.name}"/>
</jcr:jqom>

<c:forEach items="${judges.nodes}" var="judge">
    <c:set var="selectedJudge" value="${judge}"/>
    <div class="richtext">
        <h1>${judge.properties['judgeFirstName'].string}&nbsp;${judge.properties['judgeLastName'].string}</h1>
        <c:set var="pic" value="${judge.properties['image']}"/>
        <c:url value="${pic.node.url}" var="imgUrl"/>
        <p align="center"><img src="${imgUrl}" id="image"/></p>
        <div class="leg">${judge.properties['biography'].string}</div>
    </div>
</c:forEach>

<button type="button" id="switch_names">Switch</button>

<script>
    $('#switch_names').click(function (event) {
        $.ajax
        ({
            url: "<c:url value='${url.base}${selectedJudge.path}'/>.switch.do",
            success: function () {
                location.reload();
            }
        });
    });

</script>
