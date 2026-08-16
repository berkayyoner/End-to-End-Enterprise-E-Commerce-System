import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    fetchProduct();
  }, [id]);

  const fetchProduct = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/products/${id}`);
      setProduct(response.data);
    } catch (error) {
      console.error('Error fetching product:', error);
    }
  };

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
    <div className="product-details">
      <h2>{product.name}</h2>
      <img src={product.imageUrl} alt={product.name} />
      <p>{product.description}</p>
      <p>Price: ${product.price}</p>
      <p>Stock: {product.stock}</p>
      <p>Average Rating: {product.averageRating}</p>
      <p>Total Ratings: {product.totalRatings}</p>
      <h3>Product Campaigns</h3>
      <ul>
        {product.campaigns.map(campaign => (
          <li key={campaign.id}>{campaign.name}</li>
        ))}
      </ul>
      <button>Buy Now</button>
      <button>Add to Basket</button>
    </div>
  );
}

export default ProductDetails;
