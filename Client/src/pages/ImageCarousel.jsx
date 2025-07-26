import React, { useState, useEffect } from "react";

const ImageCarousel = ({ images, interval = 3000 }) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const autoSlide = setInterval(() => {
      setCurrentIndex((prev) =>
        prev === images.length - 1 ? 0 : prev + 1
      );
    }, interval);

    return () => clearInterval(autoSlide);
  }, [images.length, interval]);

  return (
    <div className="w-full overflow-hidden relative">
      <img
        src={images[currentIndex]}
        alt={`slide-${currentIndex}`}
        className="w-full h-[360px] object-cover transition-all duration-500"
      />
    </div>
  );
};

export default ImageCarousel;
