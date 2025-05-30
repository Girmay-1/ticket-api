import React, { useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Container, Typography, Box, Button, Stack, Paper, Grid, Card, CardContent, AppBar, Toolbar } from '@mui/material';
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

function App() {
  const [currentView, setCurrentView] = useState('home');
  const [user, setUser] = useState(() => {
    // Check if user is stored in localStorage on app load
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [selectedEvent, setSelectedEvent] = useState(null);

  const handleLogin = (userData) => {
    setUser(userData);
    // Store user data in localStorage
    localStorage.setItem('user', JSON.stringify(userData));
    console.log('User logged in:', userData);
  };

  const handleNavigate = (view) => {
    setCurrentView(view);
  };

  const handleSelectEvent = (event) => {
    setSelectedEvent(event);
    console.log('Selected event:', event);
  };

  const renderView = () => {
    switch(currentView) {
      case 'home':
        return (
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
                      onClick={() => setCurrentView('login')}
                    >
                      Login
                    </Button>
                    <Button 
                      variant="outlined" 
                      size="large"
                      onClick={() => setCurrentView('register')}
                    >
                      Register
                    </Button>
                  </>
                ) : (
                  <Button 
                    variant="outlined" 
                    onClick={() => { 
                      setUser(null); 
                      localStorage.removeItem('token'); 
                      localStorage.removeItem('user'); 
                    }}
                  >
                    Logout
                  </Button>
                )}
                <Button 
                  variant={user ? "contained" : "outlined"} 
                  size="large"
                  startIcon={<EventIcon />}
                  onClick={() => setCurrentView('events')}
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
      case 'login':
        return <Login onLogin={handleLogin} onNavigate={handleNavigate} />;
      case 'register':
        return <Register onNavigate={handleNavigate} />;
      case 'events':
        return (
          <EventsList 
            onNavigate={handleNavigate} 
            onSelectEvent={handleSelectEvent}
          />
        );
      case 'event-details':
        return (
          <EventDetails 
            event={selectedEvent} 
            onNavigate={handleNavigate} 
            user={user}
          />
        );
      default:
        return <Typography>Unknown view</Typography>;
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" elevation={1}>
        <Toolbar>
          <Typography 
            variant="h6" 
            sx={{ flexGrow: 1, cursor: 'pointer' }}
            onClick={() => setCurrentView('home')}
          >
            🎫 Event Tickets
          </Typography>
          {user ? (
            <>
              <Button color="inherit" onClick={() => setCurrentView('events')}>
                Events
              </Button>
              <Button 
                color="inherit" 
                onClick={() => { 
                  setUser(null); 
                  localStorage.removeItem('token'); 
                  localStorage.removeItem('user');
                  setCurrentView('home');
                }}
              >
                Logout ({user.username})
              </Button>
            </>
          ) : (
            <>
              <Button color="inherit" onClick={() => setCurrentView('login')}>
                Login
              </Button>
              <Button color="inherit" onClick={() => setCurrentView('register')}>
                Register
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      {renderView()}
    </ThemeProvider>
  );
}

export default App;