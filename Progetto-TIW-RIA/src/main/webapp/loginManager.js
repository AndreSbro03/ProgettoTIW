/**
 * Login manager
 */

/**
 * Login management
 */
(function() { // avoid variables ending up in the global scope
	
	document.getElementById("login-button").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'login', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "astemi.html";
								break;
							case 400: // bad request
							case 401: // unauthorized
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

})();