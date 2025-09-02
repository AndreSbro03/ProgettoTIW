/**
 * AuctionDetails page viewer
 */

{
	function AuctionDetails(nodes) {
		this.title = nodes['title'];
		this.startingNode = nodes['starting'];
		this.dataNode = nodes['data'];
		this.ownerNode = nodes['owner'];
		this.minIncrNode = nodes['minIncr'];
		this.initPriceNode = nodes['initPrice'];
		this.expirationNode = nodes['expiration'];
		this.bestOfferNode = nodes['bestOffer'];
		this.stateOptionNode = nodes['stateOption'];
		this.offersNode = nodes['offers'];
		this.itemsNumberNode = nodes['itemsNumber'];
		this.itemsNode = nodes['items'];

		this.offers = true;


		this.show = function(id, offers) {
			if (offers === undefined) throw new Error("Chiamata senza specifica di tipo");
			this.startingNode.style.display = "block";
			this.offers = offers;
			if (offers) this.getAuctionDetails(id);
			else this.getOwnerAuctionDetails(id);
		}

		this.getOwnerAuctionDetails = function(id, reload = false) {
			makeCall("GET", "get-user-auction?auctionId=" + id, null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var auction = JSON.parse(message);
							if (reload) this.updateOffers(auction, true); // Partialy reload
							else this.update(auction, true);
							return;
						case 401: // unauthorized
							window.location.href = "index.html";
							break;
						default:
							errorMessageBanner.show(message);
							break;
					}
				}
			}, true)
		}

		this.getAuctionDetails = function(id, reload = false) {
			makeCall("GET", "get-auction-details?auctionId=" + id, null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var auction = JSON.parse(message);
							if (reload) this.updateOffers(auction, false); // Partialy reload
							else this.update(auction, false);
							return;
						default:
							errorMessageBanner.show(message);
							break;
					}
				}
			}, true)
		}

		this.updateOffers = function(auction, userIsOwner) {
			if (userIsOwner === undefined) throw new Error("userIsOwner must be defined");
			this.stateOptionNode.innerHTML = "";
			this.bestOfferNode.innerHTML = "";
			this.offersNode.innerHTML = "";

			this.setOffersBox(auction);
			if (userIsOwner) this.setOwnerAuctionBox(auction);
			else this.setUserAuctionBox(auction);
		}

		this.setHeader = function(auction) {
			/**
			 * Set title
			 */
			this.title.textContent = "Auction n: " + auction.id;
			this.ownerNode.textContent = auction.username;
			this.minIncrNode.textContent = "Minimum increment: " + auction.minIncr + "€";
			this.initPriceNode.textContent = auction.initPrice + "€";

			/**
			 * Time
			 */
			if (auction.state === "OPEN") {
				addNode(this.expirationNode, "p", "", "Will end the: " + prettyDate(auction.dateTime));
			}
			var timer = addNode(this.expirationNode, "h2", "");
			new CountDown(auction.dateTime, timer);

		}

		this.setOffersBox = function(auction) {
			/**
			 * Offers
			 */
			if (auction.lstOffer) {
				addNode(this.bestOfferNode, "h3", "", "BEST OFFER");
				addNode(this.bestOfferNode, "div", "price", auction.lstOffer.price + "€");
				addNode(this.bestOfferNode, "p", "", "BY: " + auction.lstOffer.username.toUpperCase())

				/**
				 * Offers list
				 */
				auction.offers.forEach((offer) => this.constructOffer(offer));

			} else {
				addNode(this.bestOfferNode, "h3", "", "INIT PRICE");
				addNode(this.bestOfferNode, "div", "price", auction.initPrice + "€");
				var white = addNode(this.offersNode, "div", "centered", "No offers here, be the first!");
				white.style.color = "white";
			}
		}

		this.setOwnerAuctionBox = function(auction) {
			if (auction.state === "CLOSED") {
				if (auction.winner) {
					var p = addNode(this.stateOptionNode, "div", "item-container");
					p.style.color = "white";
					p = addNode(p, "div", "padding");
					if (auction.winner) {
						addNode(p, "h2", "", "The auction was won by: ");
						addNode(p, "p", "", "Username: " + auction.winner.username);
						addNode(p, "p", "", "Surname: " + auction.winner.surname);
						addNode(p, "p", "", "Name: " + auction.winner.name);
						addNode(p, "p", "", "Address: " + auction.winner.address);
					} else {
						addNode(p, "p", "", "No one offered for this auction");
					}
				}
			}
			else if (auction.state === "EXPIRED") {
				/**
				 * Close auction form
				 */
				var p = addNode(this.stateOptionNode, "div", "place-offer");
				p = addNode(p, "form", "form");
				p.action = "#";
				var hidden = addNode(p, "input", "");
				var submit = addNode(p, "input", "submit-input");
				hidden.type = "hidden";
				hidden.name = "auctionId";
				hidden.value = auction.id;
				submit.type = "button";
				submit.value = "Close Auction";
				var self = this;
				/**
				 * POST method to close
				 */
				submit.addEventListener('click', (e) => {
					console.log("Closing auction: " + auction.id);
					var form = e.target.closest("form");
					if (form.checkValidity()) {
						makeCall("POST", "close-auction", form,
							function(x) {
								if (x.readyState == XMLHttpRequest.DONE) {
									var message = x.responseText;
									switch (x.status) {
										case 200:
											// Notify the pageOrchestrator
											pageOrchestrator.saveState("CLOSE-AUCTION")
										
											// Reload this page
											self.show(auction.id, false);
											return;
										case 401: // unauthorized
											window.location.href = "index.html";
											break;
										case 400: // bad request
										case 500: // server error
											errorMessageBanner.show(message);
											break;
									}
								}
							}
						);
					} else {
						form.reportValidity();
					}
				});
			}
			/**
			 * Do nothing
			 */
		}

		this.setUserAuctionBox = function(auction) {
			if (auction.state === "OPEN") {
				var p = addNode(this.stateOptionNode, "div", "place-offer");
				p = addNode(p, "form", "form");
				p.action = "#";
				var label = addNode(p, "label", "form-label");
				var hidden = addNode(p, "input", "");
				var price = addNode(p, "input", "digit-input");
				var submit = addNode(p, "input", "submit-input");
				label.innerText = "Place Offer";
				hidden.type = "hidden";
				hidden.name = "auctionId";
				hidden.value = auction.id;
				price.type = "number";
				price.name = "import";
				submit.type = "button";
				submit.value = "Offer";

				var self = this;
				console.log(this.offers);
				/**
				 * POST method to send new offer
				 */
				submit.addEventListener('click', (e) => {
					var form = e.target.closest("form");
					if (form.checkValidity()) {
						makeCall("POST", 'confirm-offer', form,
							function(x) {
								if (x.readyState == XMLHttpRequest.DONE) {
									var message = x.responseText;
									switch (x.status) {
										case 200:
											// Notify pageOrchestrator
											pageOrchestrator.saveState("OFFER")
										
											// Reload this page (it's of course an offers page)
											self.show(auction.id, true);
											return;
										case 400: // bad request
										case 401: // unauthorized
										case 500: // server error
											errorMessageBanner.show(message);
											break;
									}
								}
							}
						);
					} else {
						form.reportValidity();
					}
				});
			}
			/**
			 * Else do nothing
			 */
		}

		this.update = function(auction, userIsOwner) {
			this.reset();
			this.setHeader(auction);
			this.setOffersBox(auction);

			if (userIsOwner) this.setOwnerAuctionBox(auction);
			else this.setUserAuctionBox(auction);

			/**
			 * Items grid
			 */
			this.itemsNumberNode.textContent = "There are " + auction.items.length + " items in this auction";
			new ItemsGrid(auction.items, this.itemsNode).show();
		}

		this.constructOffer = function(offer) {
			var self = this;
			var obj = addNode(self.offersNode, "div", "offer-item h-padding");
			addNode(obj, "span", "", offer.username);
			addNode(obj, "span", "", prettyDate(offer.dateTime));
			addNode(obj, "span", "", offer.price + "€");
		}

		this.reset = function() {
			this.stateOptionNode.innerHTML = "";
			this.bestOfferNode.innerHTML = "";
			this.offersNode.innerHTML = "";
			this.expirationNode.innerHTML = "";
			this.itemsNode.innerHTML = "";
		}

		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}