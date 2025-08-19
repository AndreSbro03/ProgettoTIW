/**
 * AuctionDetails page viewer
 */

{
	function AuctionDetails(nodes) {
		this.alert = nodes['alert'];
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

		this.show = function(id) {
			this.startingNode.style.display = "block";
			makeCall("GET", "get-auction-details?auctionId=" + id, null, (req) => {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var auction = JSON.parse(req.responseText);
						this.update(auction);

					} else if (req.status == 403) {
						//TODO
					}
					else {
						self.alert.textContent = message;
					}
				}
			}, true)
		}

		this.update = function(auction) {
			var self = this;
			this.reset();

			/**
			 * Set title
			 */
			self.title.textContent = "Auction n: " + auction.id;
			self.ownerNode.textContent = auction.username;
			self.minIncrNode.textContent = "Minimum increment: " + auction.minIncr + "€";
			self.initPriceNode.textContent = auction.initPrice + "€";

			/**
			 * Time
			 */
			if (auction.state === "OPEN") {
				addNode(self.expirationNode, "p", "", "Will end the: " + prettyDate(auction.dateTime));
				var timer = addNode(self.expirationNode, "h2", "");
				new CountDown(auction.dateTime, timer);
			} else {
				addNode(self.expirationNode, "p", "", "Terminated the: " + new Date(auction.dateTime).getDate());
			}

			/**
			 * Offers
			 */
			if (auction.lstOffer) {
				addNode(self.bestOfferNode, "h3", "", "BEST OFFER");
				addNode(self.bestOfferNode, "div", "price", auction.lstOffer.price + "€");
				addNode(self.bestOfferNode, "p", "", "BY: " + auction.lstOffer.username.toUpperCase())

				/**
				 * Offers list
				 */
				auction.offers.forEach((offer) => this.constructOffer(offer));

			} else {
				addNode(self.bestOfferNode, "h3", "", "INIT PRICE");
				addNode(self.bestOfferNode, "div", "price", auction.initPrice + "€");
				var white = addNode(self.offersNode, "div", "centered", "No offers here, be the first!");
				white.style.color = "white";
			}

			/**
			 * Auction state depent box
			 */
			console.log("Auction state: " + auction.state);
			if (auction.state === "CLOSED") {
				var p = addNode(self.stateOptionNode, "div", "item-container");
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
			} else {
				/**
				 * Check if the user is the owner of the auction
				 */
				if (auction.username === sessionStorage.getItem("username").trim()) {
					if (auction.state === "EXPIRED") {
						/**
						 * Close auction form
						 */
						var p = addNode(self.stateOptionNode, "div", "place-offer");
						p = addNode(p, "form", "form");
						p.action = "#";
						var hidden = addNode(p, "input", "");
						var submit = addNode(p, "input", "submit-input");
						hidden.type = "hidden";
						hidden.name = "auctionId";
						hidden.value = auction.id;
						submit.type = "submit";
						submit.value = "Close Auction";
						/**
						 * TODO: POST method to close
						 */
					}
				} else {
					if (auction.state === "OPEN") {
						var p = addNode(self.stateOptionNode, "div", "place-offer");
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
						submit.type = "submit";
						submit.value = "Offer";
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
													// Reload this page
													pageOrchestrator.seeAuctionDetails(auction.id);
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
					 * Items grid
					 */
					self.itemsNumberNode.textContent = "There are " + auction.items.length + " items in this auction";
					new ItemsGrid(auction.items, self.itemsNode).show();

				}
			}
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