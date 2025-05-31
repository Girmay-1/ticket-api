import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
  CircularProgress,
  Alert,
  Chip
} from '@mui/material';
import { CalendarToday, LocationOn } from '@mui/icons-material';

const API_BASE_URL = 'http://localhost:8081/api';

const EventsList = ({ onSelectEvent }) => {
  const navigate = useNavigate();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      setLoading(true);
      setError('');
      
      console.log('Fetching events from:', `${API_BASE_URL}/events`);
      
      const response = await fetch(`${API_BASE_URL}/events`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      console.log('Response status:', response.status);
      console.log('Response ok:', response.ok);

      if (response.ok) {
        const data = await response.json();
        setEvents(Array.isArray(data) ? data : []);
      } else {
        setError('Failed to fetch events');
      }
    } catch (err) {
      setError('Network error. Please make sure the backend is running.');
      console.error('Events fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Date TBD';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
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

  const handleEventClick = (event) => {
    // Call the parent callback to update selected event
    if (onSelectEvent) onSelectEvent(event);
    // Navigate to event details
    navigate(`/events/${event.id}`, { state: { selectedEvent: event } });
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading events...
        </Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h3" component="h1" gutterBottom>
          🎫 Available Events
        </Typography>
        <Button 
          variant="outlined" 
          onClick={() => navigate('/')}
        >
          ← Back to Home
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
          <Button onClick={fetchEvents} sx={{ ml: 2 }}>
            Retry
          </Button>
        </Alert>
      )}

      {events.length === 0 && !loading && !error && (
        <Box textAlign="center" sx={{ mt: 8 }}>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            No events available at the moment
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Check back soon for exciting events!
          </Typography>
          <Button 
            variant="contained" 
            onClick={fetchEvents}
            sx={{ mt: 2 }}
          >
            Refresh
          </Button>
        </Box>
      )}

      <Grid container spacing={3}>
        {events.map((event) => (
          <Grid item xs={12} md={6} lg={4} key={event.id}>
            <Card 
              elevation={3} 
              sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 6
                }
              }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="h5" component="h2" gutterBottom>
                  {event.name || 'Unnamed Event'}
                </Typography>
                
                <Typography 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ mb: 2, minHeight: '60px' }}
                >
                  {event.description || 'No description available'}
                </Typography>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <CalendarToday fontSize="small" sx={{ mr: 1, color: 'primary.main' }} />
                  <Typography variant="body2">
                    {formatDate(event.dateTime)}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <LocationOn fontSize="small" sx={{ mr: 1, color: 'primary.main' }} />
                  <Typography variant="body2">
                    {event.venue || 'Location TBD'}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Typography variant="body2" color="success.main" fontWeight="bold">
                    Free Event
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  {event.availableTickets > 0 ? (
                    <Chip 
                      label={`${event.availableTickets} tickets left`} 
                      color="success" 
                      size="small"
                    />
                  ) : (
                    <Chip 
                      label="Sold Out" 
                      color="error" 
                      size="small"
                    />
                  )}
                  
                  {event.category && (
                    <Chip 
                      label={event.category} 
                      variant="outlined" 
                      size="small"
                    />
                  )}
                </Box>
              </CardContent>

              <CardActions sx={{ p: 2, pt: 0 }}>
                <Button 
                  size="small" 
                  variant="outlined"
                  onClick={() => handleEventClick(event)}
                >
                  View Details
                </Button>
                <Button 
                  size="small" 
                  variant="contained"
                  disabled={!event.availableTickets || event.availableTickets <= 0}
                  onClick={() => handleEventClick(event)}
                >
                  {event.availableTickets > 0 ? 'Buy Tickets' : 'Sold Out'}
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export default EventsList;