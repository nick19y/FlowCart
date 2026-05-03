import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import Navbar from "../components/Navbar";
import type { Product } from "../types";
import { getUserIdFromToken } from "../utils/jwt";
import "./Products.css";

interface ProductForm {
  name: string;
  description: string;
  price: string;
  stock: string;
}

const emptyForm: ProductForm = {
  name: "",
  description: "",
  price: "",
  stock: "",
};

export default function Products() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [ordering, setOrdering] = useState<number | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [form, setForm] = useState<ProductForm>(emptyForm);
  const [formError, setFormError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
  }, []);

  async function fetchProducts() {
    const res = await api.get("/products");
    setProducts(res.data);
    setLoading(false);
  }

  async function handleOrder(product: Product) {
    setOrdering(product.id);
    try {
      const userId = getUserIdFromToken();
      await api.post("/orders", {
        userId,
        productId: product.id,
        quantity: 1,
      });
      navigate("/my-orders");
    } catch {
      alert("Failed to place order.");
    } finally {
      setOrdering(null);
    }
  }

  function openCreateModal() {
    setEditingProduct(null);
    setForm(emptyForm);
    setFormError("");
    setShowModal(true);
  }

  function openEditModal(product: Product) {
    setEditingProduct(product);
    setForm({
      name: product.name,
      description: product.description,
      price: String(product.price),
      stock: String(product.stock),
    });
    setFormError("");
    setShowModal(true);
  }

  async function handleDelete(id: number) {
    if (!confirm("Are you sure you want to delete this product?")) return;
    await api.delete(`/products/${id}`);
    setProducts((prev) => prev.filter((p) => p.id !== id));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError("");

    const payload = {
      name: form.name,
      description: form.description,
      price: parseFloat(form.price),
      stock: parseInt(form.stock),
    };

    try {
      if (editingProduct) {
        const res = await api.put(`/products/${editingProduct.id}`, payload);
        setProducts((prev) =>
          prev.map((p) => (p.id === editingProduct.id ? res.data : p))
        );
      } else {
        const res = await api.post("/products", payload);
        setProducts((prev) => [...prev, res.data]);
      }
      setShowModal(false);
    } catch {
      setFormError("Failed to save product. Try again.");
    }
  }

  if (loading) return <p className="loading">Loading products...</p>;

  return (
    <>
      <Navbar />
      <div className="products-container">
        <div className="products-header">
          <h2>Products</h2>
          <button className="btn-create" onClick={openCreateModal}>
            + New Product
          </button>
        </div>

        <div className="products-grid">
          {products.map((product) => (
            <div key={product.id} className="product-card">
              <div className="product-actions">
                <button
                  className="btn-edit"
                  onClick={() => openEditModal(product)}
                >
                  Edit
                </button>
                <button
                  className="btn-delete"
                  onClick={() => handleDelete(product.id)}
                >
                  Delete
                </button>
              </div>
              <h3>{product.name}</h3>
              <p className="product-description">{product.description}</p>
              <div className="product-footer">
                <span className="product-price">
                  ${product.price.toFixed(2)}
                </span>
                <span className="product-stock">Stock: {product.stock}</span>
              </div>
              <button
                className="btn-order"
                onClick={() => handleOrder(product)}
                disabled={product.stock === 0 || ordering === product.id}
              >
                {ordering === product.id ? "Ordering..." : "Buy now"}
              </button>
            </div>
          ))}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>{editingProduct ? "Edit Product" : "New Product"}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Description</label>
                <input
                  type="text"
                  value={form.description}
                  onChange={(e) =>
                    setForm({ ...form, description: e.target.value })
                  }
                  required
                />
              </div>
              <div className="form-group">
                <label>Price</label>
                <input
                  type="number"
                  step="0.01"
                  value={form.price}
                  onChange={(e) => setForm({ ...form, price: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Stock</label>
                <input
                  type="number"
                  value={form.stock}
                  onChange={(e) => setForm({ ...form, stock: e.target.value })}
                  required
                />
              </div>
              {formError && <p className="error">{formError}</p>}
              <div className="modal-buttons">
                <button
                  type="button"
                  className="btn-cancel"
                  onClick={() => setShowModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  {editingProduct ? "Save changes" : "Create"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}