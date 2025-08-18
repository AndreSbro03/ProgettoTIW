<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>LogIn</title>
<link rel="stylesheet" href="css/general.css">
</head>
<body class="body">

	<div class="padding">
		<div class="centered">
			<h2 class="title">LogIn</h2>
		</div>
	</div>

	<div class="absolute-top-left">
		<div class="padding">
			<a href="homepage.html" class="btn">Home</a>
		</div>
	</div>

	<jsp:include page="/error-message.jsp"/>

	<div class="absolute-top-right">
		<div class="padding">
			<a href="signup.jsp" class="btn">SignUp</a>
		</div>
	</div>

	<div class="padding">
		<form class="form" action="login" method="POST">
			<label class="form-label" for="username">Username</label> <input
				class="digit-input" type="text" name="username"
				placeholder="Username"> <label class="form-label"
				for="password">Password</label> <input class="digit-input"
				type="password" name="password" placeholder="Password"> <input
				class="submit-input" type="submit" value="LogIn">
		</form>
	</div>

</body>
</html>