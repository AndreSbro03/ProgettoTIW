<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="it.polimi.tiw.servlets.ItemImage"%>
<%@ page import="it.polimi.tiw.generals.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Auction Detail</title>
<link rel="stylesheet" href="css/general.css">
<link rel="stylesheet" href="css/cards.css">
<link rel="stylesheet" href="css/auction-details.css">
</head>
<body class="body">

	<div class="absolute-top-left">
		<div class="padding">
			<a href="homepage.html" class="btn">Home</a>
		</div>
	</div>

	<div class="absolute-top-right">
		<div class="padding">
			<a href="buy" class="btn">Buy</a>
		</div>
		<div class="padding">
			<a href="sell" class="btn">Sell</a>
		</div>
	</div>

	<c:choose>
		<c:when test="${auction != null}">
			<div class="padding">
				<div class="centered">
					<h2 class="title">Auction n: ${auction.id}</h2>
				</div>
			</div>

			<jsp:include page="/error-message.jsp" />

			<div class="auction-container">
				<div class="item-container">
					<div class="padding">
						<!-- Header -->
						<div class="auction-header">
							<div class="h-padding">
								<h2>${auction.username}</h2>
								<p>Minimum Increment: ${auction.minIncr}€</p>
							</div>
							<div class="h-padding">
								<p>Starting from:</p>
								<h2>${auction.initPrice}€</h2>
							</div>
							<div class="h-padding">
								<c:choose>
									<c:when test="${auction.state == AuctionState.OPEN}">
										<p>Will end the:
											${AuctionUtils.getDateTimeFormat(auction.dateTime)}</p>
										<h2>${AuctionUtils.diffTime(auction.dateTime)}</h2>
									</c:when>
									<c:otherwise>
										<p>Terminated the:
											${AuctionUtils.getDateTimeFormat(auction.dateTime)}</p>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>

				<div class="auction-main padding">
					<!-- Left column -->
					<div class="left-column">
						<!-- Best Offer -->
						<div class="best-offer">
							<c:choose>
								<c:when test="${auction.lstOffer != null}">
									<h3>BEST OFFER</h3>
									<div class="price">${auction.lstOffer.getPrice()}€</div>
									<p>BY ${auction.lstOffer.getUsername().toUpperCase()}</p>
								</c:when>
								<c:otherwise>
									<!-- If not present display the initPrice -->
									<h3>INIT PRICE</h3>
									<div class="price">${auction.initPrice}€</div>
								</c:otherwise>
							</c:choose>
						</div>

						<c:choose>
							<c:when test="${auction.state == AuctionState.CLOSED }">
								<!-- Auction is closed (Show the info) -->
								<div class="item-container" style="color: white;">
									<c:choose>
										<c:when test="${winner == null}">
											<p>No one offered for this auction</p>
										</c:when>
										<c:otherwise>
											<div class="padding">
												<h2>The auction was won by:</h2>
												<p>UserName: ${winner.username}</p>
												<p>Name: ${winner.name}</p>
												<p>Surname: ${winner.surname}</p>
												<p>Address: ${winner.address}</p>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${auction.state == AuctionState.EXPIRED }">
										<!-- Auction is expired (You can close it) -->
										<div class="place-offer">
											<form class="form" action="close-auction" method="POST">
												<input type="hidden" name="auctionId" value="${auction.id}">
												<input class="submit-input" type="submit"
													value="Close Auction">
											</form>
										</div>
									</c:when>
									<c:otherwise>
										<!-- You can't do nothing -->
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</div>

					<!-- Offers List (Right Column) -->
					<div class="offers">
						<div class="padding">
							<h3>OFFERS</h3>
							<div class="offers-list">
								<c:choose>
									<c:when test="${auction.lstOffer != null}">
										<c:forEach var="offer" items="${offers}">
											<div class="offer-item h-padding">
												<span>${offer.username}</span> <span>${AuctionUtils.getDateTimeFormat(offer.dateTime)}</span>
												<span>${offer.price}$</span>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="centered">
											<p style="color: white;">No offers here, be the first!</p>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
				<!-- All the items -->
				<div class="centered padding">
					<p class="light-text">There are ${auction.items.size()} items
						in this auction</p>
				</div>
				<div class="padding">
					<div class="product-grid">
						<c:forEach var="item" items="${auction.items}" varStatus="row">
							<div class="product-card">
								<img src="images?itemId=${item.id}" class="product-image"
									height="200px">
								<div class="padding">
									<h2 class="product-name">
										<c:out value="${item.name}" />
									</h2>
									<p class="product-description">
										id:
										<c:out value="${item.id}" />
									</p>
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
			</div>



		</c:when>
		<c:otherwise>
			<div class="padding">
				<div class="centered">
					<h2 class="title">No auction found</h2>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

</body>
</html>