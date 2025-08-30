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
		this.alert = null;

		this.start = function() {
			buy = new Buy({
				title: this.title,
				starting: document.getElementById("buy-page"),
				aNumber: document.getElementById("auction-number"),
				auctions: document.getElementById("buy-auctions"),
				wNumber: document.getElementById("won-auction-number"),
				wonAuctions: document.getElementById("won-auctions"),
				search: document.getElementById("buy-search-form")
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

		this.refresh = function() {
			/**
			 * Load from localStorage the last page visited
			 */
			page = "BUY";
			this.show(page);
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
			auctionDetails.show(id, offerPage);

		}

		this.show = function(page, args) {
			console.log("Resquest to show: " + page);
			window.scrollTo(0, 0);
			this.reset();

			switch (page) {
				case "BUY":
					buy.show(args);
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
			}
		}


	}

}