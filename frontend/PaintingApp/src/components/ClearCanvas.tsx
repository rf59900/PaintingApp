type clearCanvas = { clearCanvas: () => void };

export const ClearCanvas = ({ clearCanvas }: clearCanvas) => {
  return (
    <>
      <svg
        onClick={clearCanvas}
        className="clearCanvasSymbol"
        viewBox="0 0 100 100"
        xmlns="http://www.w3.org/2000/svg"
      >
        <rect x="30" y="40" width="40" height="50" fill="black" />
        <rect x="25" y="30" width="50" height="10" fill="black" />
        <line x1="38" y1="40" x2="38" y2="90" stroke="white" stroke-width="5" />
        <line x1="50" y1="40" x2="50" y2="90" stroke="white" stroke-width="5" />
        <line x1="62" y1="40" x2="62" y2="90" stroke="white" stroke-width="5" />
      </svg>
    </>
  );
};
