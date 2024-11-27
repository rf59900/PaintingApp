import { createContext, SetStateAction, useContext } from "react";
import { useState } from "react";

export type User = {
  username: String;
  roles: String[];
  authToken: string;
};

type SetAndGetUser = {
  user: User | undefined;
  setUser: React.Dispatch<SetStateAction<User | undefined>>;
};

const AuthContext = createContext<SetAndGetUser | undefined>(undefined);

export const AuthProvider = ({ children }: any) => {
  const [user, setUser] = useState<User | undefined>();
  return (
    <AuthContext.Provider value={{ user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used in AuthProvider");
  }

  return context;
};
