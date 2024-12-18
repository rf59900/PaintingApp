import { useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ColorPicker } from "../components/ColorPicker";

export const Paint = () => {
  const { user } = useAuth();
  const canvas = useRef<HTMLCanvasElement>(null);

  const [color, setColor] = useState("black");

  const handleColorChange = (color: string) => {
    setColor(color);
  };

  class Canvas {
    static canvas: HTMLCanvasElement | null = canvas.current;
    static ctx: CanvasRenderingContext2D | undefined | null =
      canvas?.current?.getContext("2d");
    static pixelSize = 10;
    static color = color;
    static cursorX = 0;
    static cursorY = 0;
    static drawPixel() {
      if (!Canvas.ctx) {
        console.log("no context");
        return;
      }
      Canvas.ctx.fillStyle = color;
      console.log(Canvas.ctx.fillStyle);
      Canvas.ctx.fillRect(
        this.cursorX,
        this.cursorY,
        this.pixelSize,
        this.pixelSize
      );
    }
  }

  const handleMouseMove = (
    e: React.MouseEvent<HTMLCanvasElement, MouseEvent>
  ) => {
    // if canvas has not rendered yet do nothing
    if (!Canvas.canvas) {
      console.error("ERROR: Failed to find canvas.");
      return;
    }
    // calculate mouse position coordinates from inside of the canvas
    const pos = Canvas.canvas.getBoundingClientRect();
    Canvas.cursorX = Math.floor(
      ((e.clientX - pos.left) / (pos.right - pos.left)) * Canvas.canvas.width
    );
    Canvas.cursorY = Math.floor(
      ((e.clientY - pos.top) / (pos.bottom - pos.top)) * Canvas.canvas.height
    );
    // on left click draw pixel to canvas
    if (e.buttons == 1) {
      console.log("clicked");
      Canvas.color = color;
      Canvas.drawPixel();
    }
  };

  useEffect(() => {
    console.log("reload");
    if (canvas.current) {
      const ctx = canvas.current.getContext("2d");
      Canvas.canvas = canvas.current;
      Canvas.ctx = ctx;
    }
  }, []);

  return (
    <>
      <div className="container">
        <ColorPicker color={color} setColor={handleColorChange} />
        <canvas
          ref={canvas}
          onMouseMove={(e) => handleMouseMove(e)}
          className="canvas"
        ></canvas>
      </div>
    </>
  );
};
