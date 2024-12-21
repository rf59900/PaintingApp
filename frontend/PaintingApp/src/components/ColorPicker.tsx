import { useState } from "react";

type ColorAndSetColor = {
  color: String;
  setColor: (color: string) => void;
};
export const ColorPicker = ({ color, setColor }: ColorAndSetColor) => {
  const [colors, setColors] = useState([
    "red",
    "orange",
    "yellow",
    "green",
    "black",
    "blue",
    "indigo",
    "violet",
    "pink",
  ]);

  let currentColors = colors;

  const shiftColors = (colors: Array<string>, direction: "left" | "right") => {
    if (direction == "right") {
      let start = currentColors.length - 1;
      let lastColor = currentColors[colors.length - 1];
      while (start > 0) {
        currentColors[start] = currentColors[start - 1];
        start -= 1;
      }
      currentColors[start] = lastColor;
    } else {
      let firstColor = currentColors[0];
      for (let i of Array(currentColors.length).keys()) {
        currentColors[i] = currentColors[i + 1];
      }
      currentColors[currentColors.length - 1] = firstColor;
    }
    console.log(colors);
    console.log(currentColors[4]);
    setColor(currentColors[4]);
    setColors(currentColors);
  };

  return (
    <>
      <div className="colorPickerContainer">
        <svg
          className="arrow"
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="black"
          onClick={() => shiftColors(colors, "left")}
        >
          <polygon points="14,6 8,12 14,18" />
        </svg>
        <div className="colorPicker">
          {currentColors.map((itemColor, i) => {
            return (
              <div
                key={itemColor}
                style={{ backgroundColor: itemColor }}
                className={i == 4 ? "selectedColor" : "color"}
              />
            );
          })}
        </div>
        <svg
          className="arrow"
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="black"
          onClick={() => shiftColors(colors, "right")}
        >
          <polygon points="10,6 16,12 10,18" />
        </svg>
      </div>
    </>
  );
};
