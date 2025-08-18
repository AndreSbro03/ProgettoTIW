var pageOrchestrator;
{

	let buy, auctionDetails, _pageOrchestrator = new PageOrchestrator();

	window.addEventListener("load", () => {
		// Set the pageOrchestrator as global
		pageOrchestrator = _pageOrchestrator;
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
			)

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
			buy.show();
			auctionDetails.hide();
		}

		/**
		 * Clean the title and hide every page content
		 */
		this.reset = function() {
			this.title.innerHTML = "";
			buy.hide();
		}

		this.seeAuctionDetails = function(id) {
			this.reset();
			auctionDetails.show(id);

		}
	}

}