import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Container, Typography, Box, Button, Stack, Grid, Card, CardContent, AppBar, Toolbar } from '@mui/material';
import { Event as EventIcon, ConfirmationNumber, LocalActivity } from '@mui/icons-material';
import Login from './components/Login';
import Register from './components/Register';
import EventsList from './components/EventsList';
import EventDetails from './components/EventDetails';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

// Navigation component that uses useNavigate hook
function AppContent() {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [user, setUser] = useState(() => {
    // Check if user is stored in localStorage on app load
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [selectedEvent, setSelectedEvent] = useState(() => {
    // Try to get selected event from location state
    return location.state?.selectedEvent || null;
  });

  const handleLogin = (userData) => {
    setUser(userData);
    // Store user data in localStorage
    localStorage.setItem('user', JSON.stringify(userData));
    console.log('User logged in:', userData);
    // Navigate to events page after login
    navigate('/events');
  };

  const handleSelectEvent = (event) => {
    setSelectedEvent(event);
    console.log('Selected event:', event);
    // Navigate to event details with the event data in state
    navigate(`/events/${event.id}`, { state: { selectedEvent: event } });
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setSelectedEvent(null);
    navigate('/');
  };

  // Home Page Component
  const HomePage = () => (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box textAlign="center" mb={6}>
        <Typography variant="h2" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
          🎫 Event Tickets
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom color="text.secondary">
          {user ? `Welcome back, ${user.username}!` : 'Your gateway to amazing events'}
        </Typography>
        <Stack direction="row" spacing={2} justifyContent="center" sx={{ mt: 4 }}>
          {!user ? (
            <>
              <Button 
                variant="contained" 
                size="large"
                onClick={() => navigate('/login')}
              >
                Login
              </Button>
              <Button 
                variant="outlined" 
                size="large"
                onClick={() => navigate('/register')}
              >
                Register
              </Button>
            </>
          ) : (
            <Button 
              variant="outlined" 
              onClick={handleLogout}
            >
              Logout
            </Button>
          )}
          <Button 
            variant={user ? "contained" : "outlined"} 
            size="large"
            startIcon={<EventIcon />}
            onClick={() => navigate('/events')}
          >
            Browse Events
          </Button>
        </Stack>
      </Box>

      <Grid container spacing={4}>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent sx={{ textAlign: 'center', py: 4 }}>
              <EventIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                Discover Events
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Browse through our curated selection of concerts, conferences, and exhibitions
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent sx={{ textAlign: 'center', py: 4 }}>
              <ConfirmationNumber sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                Secure Booking
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Easy and secure ticket purchasing with Stripe payment processing
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card elevation={2}>
            <CardContent sx={{ textAlign: 'center', py: 4 }}>
              <LocalActivity sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h6" gutterBottom>
                Instant Confirmation
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Get your tickets instantly with unique confirmation codes
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );

  return (
    <>
      <AppBar position="static" elevation={1}>
        <Toolbar>
          <Typography 
            variant="h6" 
            sx={{ flexGrow: 1, cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            🎫 Event Tickets
          </Typography>
          {user ? (
            <>
              <Button color="inherit" onClick={() => navigate('/events')}>
                Events
              </Button>
              <Button 
                color="inherit" 
                onClick={handleLogout}
              >
                Logout ({user.username})
              </Button>
            </>
          ) : (
            <>
              <Button color="inherit" onClick={() => navigate('/login')}>
                Login
              </Button>
              <Button color="inherit" onClick={() => navigate('/register')}>
                Register
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route 
          path="/login" 
          element={<Login onLogin={handleLogin} />} 
        />
        <Route 
          path="/register" 
          element={<Register />} 
        />
        <Route 
          path="/events" 
          element={<EventsList onSelectEvent={handleSelectEvent} />} 
        />
        <Route 
          path="/events/:id" 
          element={<EventDetails event={selectedEvent} user={user} />} 
        />
      </Routes>
    </>
  );
}

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <AppContent />
      </Router>
    </ThemeProvider>
  );
}

export default App;