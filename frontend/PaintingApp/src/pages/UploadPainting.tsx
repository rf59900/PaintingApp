import { useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "../api/axios";
import { useState } from "react";

export const UploadPainting = () => {
  const location = useLocation();
  const paintingDataURL: string = location.state;
  const { user } = useAuth();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  type CreatePaintingRequest = {
    title: string;
    description: string;
    dataUrl: string;
  };

  const uploadPainting = async () => {
    const data: CreatePaintingRequest = {
      title: title,
      description: description,
      dataUrl: paintingDataURL,
    };

    const headers = {
      headers: { Authorization: "Bearer " + user?.authToken },
    };

    console.log(headers);
    try {
      console.log(user?.authToken);
      const response = await axios.post("/painting", data, headers);
      console.log(response);
    } catch (err) {
      console.error(err);
    }
  };
  return (
    <>
      <div className="container">
        <h1>Name your masterpiece:</h1>
        <input
          onChange={(e) => setTitle(e.target.value)}
          type="text"
          value={title}
        ></input>
        <img className="uploadPaintingPreview" src={paintingDataURL} />
        <h1>Description (Optional): </h1>
        <input
          type="text"
          onChange={(e) => setDescription(e.target.value)}
          value={description}
        ></input>
        <button onClick={() => uploadPainting()}>Upload</button>
      </div>
    </>
  );
};
