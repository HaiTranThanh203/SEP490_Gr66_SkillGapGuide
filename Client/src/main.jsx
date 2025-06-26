import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './styles/custom.css'
import './styles/global.css'


import App from './App.jsx'
import { UserProvider } from './context/UserContext.jsx';
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <UserProvider>
      <App />
    </UserProvider>
  </StrictMode>
)
