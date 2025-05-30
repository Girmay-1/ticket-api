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

const Register = ({ onNavigate }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    confirmPassword: ''
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

  const validateForm = () => {
    if (!formData.username || !formData.password || !formData.email) {
      setError('All fields are required');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return false;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: formData.username,
          password: formData.password,
          email: formData.email
        })
      });

      const data = await response.json();

      if (response.ok) {
        setSuccess('Registration successful! You can now login with your credentials.');
        
        // Clear form
        setFormData({
          username: '',
          password: '',
          email: '',
          confirmPassword: ''
        });
        
        // Navigate to login after 2 seconds
        setTimeout(() => {
          onNavigate('login');
        }, 2000);
        
      } else {
        setError(data.message || data || 'Registration failed. Please try again.');
      }
    } catch (err) {
      setError('Network error. Please make sure the backend is running on port 8081.');
      console.error('Registration error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box textAlign="center" mb={3}>
          <Typography variant="h4" component="h1" gutterBottom>
            🎫 Create Account
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Join TicketMaster to book amazing events
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
            helperText="Choose a unique username"
          />

          <TextField
            fullWidth
            label="Email"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            margin="normal"
            required
            disabled={loading}
            helperText="We'll use this for booking confirmations"
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
            helperText="Minimum 6 characters"
          />

          <TextField
            fullWidth
            label="Confirm Password"
            name="confirmPassword"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
            margin="normal"
            required
            disabled={loading}
            helperText="Re-enter your password"
          />

          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 3, mb: 2 }}
          >
            {loading ? <CircularProgress size={24} /> : 'Create Account'}
          </Button>
        </form>

        <Box textAlign="center" mt={2}>
          <Typography variant="body2">
            Already have an account?{' '}
            <Button 
              color="primary" 
              onClick={() => onNavigate('login')}
              sx={{ textTransform: 'none' }}
            >
              Sign in here
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

export default Register;