<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add item</title>
<link rel="stylesheet" href="css/general.css">
</head>
<body class="body">
	<div class="padding">
		<div class="centered">
			<h2 class="title">Add Item</h2>
		</div>
	</div>
	
	<jsp:include page="/error-message.jsp"/>

	<div class="padding">
		<form class="form" action="add-item" method="POST"
			enctype="multipart/form-data">
			<label class="form-label" for="name">Name</label> <input
				class="digit-input" type="text" name="name" placeholder="Name">

			<label class="form-label" for="description">Description</label> <input
				class="digit-input" type="text" name="description"
				placeholder="Description"> <label class="form-label"
				for="image">Image</label> <input class="file-input" type="file"
				name="image" accept="image/*"> <label class="form-label"
				for="price">Price</label> <input class="digit-input" type="number"
				name="price" placeholder="Price" step="0.01"> <input
				class="submit-input" type="submit" value="Add">
		</form>
	</div>


</body>
</html>