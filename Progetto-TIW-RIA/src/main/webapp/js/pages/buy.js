/**
 * Buy page viwer
 */
{
	function Buy(nodes) {
		this.title = nodes['title'];
		this.startingNode = nodes["starting"];
		this.numberNode = nodes["aNumber"];
		this.auctionsNode = nodes["auctions"];
		this.wonNumberNode = nodes["wNumber"];
		this.wonAuctionsNode = nodes["wonAuctions"]
		this.init = false;

		this.search = function(word) {
			console.log("Searching: " + word);
			this.getAuctions(word);
		}


		/**
		 * If some auctions are provided than don't call getAuctions
		 */
		this.show = function(auctions) {
			this.startingNode.style.display = "block";
			/**
			 * Title
			 */
			this.title.textContent = "Buy";
			this.getWonAuctions();

			if (!this.init) {
				if (auctions === undefined) this.getAuctions();
				else this.updateAuctions(auctions)
				this.init = true;
			}
		}

		this.getAuctions = function(word) {
			var path = "get-auctions"
			if (word) path = path.concat("?key-word=" + word);

			console.log(path);

			makeCall("GET", path, null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var auctions = JSON.parse(message);
							this.updateAuctions(auctions);
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
			}, true)
		}

		this.getWonAuctions = function() {
			makeCall("GET", 'get-won-auctions', null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var auctions = JSON.parse(message);
							this.updateWonAuctions(auctions, true);
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
			}, true)
		}

		this.updateAuctions = function(auctions) {
			this.auctionsNode.innerHTML = "";
			const self = this;
			/**
			 * Auction number
			 */
			self.numberNode.textContent = "Number of results: " + auctions.length;

			/**
			 * AuctionList
			 */
			if (auctions.lenght !== 0) {
				new AuctionList(auctions, self.auctionsNode, true).show();
			}

		}

		this.updateWonAuctions = function(auctions) {
			this.wonAuctionsNode.innerHMTL = "";
			/**
			 * Auction number
			 */
			this.wonNumberNode.textContent = "Number of results: " + auctions.length;

			/**
			 * AuctionList
			 */
			if (auctions.lenght !== 0) {
				new AuctionList(auctions, this.wonAuctionsNode, true).show();
			}
		}

		this.reset = function() {
			this.auctionsNode.innerHTML = "";
			this.wonAuctionsNode.innerHMTL = "";
		}

		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}