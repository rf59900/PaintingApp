import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { User } from "../context/AuthContext";
import axios from "../api/axios";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";

export type JwtPayload = {
  authorities: String[];
  exp: Number;
  iat: Number;
  iss: String;
  sub: String;
};

export const Login = () => {
  const { user, setUser } = useAuth();
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const loginUser = async (username1: string, password: string) => {
    try {
      const response = await axios.post(
        "/login",
        {},
        // basic auth
        {
          auth: {
            username: username1,
            password: password,
          },
        }
      );
      setUsername("");
      setPassword("");
      const jwt = response?.data?.jwt;
      console.log(jwt);
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
      //console.log("auth token " + newUser.authToken);
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
      <button onClick={() => loginUser(username, password)}>Login</button>
    </>
  );
};
