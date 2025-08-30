<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="it.polimi.tiw.servlets.ItemImage"%>
<%@ page import="it.polimi.tiw.generals.AuctionUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Buy items</title>
<link rel="stylesheet" href="css/general.css">
<link rel="stylesheet" href="css/searchbar.css">
<link rel="stylesheet" href="css/auctionlist.css">
<link rel="stylesheet" href="css/cards.css">
</head>
<body class="body">
	<div class="padding">
		<div class="centered">
			<h2 class="title">Buy</h2>
		</div>
	</div>

	<div class="absolute-top-left">
		<div class="padding">
			<a href="homepage.html" class="btn">Home</a>
		</div>
	</div>
	
	<div class="absolute-top-right">
		<div class="padding">
			<a href="#auction-won" class="btn">Auction won</a>
		</div>
	</div>

	<form action="search-auctions" method="POST" class="search-bar">
		<input type="text" name="key-word" placeholder="Search..."
			class="search-input">
		<button type="submit" class="search-button">
			<img class="small-icon"
				src="https://img.icons8.com/?size=100&id=132&format=png&color=000000"
				alt="Search">
		</button>
	</form>


	<div class="centered">
		<div class="padding">
			<p class="light-text">
				Results
				<c:out value="${number}" />
			</p>
		</div>
	</div>

	<c:choose>
		<c:when test="${number > 0}">

			<c:forEach var="auction" items="${auctions}">
				<a href="offers?auctionId=${auction.id}"
					class="rounded-rectangle-link">
					<div class="rounded-rectangle">
						<!-- Square with all the auction info -->
						<div class="square">
							<div class="dark-text">
								<h2 class="product-name">
									<c:out value="${(auction.lstOffer == null) ? auction.initPrice : auction.lstOffer.price}" />
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
										src="${pageContext.request.contextPath}/${ItemImage.getImageUrl(item.id)}"
										class="product-image" height="150px">
									<div class="padding">
										<h2 class="product-name">
											<c:out value="${item.name}" />
										</h2>
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

		</c:when>
		<c:otherwise>
            No auctions match the search.
        </c:otherwise>
	</c:choose>
	
	<div id="auction-won" class="padding">
		<div class="centered">
			<h2 class="title">Auction won</h2>
		</div>
	</div>
	
	<div class="centered">
		<div class="padding">
			<p class="light-text">
				Results
				<c:out value="${wonNumber}" />
			</p>
		</div>
	</div>

	<c:choose>
		<c:when test="${wonNumber > 0}">

			<c:forEach var="auction" items="${wonAuctions}">
					<div class="rounded-rectangle">
						<!-- Square with all the auction info -->
						<div class="square">
							<div class="dark-text">
								<h2 class="product-name">
									<c:out value="${(auction.lstOffer == null) ? auction.initPrice : auction.lstOffer.price}" />
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
										src="${pageContext.request.contextPath}/${ItemImage.getImageUrl(item.id)}"
										class="product-image" height="150px">
									<div class="padding">
										<h2 class="product-name">
											<c:out value="${item.name}" />
										</h2>
										<p class="product-description">
											<c:out value="${item.descr}" />
										</p>
									</div>
								</div>

							</c:forEach>
						</div>
					</div>
			</c:forEach>

		</c:when>
		<c:otherwise>
            <div class="centered">You never won an auction.</div>
        </c:otherwise>
	</c:choose>



</body>
</html>