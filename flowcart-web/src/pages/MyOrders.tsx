import { useEffect, useState } from "react";
import api from "../api/axios";
import Navbar from "../components/Navbar";
import { getUserIdFromToken } from "../utils/jwt";
import "./MyOrders.css";
import type { Order } from "../types";

type StatusFilter = "ALL" | "PENDING" | "CONFIRMED" | "FAILED";

export default function MyOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [filter, setFilter] = useState<StatusFilter>("ALL");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userId = getUserIdFromToken();
    const endpoint = userId ? `/orders/user/${userId}` : "/orders";

    api.get(endpoint).then((res) => {
      setOrders(res.data);
      setLoading(false);
    });
  }, []);

  const filtered =
    filter === "ALL" ? orders : orders.filter((o) => o.status === filter);

  function statusClass(status: string) {
    if (status === "CONFIRMED") return "status confirmed";
    if (status === "FAILED") return "status failed";
    return "status pending";
  }

  if (loading) return <p className="loading">Loading orders...</p>;

  return (
    <>
      <Navbar />
      <div className="orders-container">
        <div className="orders-header">
          <h2>My Orders</h2>
          <div className="filter-buttons">
            {(["ALL", "PENDING", "CONFIRMED", "FAILED"] as StatusFilter[]).map(
              (s) => (
                <button
                  key={s}
                  className={`filter-btn ${filter === s ? "active" : ""}`}
                  onClick={() => setFilter(s)}
                >
                  {s}
                </button>
              )
            )}
          </div>
        </div>

        {filtered.length === 0 ? (
          <p className="empty">No orders found.</p>
        ) : (
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Product ID</th>
                <th>Quantity</th>
                <th>Total</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((order) => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>{order.productId}</td>
                  <td>{order.quantity}</td>
                  <td>${order.totalPrice.toFixed(2)}</td>
                  <td>
                    <span className={statusClass(order.status)}>
                      {order.status}
                    </span>
                  </td>
                  <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}