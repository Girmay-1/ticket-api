import React, { useState } from 'react';
import {
  CardElement,
  Elements,
  useStripe,
  useElements
} from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';
import {
  Box,
  Button,
  CircularProgress,
  Alert,
  Paper,
  Typography
} from '@mui/material';

// Initialize Stripe with publishable key from environment variable
const publishableKey = process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || 'pk_test_YOUR_KEY_HERE';
console.log('Stripe publishable key:', publishableKey.substring(0, 20) + '...'); // Log first 20 chars for debugging
const stripePromise = loadStripe(publishableKey);

const PaymentForm = ({ paymentIntent, onSuccess, onCancel }) => {
  const stripe = useStripe();
  const elements = useElements();
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setProcessing(true);
    setError(null);

    try {
      const result = await stripe.confirmCardPayment(paymentIntent.clientSecret, {
        payment_method: {
          card: elements.getElement(CardElement),
        }
      });

      if (result.error) {
        setError(result.error.message);
      } else {
        onSuccess(result.paymentIntent);
      }
    } catch (err) {
      setError('An unexpected error occurred.');
      console.error('Payment error:', err);
    } finally {
      setProcessing(false);
    }
  };

  const cardStyle = {
    style: {
      base: {
        color: '#32325d',
        fontFamily: 'Arial, sans-serif',
        fontSmoothing: 'antialiased',
        fontSize: '16px',
        '::placeholder': {
          color: '#aab7c4'
        }
      },
      invalid: {
        color: '#fa755a',
        iconColor: '#fa755a'
      }
    }
  };

  return (
    <Paper elevation={3} sx={{ p: 3, maxWidth: 400, mx: 'auto', mt: 3 }}>
      <Typography variant="h6" gutterBottom>
        Complete Payment
      </Typography>
      <Typography variant="body2" color="text.secondary" gutterBottom>
        Total: ${(paymentIntent.amount / 100).toFixed(2)}
      </Typography>
      
      <form onSubmit={handleSubmit}>
        <Box sx={{ mt: 3, mb: 2 }}>
          <CardElement options={cardStyle} />
        </Box>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            type="submit"
            variant="contained"
            disabled={!stripe || processing}
            fullWidth
          >
            {processing ? (
              <CircularProgress size={24} />
            ) : (
              `Pay $${(paymentIntent.amount / 100).toFixed(2)}`
            )}
          </Button>
          <Button
            variant="outlined"
            onClick={onCancel}
            disabled={processing}
          >
            Cancel
          </Button>
        </Box>
      </form>
      
      <Typography variant="caption" display="block" sx={{ mt: 2, textAlign: 'center' }}>
        Test card: 4242 4242 4242 4242
      </Typography>
    </Paper>
  );
};

const StripePaymentForm = (props) => {
  return (
    <Elements stripe={stripePromise}>
      <PaymentForm {...props} />
    </Elements>
  );
};

export default StripePaymentForm;
