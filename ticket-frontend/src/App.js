import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import Header from './components/Header';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import EventList from './components/EventList';
import EventDetails from './components/EventDetails';
import UserProfile from './components/UserProfile';

const theme = createTheme();

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Header />
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/login" component={Login} />
          <Route path="/register" component={Register} />
          <Route exact path="/events" component={EventList} />
          <Route path="/events/:id" component={EventDetails} />
          <Route path="/profile" component={UserProfile} />
        </Switch>
      </Router>
    </ThemeProvider>
  );
}

export default App;