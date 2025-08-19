/**
 * Buy page viwer
 */
{
	function Buy(_title, _alert, _startingNode, _numberNode) {
		this.alert = _alert;
		this.title = _title;
		this.startingNode = _startingNode;
		this.numberNode = _numberNode;

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
			
			if(auctions.lenght !== 0) {
				/**
				 * AuctionList
				 */
				var op = document.createElement("div");
				op.className = "auctions-shower";
				new AuctionList(auctions, op).show();
				self.startingNode.appendChild(op);
			}

		}
		
		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}