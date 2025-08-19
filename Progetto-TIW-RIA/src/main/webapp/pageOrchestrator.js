var pageOrchestrator;
{

	let buy, sell, auctionDetails, _pageOrchestrator = new PageOrchestrator();

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
		
		pageOrchestrator.start();
		pageOrchestrator.refresh();

	}, false);


	function PageOrchestrator() {
		this.title = document.getElementById("title");
		this.alert = null;

		this.start = function() {
			buy = new Buy(
				this.title,
				this.alert,
				document.getElementById("buy-page"),
				document.getElementById("auction-number")
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
				alert: this.alert,
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
		}

		this.seeAuctionDetails = function(id) {
			this.reset();
			auctionDetails.show(id);

		}

		this.show = function(page) {
			console.log("Resquest to show: " + page);
			window.scrollTo(0, 0);
			this.reset();

			switch (page) {
				case "BUY":
					buy.show();
					break;
				case "SELL":
					sell.show();
					break;
			}
		}


	}

}