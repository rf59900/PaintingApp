import React, { useEffect } from "react";
import { useAuth, User } from "../context/AuthContext";
import axios from "../api/axios";
import { useNavigate } from "react-router-dom";

export const Logout = () => {
  const sleep = (delay) => new Promise((res) => setTimeout(res, delay));
  const { user, setUser } = useAuth();
  console.log(user?.authToken);
  const navigate = useNavigate();
  console.log("logout");

  useEffect(() => {
    const logout = async () => {
      try {
        console.log(user?.authToken);
        const response = await axios.get("/logout");
        setUser(undefined);
        sleep(300);
        navigate("/");
      } catch (err) {
        console.error(err);
      }
    };
    logout();
  }, [user, setUser, navigate]);
};
