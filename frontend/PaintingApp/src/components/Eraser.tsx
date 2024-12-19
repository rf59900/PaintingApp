import React, { useState } from "react";

type EraseAndSetErase = {
  erase: boolean;
  setErase: (erase: boolean) => void;
};
export const Eraser = ({ erase, setErase }: EraseAndSetErase) => {
  const [eraserColor, setEraserColor] = useState("black");
  const handleEraserClick = () => {
    erase ? setErase(false) : setErase(true);
    erase ? setEraserColor("black") : setEraserColor("grey");
  };
  return (
    <>
      <svg
        className="eraser"
        onClick={handleEraserClick}
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 100 100"
        fill="none"
      >
        <rect
          x="30"
          y="30"
          width="40"
          height="20"
          fill={eraserColor}
          transform="rotate(45 50 50)"
        />
        <rect x="25" y="60" width="50" height="10" fill="#d3d3d3" />
      </svg>
    </>
  );
};
