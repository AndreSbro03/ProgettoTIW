<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="it.polimi.tiw.servlets.ItemImage"%>
<%@ page import="it.polimi.tiw.generals.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sell items</title>
<link rel="stylesheet" href="css/general.css">
<link rel="stylesheet" href="css/cards.css">
<link rel="stylesheet" href="css/auctionlist.css">
</head>
<body class="body">
	<div class="padding">
		<div class="centered">
			<h2 class="title">Sell</h2>
		</div>
	</div>

	<div class="absolute-top-left">
		<div class="padding">
			<a href="homepage.html" class="btn">Home</a>
		</div>
	</div>
	
	<div class="absolute-top-right">
		<div class="padding">
			<a href="log-out" class="btn">LogOut</a>
		</div>
	</div>

	<div class="centered">
		<div class="padding">
			<h2 class="light-text">Hello ${sessionScope.user.name}!</h2>
		</div>
	</div>
	<div class="centered">
		<div class="padding">
			<a href="add-item.jsp" class="btn">Add item</a>
		</div>
		<div class="padding">
			<a href="create-auction" class="btn">Create auction</a>
		</div>
	</div>

	<div class="centered">
		<div class="padding">
			<p class="light-text">You have ${openAuctions.size()} open
				auctions.</p>
		</div>
	</div>

	<c:forEach var="auction" items="${openAuctions}">
		<a href="auction-details?auctionId=${auction.id}"
			class="rounded-rectangle-link">
			<div class="rounded-rectangle">
				<!-- Square with all the auction info -->
				<div class="square">
					<div class="dark-text">
						<h2 class="product-name">
							<c:out value="${auction.initPrice}" />
							€
						</h2>
						<p>Minimum increment: ${auction.minIncr}€</p>
						<p>${AuctionUtils.diffTime(auction.dateTime)}</p>
						<p>${auction.username}</p>
					</div>
				</div>

				<!-- Auction items -->
				<div class="scroll-container h-padding">
					<c:forEach var="item" items="${auction.items}">

						<!-- Items card -->
						<div class="product-card">
							<img
								src="images?itemId=${item.id}"
								class="product-image" height="150px">
							<div class="padding">
								<h2 class="product-name">
									<c:out value="${item.name}" />
								</h2>
								<p class="product-description">
									id: <c:out value="${item.id}" />
								</p>
								<p class="product-description">
									<c:out value="${item.descr}" />
								</p>
							</div>
						</div>

					</c:forEach>
				</div>
			</div>
		</a>
	</c:forEach>

	<div class="centered">
		<div class="padding">
			<p class="light-text">You have ${closedAuctions.size()} closed
				auctions.</p>
		</div>
	</div>


	<c:forEach var="auction" items="${closedAuctions}">
		<a href="auction-details?auctionId=${auction.id}"
			class="rounded-rectangle-link">
			<div class="rounded-rectangle">
				<!-- Square with all the auction info -->
				<div class="square">
					<div class="dark-text">
						<h2 class="product-name">
							<c:out value="${auction.initPrice}" />
							€
						</h2>
						<p>Minimum increment: ${auction.minIncr}€</p>
						<p>${AuctionUtils.getDateTimeFormat(auction.dateTime)}</p>
						<p>${auction.username}</p>
					</div>
				</div>

				<!-- Auction items -->
				<div class="scroll-container h-padding">
					<c:forEach var="item" items="${auction.items}">

						<!-- Items card -->
						<div class="product-card">
							<img
								src="images?itemId=${item.id}"
								class="product-image" height="150px">
							<div class="padding">
								<h2 class="product-name">
									<c:out value="${item.name}" />
								</h2>
								<p class="product-description">
									id: <c:out value="${item.id}" />
								</p>
								<p class="product-description">
									<c:out value="${item.descr}" />
								</p>
							</div>
						</div>

					</c:forEach>
				</div>
			</div>
		</a>
	</c:forEach>


	<div class="centered">
		<div class="padding">
			<p class="light-text">Found ${items.size()} items not in auctions.</p>
		</div>
	</div>

	<div class="centered">
		<div class="product-grid">
			<c:forEach var="item" items="${items}" varStatus="row">
				<div class="product-card">
					<img
						src="images?itemId=${item.id}"
						class="product-image" height="200px">
					<div class="padding">
						<h2 class="product-name">
							<c:out value="${item.name}" />
						</h2>
						<p class="product-description">
							<c:out value="${item.descr}" />
						</p>
						<p class="product-description">
							<c:out value="${item.price}" />
							€
						</p>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>

</body>
</html>