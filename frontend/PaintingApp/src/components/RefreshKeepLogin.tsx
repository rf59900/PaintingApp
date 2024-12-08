import { useEffect } from "react";
import { useRefreshToken } from "../hooks/useRefreshToken";
import { useAuth } from "../context/AuthContext";
import { Outlet } from "react-router-dom";

export const RefreshKeepLogin = () => {
  const refresh = useRefreshToken();
  const { auth } = useAuth();
  console.log(auth);
  useEffect(() => {
    const verifyRefreshToken = async () => {
      await refresh();
    };
    console.log("hey");
    !auth?.accessToken ? verifyRefreshToken() : null;
  }, []);

  return (
    <>
      <h1> Hey </h1>
      <Outlet />
    </>
  );
};
