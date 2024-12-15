import { useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";

export const Paint = () => {
  const { user } = useAuth();
  const canvas = useRef<HTMLCanvasElement>(null);
  class Canvas {
    static canvas: HTMLCanvasElement | null = null;
    static ctx: CanvasRenderingContext2D | null = null;
    static pixelSize = 10;
    static color = "black";
    static cursorX = 0;
    static cursorY = 0;
    static drawPixel() {
      if (!Canvas.ctx) {
        return;
      }
      Canvas.ctx.fillStyle = Canvas.color;
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
      Canvas.drawPixel();
    }
  };

  useEffect(() => {
    if (canvas.current) {
      const ctx = canvas.current.getContext("2d");
      if (!ctx) {
        return;
      }
      Canvas.canvas = canvas.current;
      Canvas.ctx = ctx;
    }
  }, []);

  return (
    <canvas
      ref={canvas}
      onMouseMove={(e) => handleMouseMove(e)}
      className="canvas"
    ></canvas>
  );
};
