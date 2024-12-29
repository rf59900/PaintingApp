import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "../api/axios";
import { useRef, useState } from "react";

export const UploadPainting = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const paintingDataURL: string = location.state;
  const { user } = useAuth();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  let error = useRef<HTMLHeadingElement>(null);

  type CreatePaintingRequest = {
    title: string;
    description: string;
    dataUrl: string;
  };

  const setError = (
    errorType: "titleError" | "dataURLError" | "networkError"
  ) => {
    let errorElement = error.current;
    if (!errorElement) {
      console.error(
        "ERROR: Attempted to set error message before error element loaded"
      );
      return;
    }
    errorElement.style.display = "flex";
    if (errorType == "titleError") {
      errorElement.innerText =
        "ERROR: Title must be at least one character long";
    } else if (errorType == "dataURLError") {
      errorElement.innerHTML = "ERROR: No painting data url present";
    } else {
      errorElement.innerHTML = "ERROR: Failed to send info to server";
    }
  };

  const uploadPainting = async () => {
    if (title.length < 1) {
      setError("titleError");
      return;
    }

    if (!paintingDataURL) {
      setError("dataURLError");
      return;
    }

    const data: CreatePaintingRequest = {
      title: title,
      description: description,
      dataUrl: paintingDataURL,
    };

    const headers = {
      headers: { Authorization: "Bearer " + user?.authToken },
    };

    try {
      const response = await axios.post("/painting", data, headers);
      console.log(response);
      navigate("/");
    } catch (err) {
      setError("networkError");
      console.error(err);
    }
  };
  return (
    <>
      <div className="container">
        <h1 className="paintingUploadErrorMessage" ref={error}></h1>
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
