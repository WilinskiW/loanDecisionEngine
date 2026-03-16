class Decision {
    constructor(outcome, amount) {
        this.outcome = outcome;
        this.amount = amount;
    }
    getOutcome() { return this.outcome; }
    getAmount() { return this.amount; }
}

document.addEventListener('DOMContentLoaded', () => {
    const sliders = document.querySelectorAll('.slider');
    const inputs = document.querySelectorAll('.field-input-wrapper input');

    const calculatorView = document.querySelector('.calculator-view');
    const resultView = document.querySelector('.result-view');
    const declinedView = document.querySelector('.declined-view');

    const resAmountSpan = document.getElementById('res-amount');
    const resPeriodSpan = document.getElementById('res-period');

    const toggleView = (viewName) => {
        calculatorView.classList.add('hidden');
        resultView.classList.add('hidden');
        declinedView.classList.add('hidden');

        if (viewName === 'calc') calculatorView.classList.remove('hidden');
        if (viewName === 'pos') resultView.classList.remove('hidden');
        if (viewName === 'neg') declinedView.classList.remove('hidden');
    };

    const updateSliderFill = (slider) => {
        const val = slider.value;
        const pct = (val - slider.min) / (slider.max - slider.min) * 100;
        slider.style.background = `linear-gradient(to right, #300E54 ${pct}%, #CCBDE5 ${pct}%)`;
    };

    const setupSync = (slider, input) => {
        slider.addEventListener('input', (e) => {
            input.value = e.target.value;
            updateSliderFill(slider);
        });
        input.addEventListener('input', (e) => {
            let value = parseFloat(e.target.value);
            if (value > input.max) e.target.value = input.max;
            if (!isNaN(value) && value >= input.min && value <= input.max) {
                slider.value = value;
                updateSliderFill(slider);
            }
        });
        input.addEventListener('blur', (e) => {
            let value = parseFloat(e.target.value);
            if (isNaN(value) || value < input.min) e.target.value = input.min;
            else if (value > input.max) e.target.value = input.max;
            slider.value = e.target.value;
            updateSliderFill(slider);
        });
    };

    const makeRequest = () => {
        const payload = {
            amount: sliders[0].value,
            period: sliders[1].value,
            personalCode: inputs[2].value
        };

        fetch('http://localhost:8080/api/offer', {
            method: 'POST',
            body: JSON.stringify(payload),
            headers: { "Content-type": "application/json; charset=UTF-8" }
        })
            .then(response => {
                if (!response.ok) throw new Error('Server error');
                return response.json();
            })
            .then(data => {
                const decision = new Decision(data.outcome, data.amount);
                if (decision.getOutcome() === 'POSITIVE') {
                    resAmountSpan.textContent = decision.getAmount().toLocaleString();
                    resPeriodSpan.textContent = sliders[1].value;
                    toggleView('pos');
                } else {
                    toggleView('neg');
                }
            })
            .catch(error => {
                console.error(error);
                alert("Connection error. Check CORS settings.");
            });
    };

    setupSync(sliders[0], inputs[0]);
    setupSync(sliders[1], inputs[1]);
    updateSliderFill(sliders[0]);
    updateSliderFill(sliders[1]);

    document.querySelector('.form-button').addEventListener('click', (e) => {
        e.preventDefault();
        makeRequest();
    });

    document.querySelectorAll('.back-button, .back-to-calc').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleView('calc');
        });
    });

    document.querySelector('.accept-offer').addEventListener('click', (e) => {
        e.preventDefault();
        alert("Congratulations! Offer accepted!");
    })
});