<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<c:if test="${not empty sessionScope.errorMsg}">
    <input type="checkbox" id="hide-error" />
    <div class="error-box">
        <div class="padding">
            <label for="hide-error" class="close-btn">×</label> ${sessionScope.errorMsg}
        </div>
    </div>
    <% session.removeAttribute("errorMsg"); %>
</c:if>