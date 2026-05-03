import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

export default function Navbar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <span className="navbar-brand">FlowCart</span>
      <div className="navbar-links">
        <Link to="/products">Products</Link>
        <Link to="/my-orders">My Orders</Link>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}