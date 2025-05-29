import React, { useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Container, Typography, Box, Button, Stack } from '@mui/material';
import Login from './components/Login';
import EventsList from './components/EventsList';

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
  const [user, setUser] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState(null);

  const handleLogin = (userData) => {
    setUser(userData);
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
            <Box textAlign="center">
              <Typography variant="h2" component="h1" gutterBottom>
                🎫 TicketMaster MVP
              </Typography>
              <Typography variant="h5" component="h2" gutterBottom>
                {user ? `Welcome back, ${user.username || 'User'}!` : 'Backend Ready! Building Frontend Components...'}
              </Typography>
              <Stack direction="row" spacing={2} justifyContent="center" sx={{ mt: 4 }}>
                {!user ? (
                  <>
                    <Button variant="contained" onClick={() => setCurrentView('login')}>
                      Login
                    </Button>
                    <Button variant="outlined" onClick={() => setCurrentView('register')}>
                      Register
                    </Button>
                  </>
                ) : (
                  <Button variant="outlined" onClick={() => { 
                    setUser(null); 
                    localStorage.removeItem('token'); 
                    localStorage.removeItem('user'); 
                  }}>
                    Logout
                  </Button>
                )}
                <Button variant="outlined" onClick={() => setCurrentView('events')}>
                  View Events
                </Button>
              </Stack>
            </Box>
          </Container>
        );
      case 'login':
        return <Login onLogin={handleLogin} onNavigate={handleNavigate} />;
      case 'register':
        return (
          <Container maxWidth="sm" sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom>Register</Typography>
            <Typography>Register component coming next...</Typography>
            <Button sx={{ mt: 2 }} onClick={() => setCurrentView('home')}>Back to Home</Button>
          </Container>
        );
      case 'events':
        return (
          <EventsList 
            onNavigate={handleNavigate} 
            onSelectEvent={handleSelectEvent}
          />
        );
      default:
        return <Typography>Unknown view</Typography>;
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {renderView()}
    </ThemeProvider>
  );
}

export default App;