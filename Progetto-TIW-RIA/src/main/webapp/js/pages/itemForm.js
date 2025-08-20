/**
 * Form page viewer
 */
{
	function ItemForm(nodes) {
		this.title = nodes["title"];
		this.startingNode = nodes["starting"];
		this.submitButton = nodes["submit"];

		this.show = function() {
			this.startingNode.style.display = "block";
			/**
			 * No server request needed
			 */
			this.update();
		}


		this.update = function() {
			this.reset();
			this.title.innerText = "Add Item";
			this.submitButton.addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'add-item', form,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200:
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

		this.hide = function() {
			this.startingNode.style.display = "none";
		}

		this.reset = function() {
		}

	}
}