/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
		cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

function prettyDate(date) {
	return new Date(date).toLocaleString("it-IT", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric",
		hour: "2-digit",
		minute: "2-digit",
		second: "2-digit"
	})
}

function addNode(p, type, tClass, text) {
	var c = document.createElement(type);
	c.className = tClass;
	p.appendChild(c);
	if (text != undefined) {
		var t = document.createTextNode(text);
		c.appendChild(t);
	}
	return c;
}
