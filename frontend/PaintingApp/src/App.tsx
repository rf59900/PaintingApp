import "./App.css";
import { Home } from "./pages/Home";
import { Login } from "./pages/Login";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { RefreshKeepLogin } from "./components/RefreshKeepLogin";
import { Logout } from "./pages/Logout";
import { Paint } from "./pages/Paint";

const App = () => {
  return (
    <AuthProvider>
      <RefreshKeepLogin />
      <BrowserRouter>
        <Routes>
          <Route element={<RefreshKeepLogin />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/paint" element={<Paint />} />
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
