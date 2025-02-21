const stripe = Stripe('pk_test_51QrLcZFTCZWykylGpIi2hPHdz9u2ghyWWzRuteR61XdcrN4cy9MSV1OSw773qbh8UCHMopOdFxmMrLA8xU0jvaFY00LhsTGF9L');
let elements;

const paymentForm = document.getElementById('payment-form');
const paymentButton = document.getElementById('payment-button');
const messageDiv = document.getElementById('payment-message');

async function initialize() {
    try {
        console.log('Fetching PaymentIntent...');
        const response = await fetch('/api/payment/create-payment-intent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${AUTH_TOKEN}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const {clientSecret} = await response.json();
        console.log('clientSecret:', clientSecret);

        elements = stripe.elements({clientSecret});
        const paymentElement = elements.create('payment');
        paymentElement.mount('#payment-element');

        paymentButton.disabled = false;
    } catch (error) {
        messageDiv.textContent = 'Error initializing payment. Please try again.';
        console.error('Error:', error);
    }
}

async function handlePayment(e) {
    e.preventDefault();
    setLoading(true);

    try {
        await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: 'http://localhost:8080/payment-success.html',
            },
            redirect: 'if_required'
        });

    } catch (error) {
        messageDiv.textContent = 'An unexpected error occurred.';
        console.error('Error:', error);
    }

    setLoading(false);
}

function setLoading(isLoading) {
    paymentButton.disabled = isLoading;
    paymentButton.textContent = isLoading ? 'Processing...' : 'Pay Now';
}

if (paymentForm) {
    paymentForm.addEventListener('submit', handlePayment);
    initialize();
} else {
    console.error('Payment form not found!');
}
