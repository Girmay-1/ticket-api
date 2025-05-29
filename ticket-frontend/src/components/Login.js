import React, { useState } from 'react';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress
} from '@mui/material';

const API_BASE_URL = 'http://localhost:8081/api';

const Login = ({ onLogin, onNavigate }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (response.ok) {
        // Store JWT token
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user || { username: formData.username }));
        
        setSuccess('Login successful!');
        
        // Call parent callback if provided
        if (onLogin) {
          onLogin(data);
        }
        
        // Navigate to events or home
        setTimeout(() => {
          onNavigate('events');
        }, 1000);
        
      } else {
        setError(data.message || 'Login failed. Please check your credentials.');
      }
    } catch (err) {
      setError('Network error. Please make sure the backend is running on port 8081.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box textAlign="center" mb={3}>
          <Typography variant="h4" component="h1" gutterBottom>
            🎫 Login
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Access your TicketMaster account
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }}>
            {success}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="Username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            margin="normal"
            required
            disabled={loading}
          />

          <TextField
            fullWidth
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            margin="normal"
            required
            disabled={loading}
          />

          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 3, mb: 2 }}
          >
            {loading ? <CircularProgress size={24} /> : 'Login'}
          </Button>
        </form>

        <Box textAlign="center" mt={2}>
          <Typography variant="body2">
            Don't have an account?{' '}
            <Button 
              color="primary" 
              onClick={() => onNavigate('register')}
              sx={{ textTransform: 'none' }}
            >
              Register here
            </Button>
          </Typography>
        </Box>

        <Box textAlign="center" mt={2}>
          <Button 
            variant="outlined" 
            onClick={() => onNavigate('home')}
            size="small"
          >
            ← Back to Home
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default Login;