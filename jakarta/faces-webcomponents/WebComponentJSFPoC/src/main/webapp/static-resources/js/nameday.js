class NamedayElement extends HTMLElement {

    constructor() {
        super();
        this.nameDay = 'loading...';
        
        // Optional: Attach a shadow DOM
        this.attachShadow({ mode: 'open' });
    }

    static get observedAttributes() {
        console.log("observedAttributes()");
        return ['date', 'update', 'dateDesc', 'nameDay'];
    }

    // Called when an attribute is added, removed, or changed
    attributeChangedCallback(name, oldValue, newValue) {
        console.log("attributeChangedCallback() -- name: "
                +name
                +", old: '"
                +oldValue
                +"', new: '"
                +newValue
                +"'");
        if(name==="date") {
            this.date = newValue;
        }
        if(name==="update") {
            this.update = newValue;
        }
        this.render();
    }

    // Called when the element is added to the document
    connectedCallback() {
        //super.connectedCallback();
        console.log("connectedCallback()");
        this.getModel().then(res => {
            // wait 2 seconds to see the change
            setTimeout(() => {
                // what I get from the rest
                console.log(res);
                this.nameDay = res[0].name;
                console.log("connectedCallback(): nameDay is '" + this.nameDay + "'");
                this.render();
                if(this.update !== undefined) {
                    document.getElementById(this.update).value = this.nameDay;
                    // alert(this.update);
                }
            }, 2000);
        });
        this.render();
    }

    // Called when the element is removed from the document
    disconnectedCallback() {
        // Cleanup if necessary
    }

    async getModel() {
        var url = "https://svatky.adresa.info/json";
        if (this.date === undefined) {
            this.dateDesc = "Today";
        } else {
            this.dateDesc = parseInt(this.date.substring(0, 2)) + ". " + parseInt(this.date.substring(2, 4)) + ".";
            url = url + "?date=" + this.date;
        }
        var response = await fetch(url);
        return response.json();
    }

    render() {
        console.log("render()");
        this.shadowRoot.innerHTML = `
                <style>
                    .emph { color: green; }
                </style>
                ${this.dateDesc} is the nameday for <span class='emph'>${this.nameDay}</span>`;
    }
}
customElements.define('nameday-element', NamedayElement);
