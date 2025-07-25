import { RouterProvider } from "react-router-dom";
import router from "./router";
import { createRoot } from "react-dom/client";

const root = createRoot(document.getElementById("root"));
root.render(
 
    <RouterProvider router={router} />
  
);
