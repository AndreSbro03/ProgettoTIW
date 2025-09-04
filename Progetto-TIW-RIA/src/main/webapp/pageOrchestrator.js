var pageOrchestrator;
{

	let buy, sell, auctionDetails, itemForm, auctionForm, _pageOrchestrator = new PageOrchestrator();

	window.addEventListener("load", () => {

		var username = sessionStorage.getItem("username");
		if (username == null) {
			window.location.href = "index.html";
			return;
		}

		console.log("Welcome back: " + username);

		// Set the pageOrchestrator as global
		pageOrchestrator = _pageOrchestrator;

		// Link the buttons with their functions
		document.getElementById("sell-btn").addEventListener("click", () => pageOrchestrator.show("SELL"));
		document.getElementById("buy-btn").addEventListener("click", () => pageOrchestrator.show("BUY"));
		document.getElementById("add-item-btn").addEventListener("click", () => pageOrchestrator.show("ITEM-FORM"));
		document.getElementById("add-auction-btn").addEventListener("click", () => pageOrchestrator.show("AUCTION-FORM"));

		/**
		 * Search Button
		 */
		document.getElementById("buy-search-form").addEventListener("submit", (e) => {
			// Notify pageOrchestrator
			pageOrchestrator.saveState("SEARCH")
			e.preventDefault();
			var keyword = new FormData(e.target.closest("form")).get("key-word");
			buy.search(keyword);
		})


		document.getElementById("logout-btn").addEventListener("click", () => {
			makeCall("GET", "get-user-data", null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200: // OK
							sessionStorage.removeItem('username');
							window.location.href = "index.html";
							break;
						case 500: // server error
							errorMessageBanner.show(message);
							break;
						default:
							throw new Error("Unimplemented response to error");
					}
				}

			}, true);
		});

		pageOrchestrator.start();
		pageOrchestrator.refresh();

	}, false);


	function PageOrchestrator() {
		this.title = document.getElementById("title");
		this.stateName = "state";

		this.start = function() {
			buy = new Buy({
				title: this.title,
				starting: document.getElementById("buy-page"),
				aNumber: document.getElementById("auction-number"),
				auctions: document.getElementById("buy-auctions"),
				wNumber: document.getElementById("won-auction-number"),
				wonAuctions: document.getElementById("won-auctions"),
			}
			);

			sell = new Sell({
				title: this.title,
				starting: document.getElementById("sell-page"),
				greetings: document.getElementById("sell-greetings"),
				closedAuctions: document.getElementById("closed-auctions"),
				caNumber: document.getElementById("closed-auctions-number"),
				openAuctions: document.getElementById("open-auctions"),
				oaNumber: document.getElementById("open-auctions-number"),
				itemsGrid: document.getElementById("sell-items-grid"),
				iNumber: document.getElementById("sell-items-number")
			}
			);

			auctionDetails = new AuctionDetails({
				title: this.title,
				starting: document.getElementById("auction-details-page"),
				data: document.getElementById("auction-data"),
				owner: document.getElementById("auction-owner"),
				minIncr: document.getElementById("auction-min-incr"),
				initPrice: document.getElementById("auction-init-price"),
				expiration: document.getElementById("auction-expiration"),
				bestOffer: document.getElementById("auction-best-offer"),
				stateOption: document.getElementById("auction-state-option"),
				offers: document.getElementById("auction-offers-list"),
				itemsNumber: document.getElementById("auction-items-number"),
				items: document.getElementById("auction-items")
			});

			itemForm = new ItemForm({
				title: this.title,
				starting: document.getElementById("add-item-form"),
				submit: document.getElementById("item-form-submit")
			})

			auctionForm = new AuctionForm({
				title: this.title,
				starting: document.getElementById("create-auction-form"),
				submit: document.getElementById("auction-form-submit"),
				items: document.getElementById("form-free-items")
			});
		}

		/**
		 * Cookie management
		 */

		/**
		 * Get lastAction cookie name
		 */
		function _lastActionCookieName() {
			const username = sessionStorage.getItem("username");
			return "lastAction_" + encodeURIComponent(username);
		}

		/**
		 * Get lastVisitedAuctions cookie name
		 */
		function _lastVisitedCookieName() {
			const username = sessionStorage.getItem("username");
			return "lastVisitedAuctions_" + encodeURIComponent(username);
		}

		/**
		 * Getters
		 */
		function _getLastAction() {
			const name = _lastActionCookieName() + "=";
			const cookies = document.cookie.split(';');

			for (let c of cookies) {
				c = c.trim();
				if (c.indexOf(name) === 0) {
					return decodeURIComponent(c.substring(name.length));
				}
			}
			return "";
		}

		function _getVisitedAuctions() {
			const name = _lastVisitedCookieName() + "=";
			const cookies = document.cookie.split(';');

			for (let c of cookies) {
				c = c.trim();
				if (c.indexOf(name) === 0) {
					const value = decodeURIComponent(c.substring(name.length));
					if (value === "") return [];
					return value.split(',').map(num => parseInt(num, 10)).filter(n => !Number.isNaN(n));
				}
			}
			return [];
		}

		/**
		 * End of getters
		 */

		function _saveVisitedAuctionCookie(ids) {
			const d = new Date();
			d.setMonth(d.getMonth() + 1);

			/**
			 * ids join doesn't work on single values
			 */
			const value = Array.isArray(ids) ? ids.join(',') : ids;

			// Salva array come stringa separata da virgole
			document.cookie = _lastVisitedCookieName() + "=" + value +
				";expires=" + d.toUTCString() + ";path=/";
		}

		/**
		 * Possible actions:
		 * 		- search auction (Not a real action but useful to quick test) 
		 * 		- create item
		 * 		- make offer
		 * 		- close auction
		 * 		- (create auction) <-- This one should trigger the sell page 
		 */
		this.saveState = function(lastAction) {
			const d = new Date();
			d.setMonth(d.getMonth() + 1);
			document.cookie = _lastActionCookieName() + "=" + lastAction +
				";expires=" + d.toUTCString() + ";path=/";
		}

		/**
		 * remove a visited auction from the list
		 */
		function removeVisitedAuction(aId) {
			let auctions = _getVisitedAuctions();
			const id = parseInt(aId, 10);
			if (Number.isNaN(id)) return;

			if (auctions.includes(id)) {
				auctions = auctions.filter(x => x !== id);
			}
			_saveVisitedAuctionCookie(auctions);
		}
		
		/**
		 * Add a visited auction to the list
		 */
		this.saveVisitedAuction = function(aId) {
			let auctions = _getVisitedAuctions();
			const id = parseInt(aId, 10);
			if (Number.isNaN(id)) return;

			if (!auctions.includes(id)) {
				auctions.push(id);
			}
			_saveVisitedAuctionCookie(auctions);
		}


		/**
		 * End cookie management
		 */

		/**
		 * If the cookie is not present than go to BUY 
		 * If the cookie is present check if the last auciton is create auction
		 */
		this.refresh = function(a) {
			this.reset();
			var lastAction = _getLastAction();
			/**
			 * Check if action cookie exists
			 */
			if (lastAction === "") {
				/**
				 * Cookie doesn't exist
				 */

				// create them
				this.saveState("SEARCH");

				this.show("BUY");
				buy.search();
			}
			else if (lastAction === "CREATE-AUCTION") {
				this.show("SELL");
			}
			else {
				/**
				 * For each id saved in the lastVisitedAuctions we make a request
				 * to the server to get the auction obj and if the auction is expired 
				 * we don't add the result to the list and remove the id from the cookie.
				 */
				var visitedAuctions = _getVisitedAuctions();

				var requests = visitedAuctions.map((id) => {
					return new Promise((resolve) => {
						makeCall("GET", "get-auction-details?auctionId=" + id, null, (x) => {
							if (x.readyState == XMLHttpRequest.DONE) {
								switch (x.status) {
									case 200:
										var auction = JSON.parse(x.responseText);
										var expiration = new Date(auction.dateTime);
										var now = new Date();
										/**
										 * the auction is expired?
										 */
										if (expiration < now) {
											removeVisitedAuction(id);
											resolve(null);
										} else {
											resolve(auction);
										}
										break;
									case 400:
										/**
										 * The auction is not valid
										 */
										removeVisitedAuction(id);
										resolve(null);
										break;
									default:
										resolve(null);
										break;
								}
							}
						}, true);
					});
				});

				/**
				 * Wait for all auctions
				 */
				Promise.all(requests).then(results => {
					var out = results.filter(a => a !== null);
					out.sort((a, b) => {
						var dateA = new Date(a.dateTime);
						var dateB = new Date(b.dateTime);
						// First the ones the newest
						if(dateA < dateB) return 1; // a will be after b if b is older than a
						if(dateA > dateB) return -1;
						else return 0;
					});
					this.show("BUY", out);
				});
			}
		}

		/**
		 * Clean the title and hide every page content
		 */
		this.reset = function() {
			this.title.innerHTML = "";
			buy.hide();
			auctionDetails.hide();
			sell.hide();
			itemForm.hide();
			auctionForm.hide();
		}

		this.seeAuctionDetails = function(id, offerPage) {
			this.reset();
			if (offerPage) this.saveVisitedAuction(id);
			auctionDetails.show(id, offerPage);

		}

		this.show = function(page, arg) {
			console.log("Resquest to show: " + page);
			window.scrollTo(0, 0);
			this.reset();

			switch (page) {
				case "BUY":
					buy.show(arg);
					break;
				case "SELL":
					sell.show();
					break;
				case "ITEM-FORM":
					itemForm.show();
					break;
				case "AUCTION-FORM":
					auctionForm.show();
					break;
				default:
					throw new Error("Not a vaild page");
			}
		}


	}

}