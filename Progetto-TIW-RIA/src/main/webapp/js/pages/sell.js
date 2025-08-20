/**
 * Sell page viewer
 */
{

	function Sell(nodes) {
		this.title = nodes['title'];
		this.startingNode = nodes['starting'];
		this.greetings = nodes['greetings'];
		this.closedAuctions = nodes['closedAuctions'];
		this.caNumber = nodes['caNumber'];
		this.openAuctions = nodes['openAuctions'];
		this.oaNumber = nodes['oaNumber'];
		this.itemsGrid = nodes['itemsGrid'];
		this.iNumber = nodes['iNumber'];
		
		this.show = function() {
			makeCall("GET", "get-user-data", null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200: // OK
							const data = JSON.parse(message);
							const items = data.items;
							const closed = data.closedAuctions;
							const open = data.openAuctions;
							this.update(closed, open, items);
							break;
						case 400: // bad request
						case 500: // server error
							errorMessageBanner.show(message);
							break;
						case 401: // unauthorized
							window.location.href = "index.html";
							break;
						default:
							throw new Error("Unimplemented response to error");
					}
				}

			}, true);
		}
		
		this.update = function(closed, open, items) {
			this.reset();
			this.startingNode.style.display = "block";
			this.title.innerText = "Sell";
			this.greetings.innerText = "Welcome back " + sessionStorage.getItem("username");
			new AuctionList(closed, this.closedAuctions).show();
			this.caNumber.innerText = "You have " + closed.length + " closed auctions.";
			new AuctionList(open, this.openAuctions).show();
			this.oaNumber.innerText = "You have " + open.length + " open auctions.";
			new ItemsGrid(items, this.itemsGrid).show();
			this.iNumber.innerText = "Found " + items.length + " items not in auctions.";
		}
		
		this.hide = function() {
			console.log("hiding");
			this.startingNode.style.display = "none";
		}
		
		this.reset = function(){
			this.closedAuctions.innerHTML = "";
			this.openAuctions.innerHTML = "";
			this.itemsGrid.innerHTML = "";
		}
	}
}