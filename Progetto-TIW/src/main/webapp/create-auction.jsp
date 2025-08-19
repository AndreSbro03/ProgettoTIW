<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Auction</title>
<link rel="stylesheet" href="css/general.css">
</head>
<body class="body">
	<div class="padding">
		<div class="centered">
			<h2 class="title">Create Auction</h2>
		</div>
	</div>

	<jsp:include page="/error-message.jsp"/>

	<div class="padding">
		<form class="form" action="confirm-auction" method="POST">

			<label class="form-label">Select Items</label>
			<div class="checkbox-grid">
				<c:forEach var="item" items="${items}">
					<label class="checkbox-item"> <input type="checkbox"
						name="item_ids" value="${item.id}" />
						<span class="checkbox-label"> <c:out value="${item.name}" />
							— <c:out value="${item.price}" />€
					</span>
					</label>
				</c:forEach>
			</div>


			<label class="form-label" for="min_incr">Minimum offer
				increment</label> <input class="digit-input" type="number" name="min_incr"
				placeholder="Minimum offer increment"> <label
				class="form-label" for="expiration">Expiration</label> <input
				class="digit-input" type="date" name="date" placeholder="Date">
			<input class="digit-input" type="time" name="time" placeholder="Time">
			<input class="submit-input" type="submit" value="Add">
		</form>
	</div>

</body>
</html>