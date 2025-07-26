import { Suspense } from "react";
import { RouterProvider } from "react-router-dom";
import router from "./router";
import PageSkeleton from "./components/PageSkeleton";

export default function App() {
  return (
    <Suspense fallback={<PageSkeleton />}>
      <RouterProvider router={router} />
    </Suspense>
  );
}
