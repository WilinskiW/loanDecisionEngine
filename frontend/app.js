document.addEventListener('DOMContentLoaded', () => {
    const sliders = document.querySelectorAll('.slider');
    const amountSlider = sliders[0];
    const periodSlider = sliders[1];

    const inputs = document.querySelectorAll('.field-input-wrapper input');
    const amountInput = inputs[0];
    const periodInput = inputs[1];


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

    const makeRequest = (sliders, inputs) => {
        fetch('http://localhost:8080/api/offer', {
            method: 'POST',
            body: JSON.stringify({
                    amount: sliders[0].value,
                    period: sliders[1].value,
                    personalCode: inputs[2].value
                }
            ),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }})
            .then(response => response.json())
            .then(data => console.log(data))
    }

        setupSync(amountSlider, amountInput);
        setupSync(periodSlider, periodInput);

        updateSliderFill(amountSlider);
        updateSliderFill(periodSlider);

        const loanButton = document.querySelector('.form-button');
        if (loanButton) {
            loanButton.addEventListener('click', (e) => {
                e.preventDefault();
                makeRequest(sliders, inputs);
            });
        }
    }
);