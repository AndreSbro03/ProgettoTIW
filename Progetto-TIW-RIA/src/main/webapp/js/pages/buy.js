/**
 * Buy page viwer
 */
{
	function Buy(nodes) {
		this.title = nodes['title'];
		this.startingNode = nodes["starting"];
		this.numberNode = nodes["aNumber"];
		this.auctionsNode = nodes["auctions"];
		this.searchForm = nodes["search"];
		this.wonNumberNode = nodes["wNumber"];
		this.wonAuctionsNode = nodes["wonAuctions"]


		this.show = function(word) {
			const self = this;
			this.startingNode.style.display = "block";
			/**
			 * Title
			 */
			self.title.textContent = "Buy";
			this.getAuctions();
			this.getWonAuctions(word);
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

			/**
			 * Search Button
			 */
			this.searchForm.addEventListener("click", (e) => {
				e.preventDefault();
				var keyword = new FormData(this.searchForm).get("key-word");
				this.getAuctions(keyword)
			})

		}

		this.updateWonAuctions = function(auctions) {
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
			this.wonAuctionsNode = "";
		}

		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}