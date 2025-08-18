<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SignUp</title>
<link rel="stylesheet" href="css/general.css">
</head>
<body class="body">

	<div class="padding">
		<div class="centered">
			<h2 class="title">SignUp</h2>
		</div>
	</div>

	<div class="absolute-top-left">
		<div class="padding">
			<a href="homepage.html" class="btn">Home</a>
		</div>
	</div>

	<div class="absolute-top-right">
		<div class="padding">
			<a href="login.jsp" class="btn">LogIn</a>
		</div>
	</div>

	<jsp:include page="/error-message.jsp"/>

	<div class="padding">
		<form class="form" action="singup" method="POST">
			<label class="form-label" for="name">Name</label> <input
				class="digit-input" type="text" name="name" placeholder="Name"
				minlength="4"> <label class="form-label" for="surname">Surname</label>
			<input class="digit-input" type="text" name="surname"
				placeholder="Surname" minlength="4"> <label
				class="form-label" for="address">Address</label> <input
				class="digit-input" type="text" name="address" placeholder="Address"
				minlength="4"> <label class="form-label" for="username">Username</label>
			<input class="digit-input" type="text" name="username"
				placeholder="Username" minlength="4"> <label
				class="form-label" for="password">Password</label> <input
				class="digit-input" type="password" name="password"
				placeholder="Password" minlength="4"> <input
				class="digit-input" type="password" name="confirm-pw"
				placeholder="Repeat password" minlength="4"> <input
				class="submit-input" type="submit" value="LogIn">
		</form>
	</div>

</body>
</html>