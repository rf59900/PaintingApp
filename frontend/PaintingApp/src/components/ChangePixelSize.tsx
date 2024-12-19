type pixelSizeAndSetPixelSize = {
  pixelSize: number;
  setPixelSize: (pixelSize: number) => void;
};

export const ChangePixelSize = ({
  pixelSize,
  setPixelSize,
}: pixelSizeAndSetPixelSize) => {
  return (
    <>
      <div className="changePixelSizeContainer">
        <svg
          onClick={() => setPixelSize(pixelSize == 1 ? 1 : (pixelSize -= 1))}
          className="changePixelSizeSymbol"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 100 100"
          fill="black"
        >
          <rect x="10" y="40" width="80" height="20" />
        </svg>
        <h1 className="currentPixelSize">{pixelSize}</h1>
        <svg
          onClick={() => setPixelSize((pixelSize += 1))}
          className="changePixelSizeSymbol"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 100 100"
          fill="black"
        >
          <rect x="40" y="10" width="20" height="80" />
          <rect x="10" y="40" width="80" height="20" />
        </svg>
      </div>
    </>
  );
};
