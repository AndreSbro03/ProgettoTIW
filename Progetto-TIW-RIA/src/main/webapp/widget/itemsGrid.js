/**
 * Items Grid widget
 */
{
	function ItemsGrid(_items, _startingNode) {
		this.items = _items;
		this.startingNode = _startingNode;

		this.show = function() {
			var self = this;
			self.items.forEach((item) => {
				var ip = self.startingNode;
				ip = addNode(ip, "div", "product-card");
				var image = addNode(ip, "img", "product-image");
				image.height = 150;
				image.src = "images?itemId=" + item.id;
				ip = addNode(ip, "div", "padding");
				addNode(ip, "h2", "product-name", item.name);
				addNode(ip, "p", "product-description", item.descr);
				addNode(ip, "p", "product-description", item.price);
			});
		}

	}
}