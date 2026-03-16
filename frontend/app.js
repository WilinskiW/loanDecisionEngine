class Decision {
    constructor(outcome, amount) {
        this.outcome = outcome;
        this.amount = amount;
    }

    getOutcome() {
        return this.outcome;
    }

    getAmount() {
        return this.amount;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const sliders = document.querySelectorAll('.slider');
    const amountSlider = sliders[0];
    const periodSlider = sliders[1];

    const inputs = document.querySelectorAll('.field-input-wrapper input');
    const amountInput = inputs[0];
    const periodInput = inputs[1];
    const personalCodeInput = inputs[2];

    const calculatorView = document.querySelector('.calculator-view');
    const resultView = document.querySelector('.result-view');
    const backButton = document.querySelector('.back-button');
    const loanButton = document.querySelector('.form-button');

    const resAmountSpan = document.getElementById('res-amount');
    const resPeriodSpan = document.getElementById('res-period');



    const toggleView = (showResult = true) => {
        if (showResult) {
            calculatorView.classList.add('hidden');
            resultView.classList.remove('hidden');
        } else {
            calculatorView.classList.remove('hidden');
            resultView.classList.add('hidden');
        }
    };

    const updateSliderFill = (slider) => {
        const val = slider.value;
        const min = slider.min;
        const max = slider.max;
        const pct = (val - min) / (max - min) * 100;
        slider.style.background = `linear-gradient(to right, #300E54 ${pct}%, #CCBDE5 ${pct}%)`;
    };


    const setupSync = (slider, input) => {
        slider.addEventListener('input', (e) => {
            input.value = e.target.value;
            updateSliderFill(slider);
        });

        input.addEventListener('input', (e) => {
            let value = parseFloat(e.target.value);
            const min = parseFloat(input.min);
            const max = parseFloat(input.max);

            if (value > max) {
                e.target.value = max;
                value = max;
            }

            if (!isNaN(value) && value >= min && value <= max) {
                slider.value = value;
                updateSliderFill(slider);
            }
        });

        input.addEventListener('blur', (e) => {
            let value = parseFloat(e.target.value);
            const min = parseFloat(input.min);
            const max = parseFloat(input.max);

            if (isNaN(value) || value < min) {
                e.target.value = min;
                slider.value = min;
            } else if (value > max) {
                e.target.value = max;
                slider.value = max;
            }
            updateSliderFill(slider);
        });
    };

    const makeRequest = () => {
        const payload = {
            amount: amountSlider.value,
            period: periodSlider.value,
            personalCode: personalCodeInput.value
        };

        fetch('http://localhost:8080/api/offer', {
            method: 'POST',
            body: JSON.stringify(payload),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('Błąd serwera');
                return response.json();
            })
            .then(data => {
                const decision = new Decision(data.outcome, data.amount);

                if (decision.getOutcome() === 'POSITIVE') {
                    resAmountSpan.textContent = decision.getAmount().toLocaleString();
                    resPeriodSpan.textContent = periodSlider.value;
                    toggleView(true);
                } else {
                    alert("Niestety, Twoja prośba o pożyczkę została odrzucona.");
                }
            })
            .catch(error => {
                console.error("Błąd podczas fetch:", error);
                alert("Nie udało się połączyć z serwerem. Sprawdź konsolę i CORS.");
            });
    };


    setupSync(amountSlider, amountInput);
    setupSync(periodSlider, periodInput);

    updateSliderFill(amountSlider);
    updateSliderFill(periodSlider);

    if (loanButton) {
        loanButton.addEventListener('click', (e) => {
            e.preventDefault();
            makeRequest();
        });
    }

    if (backButton) {
        backButton.addEventListener('click', (e) => {
            e.preventDefault();
            toggleView(false);
        });
    }
});