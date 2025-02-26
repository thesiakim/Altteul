// index.tsx (최상위 파일)
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import router from "./routes";
import "@styles/index.css";

const root = createRoot(document.getElementById("root")!);
root.render(<RouterProvider router={router} />);
