/**
 * Buy page viwer
 */
{
	function Buy(nodes) {
		this.title = nodes['title'];
		this.startingNode = nodes["starting"];
		this.numberNode = nodes["aNumber"];
		this.auctionsNode = nodes["auctions"];

		this.show = function() {
			makeCall("GET", "get-auctions", null, (req) => {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var auctions = JSON.parse(req.responseText);
						this.update(auctions);
					} else if (req.status == 403) {
						//TODO
					}
					else {
						self.alert.textContent = message;
					}
				}
			}, true)
		}

		this.update = function(auctions) {
			const self = this;
			this.startingNode.style.display = "block";
			/**
			 * Title
			 */
			self.title.textContent = "Buy";
			/**
			 * Auction number
			 */
			self.numberNode.textContent = "Number of results: " + auctions.length;
			
			/**
			 * AuctionList
			 */
			if(auctions.lenght !== 0) {
				new AuctionList(auctions, self.auctionsNode).show();
			}

		}
		
		this.reset = function() {
			this.auctionsNode.innerHTML = "";
		}
		
		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}