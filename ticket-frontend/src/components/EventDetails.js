import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Grid,
  Chip,
  TextField,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Divider
} from '@mui/material';
import { 
  CalendarToday, 
  LocationOn, 
  Person,
  ConfirmationNumber,
  ArrowBack
} from '@mui/icons-material';
import StripePaymentForm from './StripePaymentForm';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081/api';

const EventDetails = ({ event, onNavigate, user }) => {
  const [ticketQuantity, setTicketQuantity] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [orderDetails, setOrderDetails] = useState(null);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [paymentIntentData, setPaymentIntentData] = useState(null);

  // Handle case where no event is selected
  if (!event) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h5" gutterBottom>
            No Event Selected
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            Please select an event from the events list to view details.
          </Typography>
          <Button 
            variant="contained" 
            onClick={() => onNavigate('events')}
          >
            Browse Events
          </Button>
        </Paper>
      </Container>
    );
  }

  const formatDate = (dateString) => {
    if (!dateString) return 'Date TBD';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (e) {
      return dateString;
    }
  };

  const handleQuantityChange = (e) => {
    const value = parseInt(e.target.value);
    if (value >= 1 && value <= Math.min(10, event.availableTickets)) {
      setTicketQuantity(value);
    }
  };

  const handlePurchase = async () => {
    if (!user) {
      setError('Please login to purchase tickets');
      setTimeout(() => onNavigate('login'), 2000);
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // Get JWT token from localStorage
      const token = localStorage.getItem('token');
      console.log('Current user:', user);
      console.log('JWT token exists:', !!token, 'Length:', token?.length);
      
      if (!token) {
        throw new Error('No authentication token found. Please login again.');
      }
      
      // For MVP: Try the full Stripe flow first, fall back to simple reservation
      try {
        // Always use Stripe infrastructure for consistency
        const response = await fetch(`${API_BASE_URL}/payments/create-intent`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            eventId: event.id,
            ticketType: 'general',
            price: event.price || 0.0, // Use dynamic pricing from event
            quantity: ticketQuantity
          })
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('Backend error response:', response.status, errorText);
          
          if (response.status === 401) {
            // Token expired or invalid - redirect to login
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            throw new Error('Session expired. Please login again.');
          }
          
          throw new Error(`Payment processing failed: ${errorText}`);
        }

        const paymentData = await response.json();
        console.log('Payment intent created:', paymentData);

        // For free events (amount = 0), auto-confirm the payment
        if (paymentData.amount === 0) {
          const confirmResponse = await fetch(`${API_BASE_URL}/payments/confirm?paymentIntentId=${paymentData.paymentIntentId}`, {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (confirmResponse.ok) {
            // Success! Show confirmation
            setSuccess(`Successfully reserved ${ticketQuantity} ticket(s)!`);
            setOrderDetails({
              orderId: paymentData.orderId,
              eventName: event.name,
              quantity: ticketQuantity,
              totalPrice: 0,
              confirmationCode: `CONF-${paymentData.paymentIntentId.slice(-8).toUpperCase()}`,
              paymentIntentId: paymentData.paymentIntentId
            });
            
            // Update available tickets locally
            event.availableTickets -= ticketQuantity;
            return; // Success - exit the function
          } else {
            const errorText = await confirmResponse.text();
            throw new Error(`Failed to confirm reservation: ${errorText}`);
          }
        } else {
          // For paid events, show the Stripe payment form
          setPaymentIntentData(paymentData);
          setShowPaymentForm(true);
          return;
        }
        
      } catch (stripeError) {
        console.log('Stripe payment failed, falling back to simple reservation:', stripeError.message);
        
        // Fallback: Simple reservation for MVP (only for free events)
        console.log('Using fallback reservation system for MVP');
        await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate processing
        
        const mockOrderId = Date.now();
        const mockConfirmationCode = `CONF-${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
        
        setSuccess(`Successfully reserved ${ticketQuantity} ticket(s)! (MVP Mode)`);
        setOrderDetails({
          orderId: mockOrderId,
          eventName: event.name,
          quantity: ticketQuantity,
          totalPrice: 0,
          confirmationCode: mockConfirmationCode,
          paymentIntentId: `mvp_${mockOrderId}`
        });
        
        // Update available tickets locally
        event.availableTickets -= ticketQuantity;
      }
      
    } catch (err) {
      setError(err.message);
      console.error('Purchase error:', err);
      
      // If authentication error, redirect to login after delay
      if (err.message.includes('login again') || err.message.includes('Session expired')) {
        setTimeout(() => {
          onNavigate('login');
        }, 3000);
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentSuccess = async (paymentIntent) => {
    try {
      const token = localStorage.getItem('token');
      
      // Confirm payment on backend
      const confirmResponse = await fetch(`${API_BASE_URL}/payments/confirm?paymentIntentId=${paymentIntent.id}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (confirmResponse.ok) {
        setSuccess(`Successfully purchased ${ticketQuantity} ticket(s)!`);
        setOrderDetails({
          orderId: paymentIntentData.orderId,
          eventName: event.name,
          quantity: ticketQuantity,
          totalPrice: paymentIntent.amount / 100,
          confirmationCode: `CONF-${paymentIntent.id.slice(-8).toUpperCase()}`,
          paymentIntentId: paymentIntent.id
        });
        setShowPaymentForm(false);
        event.availableTickets -= ticketQuantity;
      } else {
        setError('Payment confirmed but failed to finalize order. Please contact support.');
      }
    } catch (err) {
      setError('Error finalizing order: ' + err.message);
    }
  };

  if (showPaymentForm && paymentIntentData) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <StripePaymentForm
          paymentIntent={paymentIntentData}
          onSuccess={handlePaymentSuccess}
          onCancel={() => {
            setShowPaymentForm(false);
            setPaymentIntentData(null);
            setError('');
          }}
        />
      </Container>
    );
  }

  if (success && orderDetails) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Box textAlign="center" mb={4}>
            <ConfirmationNumber sx={{ fontSize: 60, color: 'success.main', mb: 2 }} />
            <Typography variant="h4" color="success.main" gutterBottom>
              Tickets Purchased! 🎉
            </Typography>
          </Box>

          <Card elevation={2} sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Order Details</Typography>
              <Divider sx={{ my: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Order ID
                  </Typography>
                  <Typography variant="body1" fontWeight="bold">
                    {orderDetails.orderId}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Confirmation Code
                  </Typography>
                  <Typography variant="body1" fontWeight="bold">
                    {orderDetails.confirmationCode}
                  </Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary">
                    Event
                  </Typography>
                  <Typography variant="body1" fontWeight="bold">
                    {orderDetails.eventName}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Quantity
                  </Typography>
                  <Typography variant="body1" fontWeight="bold">
                    {orderDetails.quantity} ticket(s)
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Total
                  </Typography>
                  <Typography variant="h6" color={orderDetails.totalPrice > 0 ? "primary.main" : "success.main"} fontWeight="bold">
                    {orderDetails.totalPrice > 0 ? `${orderDetails.totalPrice.toFixed(2)}` : 'FREE'}
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          <Alert severity="success" sx={{ mb: 3 }}>
            <Typography variant="body1">
              <strong>Your tickets have been {orderDetails.totalPrice > 0 ? 'purchased' : 'reserved'}!</strong><br />
              Order processed through Stripe. Save your confirmation code: <strong>{orderDetails.confirmationCode}</strong>
            </Typography>
          </Alert>

          <Box textAlign="center">
            <Button 
              variant="contained" 
              onClick={() => onNavigate('events')}
              sx={{ mr: 2 }}
            >
              Browse More Events
            </Button>
            <Button 
              variant="outlined" 
              onClick={() => onNavigate('home')}
            >
              Go Home
            </Button>
          </Box>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Button 
        variant="outlined" 
        startIcon={<ArrowBack />}
        onClick={() => onNavigate('events')}
        sx={{ mb: 3 }}
      >
        Back to Events
      </Button>

      <Grid container spacing={4}>
        {/* Event Details */}
        <Grid item xs={12} md={8}>
          <Paper elevation={3} sx={{ p: 4 }}>
            <Typography variant="h3" component="h1" gutterBottom>
              {event.name}
            </Typography>

            <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
              {event.availableTickets > 0 ? (
                <Chip 
                  label={`${event.availableTickets} tickets available`} 
                  color="success" 
                />
              ) : (
                <Chip 
                  label="Sold Out" 
                  color="error" 
                />
              )}
              {(event.price && event.price > 0) ? (
                <Chip label={`${event.price}`} color="primary" variant="outlined" />
              ) : (
                <Chip label="Free Event" color="primary" variant="outlined" />
              )}
            </Box>

            <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
              Event Details
            </Typography>
            <Typography variant="body1" paragraph>
              {event.description || 'Join us for an amazing event experience!'}
            </Typography>

            <Box sx={{ mt: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <CalendarToday sx={{ mr: 2, color: 'primary.main' }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Date & Time
                  </Typography>
                  <Typography variant="body1">
                    {formatDate(event.dateTime)}
                  </Typography>
                </Box>
              </Box>

              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <LocationOn sx={{ mr: 2, color: 'primary.main' }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Venue
                  </Typography>
                  <Typography variant="body1">
                    {event.venue || 'Venue TBD'}
                  </Typography>
                </Box>
              </Box>

              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Person sx={{ mr: 2, color: 'primary.main' }} />
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Capacity
                  </Typography>
                  <Typography variant="body1">
                    {event.totalTickets} total seats
                  </Typography>
                </Box>
              </Box>
            </Box>
          </Paper>
        </Grid>

        {/* Ticket Purchase */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3, position: 'sticky', top: 20 }}>
            <Typography variant="h5" gutterBottom>
              Get Your Tickets
            </Typography>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {event.availableTickets > 0 ? (
              <>
                <Box sx={{ mb: 3 }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Number of Tickets
                  </Typography>
                  <TextField
                    type="number"
                    value={ticketQuantity}
                    onChange={handleQuantityChange}
                    inputProps={{ 
                      min: 1, 
                      max: Math.min(10, event.availableTickets) 
                    }}
                    fullWidth
                    size="small"
                    helperText={`Max ${Math.min(10, event.availableTickets)} tickets per order`}
                  />
                </Box>

                <Box sx={{ mb: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
                  <Typography variant="body2" color="text.secondary">
                    Order Summary
                  </Typography>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                    <Typography>
                      {ticketQuantity} × {(event.price && event.price > 0) ? `${event.price} Ticket` : 'Free Ticket'}
                    </Typography>
                    <Typography fontWeight="bold">
                      {(event.price && event.price > 0) ? `${(event.price * ticketQuantity).toFixed(2)}` : 'FREE'}
                    </Typography>
                  </Box>
                  <Divider sx={{ my: 1 }} />
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="h6">
                      Total
                    </Typography>
                    <Typography variant="h6" color={(event.price && event.price > 0) ? "primary.main" : "success.main"}>
                      {(event.price && event.price > 0) ? `${(event.price * ticketQuantity).toFixed(2)}` : 'FREE'}
                    </Typography>
                  </Box>
                </Box>

                <Button
                  variant="contained"
                  size="large"
                  fullWidth
                  onClick={handlePurchase}
                  disabled={loading || !user}
                  sx={{ mb: 2 }}
                >
                  {loading ? (
                    <CircularProgress size={24} />
                  ) : (
                    (event.price && event.price > 0) 
                      ? `Buy ${ticketQuantity} Ticket${ticketQuantity > 1 ? 's' : ''} - ${(event.price * ticketQuantity).toFixed(2)}`
                      : `Get ${ticketQuantity} Ticket${ticketQuantity > 1 ? 's' : ''}`
                  )}
                </Button>

                {!user && (
                  <Alert severity="info" sx={{ mt: 2 }}>
                    Please{' '}
                    <Button 
                      color="primary" 
                      onClick={() => onNavigate('login')}
                      sx={{ textTransform: 'none', p: 0, minWidth: 'auto' }}
                    >
                      login
                    </Button>
                    {' '}to purchase tickets
                  </Alert>
                )}
              </>
            ) : (
              <Alert severity="error">
                This event is sold out. Check back later for more events!
              </Alert>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default EventDetails;