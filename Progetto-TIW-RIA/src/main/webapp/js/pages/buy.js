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
		

		this.show = function(word) {
			
			var path = "get-auctions"
			if(word) path = path.concat("?key-word=" + word);
			
			console.log(path);
			
			makeCall("GET", path, null, (req) => {
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
			
			/**
			 * Search Button
			 */
			this.searchForm.addEventListener("click", (e) => {
				e.preventDefault();
				var keyword = new FormData(this.searchForm).get("key-word");
				this.show(keyword)
			})

		}
		
		this.reset = function() {
			this.auctionsNode.innerHTML = "";
		}
		
		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}
}