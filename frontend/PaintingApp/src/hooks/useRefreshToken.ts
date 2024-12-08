import { act, useEffect } from "react";
import axios from "../api/axios";
import { useAuth } from "../context/AuthContext";
import { jwtDecode } from "jwt-decode";
import { User } from "../context/AuthContext";
import { JwtPayload } from "../pages/Login";

export const useRefreshToken = () => {
    const { user, setUser } = useAuth();
  const refresh = async () => {
    const getUserInfo = async () => {
        try {
            console.log("auth token " + user?.authToken)
            const response = await axios.get("/refresh");
            const decoded : JwtPayload = jwtDecode(response?.data?.jwt);
            console.log(decoded);
            const newUser : User = {
                username: decoded.sub,
                roles: decoded.authorities,
                authToken: response.data.jwt
            }
            setUser(newUser)
        } catch (err) {
            console.error(err);
        }
    }
    getUserInfo();
  }
  return refresh;
}
