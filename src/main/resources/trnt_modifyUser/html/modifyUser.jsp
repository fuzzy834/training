<%@ page import="org.jahia.services.content.JCRSessionFactory" %>
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

<c:set var="userName" value="<%=JCRSessionFactory.getInstance().getCurrentUser().getName()%>"/>
<jcr:jqom var="journalist">
    <query:selector nodeTypeName="jnt:user"/>
    <query:equalTo propertyName="j:nodename" value="${userName}"/>
</jcr:jqom>

<c:set var="users" value="${journalist.nodes}"/>
<c:forEach items="${users}" var="u">
    <c:set var="user" value="${u}"/>
</c:forEach>

<c:set var="magazine" value="${user.properties['magazine'].string}"/>
<c:set var="language" value="${user.properties['languages'].string}"/>
<c:set var="gender" value="${user.properties['title'].string}"/>
<c:set var="academicTitle" value="${user.properties['academicTitle'].string}"/>
<c:set var="name" value="${user.properties['name'].string}"/>
<c:set var="surname" value="${user.properties['surname'].string}"/>
<c:set var="address" value="${user.properties['address'].string}"/>
<c:set var="zipcode" value="${user.properties['npa'].string}"/>
<c:set var="city" value="${user.properties['place'].string}"/>
<c:set var="phone" value="${user.properties['phone'].string}"/>
<c:set var="cellphone" value="${user.properties['cellphone'].string}"/>
<c:set var="email" value="${user.properties['email'].string}"/>

<h1>My accreditation profile</h1>
<div class="mod modForm" id="editForm">
    <form action="<c:url value='${url.base}${user.path}'/>.send.do"
          method="post" name="frmFormular">
        <ol>
            <li class="row">
                <div class="label"><label for="magazine">Newspapers concerned</label></div>
                <div class="fields"><input type="text" class="text" name="magazine" id="magazine" value="${magazine}"
                                           readonly/></div>
            </li>
            <li class="row">
                <div class="label"><label>Language of work&nbsp;*</label></div>
                <div class="fields">
                    <label for="french">French</label>
                    <input id="french" name="french" type="checkbox" value="French"
                           <c:if test="${fn:contains(language, 'French')}">checked</c:if> onclick="return false;"/><br>
                    <label for="german">German</label>
                    <input id="german" name="german" type="checkbox" value="German"
                           <c:if test="${fn:contains(language, 'German')}">checked</c:if> onclick="return false;"/><br>
                    <label for="italian">Italian</label>
                    <input id="italian" name="italian" type="checkbox" value="Italian"
                           <c:if test="${fn:contains(language, 'Italian')}">checked</c:if> onclick="return false;"/><br>
                </div>
            </li>
            <h1>My contact information</h1>
            <li class="row">
                <div class="label"><label for="gender">Gender&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text" name="title" id="gender" value="${gender}"
                                           readonly/></div>
            </li>

            <li class="row">
                <div class="label"><label for="title">Academic title</label></div>
                <div class="fields"><input type="text" class="text" name="academicTitle" id="title" size="20"
                                           value="${academicTitle}" readonly/>
                </div>
            </li>

            <li class="row">
                <div class="label"><label for="name">Name&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text" name="name" id="name"
                                           size="20" value="${name}" readonly/></div>
            </li>

            <li class="row">
                <div class="label"><label for="surname">Surname&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text" name="surname" id="surname" size="20"
                                           value="${surname}" readonly/></div>
            </li>

            <li class="row">
                <div class="label"><label for="address">Address&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text" name="address" id="address"
                                           size="20" value="${address}"/></div>
            </li>

            <li class="row">
                <div class="label"><label for="zip">Zip code&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text zip" name="npa" id="zip" size="20"
                                           value="${zipcode}"/>

            <li class="row">
                <div class="label"><label for="city">City&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text zip" name="place" id="city" size="20"
                                           value="${city}"/>

            <li class="row">
                <div class="label"><label for="phone">Phone</label></div>
                <div class="fields"><input type="text" class="text" name="phone" id="phone" size="20"
                                           value="${phone}"/>
                </div>
            </li>

            <li class="row">
                <div class="label"><label for="mobile">Mobile</label></div>
                <div class="fields"><input type="text" class="text" name="cellphone" id="mobile" size="20"
                                           value="${cellphone}"/>
                </div>
            </li>

            <li class="row">
                <div class="label"><label for="email">Email&nbsp;*</label></div>
                <div class="fields"><input type="text" class="text" name="email" id="email" size="20"
                                           value="${email}"/></div>
            </li>
        </ol>
        <div class="buttonbar">
            <button id="submit" type="submit">Submit</button>
        </div>
    </form>
</div>
