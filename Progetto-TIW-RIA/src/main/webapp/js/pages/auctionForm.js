/**
 * Add auction form viewer 
 */
{
	function AuctionForm(nodes) {
		this.title = nodes["title"];
		this.startingNode = nodes["starting"];
		this.submit = nodes["submit"];
		this.items = nodes["items"];

		this.show = function() {
			this.startingNode.style.display = "block";
			/**
			 * Get all avaiable items
			 */
			// Necessario () => {} perchè sennò this va perso
			makeCall("GET", "get-free-items", null, (x) => {
				if (x.readyState == XMLHttpRequest.DONE) {
					var message = x.responseText;
					switch (x.status) {
						case 200:
							var items = JSON.parse(message);
							this.update(items);
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

		this._checkAuctionForm = function(form) {
			/**
			 * Check for:
			 * 		- empty ids
			 * 		- date time in the past
			 */
			// IDS
			const selectedIds = Array.from(
				form.querySelectorAll("#form-free-items input[type='checkbox']:checked")
			).map(cb => cb.id);
			console.log("Selected IDs:", selectedIds);

			if (selectedIds.length == 0) {
				return { valid: false, message: "Select at least one item" };
			}

			// DATETIME
			const date = form.querySelector("input[name='date']").value;
			const time = form.querySelector("input[name='time']").value;

			if (!date || !time) {
				return { valid: false, message: "Fill date and time" };
			}

			const expiration = new Date(`${date}T${time}`);
			const now = new Date();

			if (expiration <= now) {
				return { valid: false, message: "date is in the past" };
			}

			return { valid: true, message: "OK" };
		}

		this.update = function(items) {
			this.reset();
			this.title.innerText = "Create Auction";
			items.forEach((item) => {
				var p = addNode(this.items, "label", "checkbox-item");
				var check = addNode(p, "input", "");
				check.type = "checkbox";
				check.name = "items_ids";
				check.value = item.id;
				var text = addNode(p, "span", "checkbox-label");
				text.innerText = item.name + " - " + item.price + "€";
			})
			this.submit.addEventListener('click', (e) => {
				console.log("Cliked");
				var form = e.target.closest("form");
				if (form.checkValidity()) {

					var check = this._checkAuctionForm(form);
					if (!check.valid) {
						errorMessageBanner.show("js: " + check.message);
						return;
					}
					
					makeCall("POST", 'confirm-auction', form,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
										// Notify pageOrchestrator
										pageOrchestrator.saveState("CREATE-AUCTION");
										pageOrchestrator.show("SELL");
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
						}
					);
				} else {
					form.reportValidity();
				}
			});
		}

		this.reset = function() {
			this.items.innerHTML = "";
		}

		this.hide = function() {
			this.startingNode.style.display = "none";
		}
	}

}