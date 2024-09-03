class BlinkyNewthing extends HTMLElement {
	
	static get observedAttributes() {
		// Watched component attributes.
		return ['noise', 'stuff'];
	}
	
	constructor() {
		super();
	}
	
	connectedCallback() {
		console.log('connectedCallback');
		
		this.classList.add('js__newthing');
		
		// Setup the component.
		const el = document.createElement('span');
		el.setAttribute('class', 'el');
		el.setAttribute('style', 'border: dotted 2px currentColor; padding: 3px;');
		this.append(el);
		el.innerHTML = this.getAttribute('noise');

		const btn = document.createElement('button');
		btn.id = 'stuff-btn';
		btn.setAttribute('type', 'button');
		btn.innerText = 'Change to Baa';
		this.append(btn);
		btn.addEventListener('click', () => {
			this.setAttribute('noise', 'baa');
		});
	}
	
	disconnectedCallback() {
		console.log('disconnectedCallback');
	}
	
	attributeChangedCallback(att, old_v, new_v) {
		
		if ((!this.classList.contains('js__newthing')) || (old_v === new_v)) return false;
		
		console.log('attributeChangedCallback: '+att+', old: '+old_v+', new: '+new_v);
		
		if (att == "noise") {
			this.querySelector('.el').innerHTML = new_v;
		}
	}
}

customElements.define('blinky-newthing', BlinkyNewthing);