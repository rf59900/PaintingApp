import { useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { ColorPicker } from "../components/ColorPicker";
import { ChangePixelSize } from "../components/ChangePixelSize";
import { Eraser } from "../components/Eraser";
import { ClearCanvas } from "../components/ClearCanvas";
import { useNavigate } from "react-router-dom";

export const Paint = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const canvas = useRef<HTMLCanvasElement>(null);
  const canvasGUI = useRef<HTMLDivElement>(null);

  const [color, setColor] = useState("black");
  const [pixelSize, setPixelSize] = useState(10);
  const [erase, setErase] = useState(false);

  const handleColorChange = (color: string) => {
    setColor(color);
  };

  const handlePixelSizeChange = (pixelSize: number) => {
    setPixelSize(pixelSize);
  };

  const handleErase = (erase: boolean) => {
    setErase(erase);
  };

  const clearCanvas = () => {
    Canvas.ctx?.clearRect(0, 0, 10000, 10000);
  };

  const handleUpload = () => {
    if (!user) {
      navigate("/login");
      return;
    }
    navigate("/upload", {
      state: Canvas.canvas?.toDataURL(),
    });
  };

  // re-aligns the canvas gui on page load and resize/screen rotation
  const resizeGUI = () => {
    const pos = Canvas.canvas?.getBoundingClientRect();
    if (!canvasGUI.current) {
      return;
    }
    canvasGUI.current.style.top = pos?.top.toString() + "px";
    canvasGUI.current.style.left = pos?.left.toString() + "px";
  };

  const displayGUI = (e: KeyboardEvent) => {
    if (canvasGUI.current && e.key == "Escape") {
      if (!Canvas.canvas) {
        return;
      }
      if (canvasGUI.current.style.display == "none") {
        canvasGUI.current.style.display = "flex";
      } else {
        canvasGUI.current.style.display = "none";
      }
    }
  };

  class Canvas {
    static canvas: HTMLCanvasElement | null = canvas.current;
    static ctx: CanvasRenderingContext2D | undefined | null =
      canvas?.current?.getContext("2d");
    static erase = erase;
    static pixelSize = pixelSize;
    static color = color;
    static cursorX = 0;
    static cursorY = 0;
    static drawPixel() {
      if (!Canvas.ctx) {
        console.log("no context");
        return;
      }
      if (Canvas.erase) {
        Canvas.ctx.clearRect(
          this.cursorX,
          this.cursorY,
          this.pixelSize,
          this.pixelSize
        );
      } else {
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

    if (canvasGUI.current) {
      document.addEventListener("keydown", displayGUI);
    }

    window.addEventListener("resize", resizeGUI);
    window.addEventListener("load", resizeGUI);
  }, []);

  return (
    <>
      <div className="container">
        <div className="changePixelEraserRow">
          <ClearCanvas clearCanvas={clearCanvas} />
          <ChangePixelSize pixelSize={pixelSize} setPixelSize={setPixelSize} />
          <Eraser erase={erase} setErase={handleErase} />
        </div>
        <ColorPicker color={color} setColor={handleColorChange} />
        <div ref={canvasGUI} className="canvasGUI">
          <button className="GUIButton">Save</button>
          <button onClick={handleUpload} className="GUIButton">
            Upload
          </button>
        </div>
        <canvas
          ref={canvas}
          onMouseMove={(e) => handleMouseMove(e)}
          className="canvas"
        ></canvas>
      </div>
    </>
  );
};
