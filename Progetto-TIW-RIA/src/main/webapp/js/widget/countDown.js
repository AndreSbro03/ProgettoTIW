{
	function CountDown(_endTime, _node) {
		this.targetDate = new Date(_endTime); // assicuriamoci che sia un Date
		this.node = _node;

		this.updateTimer = () => {
			const now = new Date();
			const diff = this.targetDate - now;

			if (diff <= 0) {
				this.node.textContent = "Expired: " + prettyDate(this.targetDate);
				clearInterval(this.timerInterval);
				return;
			}

			// Calcoli
			const days = Math.floor(diff / (1000 * 60 * 60 * 24));
			const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
			const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
			const seconds = Math.floor((diff % (1000 * 60)) / 1000);

			// Funzione per formattare a due cifre
			const pad = (n) => String(n).padStart(2, "0");

			this.node.textContent =
				days + "d " + pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
		};

		// Primo update
		this.updateTimer();
		// Aggiorna ogni secondo
		this.timerInterval = setInterval(this.updateTimer, 1000);
	}
}
