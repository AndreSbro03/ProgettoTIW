{

	function AuctionList(_auctions, _startingNode, _offersPage) {
		this.startingNode = _startingNode;
		this.auctions = _auctions;
		this.offersPage = _offersPage;

		function appendItemCard(startingNode, item) {
			var ip = startingNode;
			ip = addNode(ip, "div", "product-card");
			var image = addNode(ip, "img", "product-image");
			image.height = 150;
			image.src = "images?itemId=" + item.id;
			ip = addNode(ip, "div", "padding");
			addNode(ip, "h2", "product-name", item.name);
			addNode(ip, "p", "product-description", item.descr);
		}

		this.show = function() {
			if(this.offersPage === undefined) throw new Error("Missing offersPage");
			var self = this;
			/// Clean the container
			self.startingNode.innerHTML = "";
			/// Iter the auctions
			self.auctions.forEach(function(auction) {
				console.log(auction);
				var rr = addNode(self.startingNode, "div", "rounded-rectangle");

				/**
				 * Make the rectangle clickable
				 */
				rr.addEventListener("click", (e) => {
					// dependency via module parameter
					console.log("Clicked on element with id: " + auction.id);
					pageOrchestrator.seeAuctionDetails(auction.id, self.offersPage);
					//auctionDetails.show(e.target.getAttribute("auctionId")); // the list must know the details container
				}, false);

				p = addNode(rr, "div", "square");
				p = addNode(p, "div", "dark-text");
				/**
				 * Calculate current price 
				 */
				var currPrice = (auction.lstOffer) ? auction.lstOffer.price : auction.initPrice;
				addNode(p, "h2", "product-name", currPrice + "€");
				/**
				 * Show auctiond information
				 */
				addNode(p, "p", "product-descr", "Minimum increment: " + auction.minIncr);
				var countDown = addNode(p, "p", "product-descr");
				new CountDown(auction.dateTime, countDown);
				addNode(p, "p", "product-descr", auction.username);
				var cp = addNode(rr, "div", "scroll-container h-padding");
				/**
				 * Render items cards
				 */
				auction.items.forEach((item) => appendItemCard(cp, item));
			});
		}

		this.update = function(_auctions) {
			this.auctions = _auctions;
			this.show();
		}
	}


}