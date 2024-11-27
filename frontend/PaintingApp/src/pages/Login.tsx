import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { User } from "../context/AuthContext";
import axios from "../api/axios";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";

export const Login = () => {
  type JwtPayload = {
    authorities: String[];
    exp: Number;
    iat: Number;
    iss: String;
    sub: String;
  };
  const { user, setUser } = useAuth();
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const loginUser = async () => {
    try {
      const response = await axios.post(
        "/login",
        {},
        // basic auth
        {
          auth: {
            username: "Ryan",
            password: "Password",
          },
        }
      );
      setUsername("");
      setPassword("");
      const jwt = response?.data?.jwt;
      const decoded: JwtPayload = jwtDecode(jwt);
      const username = decoded.sub;
      const roles = decoded.authorities;
      console.log(typeof jwt);
      const newUser: User = {
        username,
        roles,
        authToken: jwt,
      };
      setUser(newUser);
      console.log(response);
      console.log(user);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      <h1>User : {user?.username}</h1>
      <Link to={"/"}>Home</Link>
      <input value={username} onChange={(e) => setUsername(e.target.value)} />
      <input value={password} onChange={(e) => setPassword(e.target.value)} />
      <button onClick={() => loginUser()}>Login</button>
    </>
  );
};
