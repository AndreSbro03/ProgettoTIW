/**
 * Global error message banner widget
 */

let errorMessageBanner = new ErrorMessageBanner();

window.addEventListener("load", () => {
	errorMessageBanner.start();
}, false);

function ErrorMessageBanner() {
	this.banner;

	this.start = function() {
		this.banner = document.getElementById("error-box");
	};

	this.show = function(errorMsg) {
		var p = addNode(this.banner, "div", "error-box");
		p = addNode(p, "div", "padding");
		addNode(p, "label", "close-btn", "x").addEventListener("click", () => {
			console.log("cliked");
			this.hide();
		});
		var msg = addNode(p, "p", "");
		msg.innerText = errorMsg;
	};

	this.hide = function() {
		this.banner.innerHTML = "";
	};
}
