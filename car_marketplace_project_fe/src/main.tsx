
import './styles/main.css';

import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import {createBrowserRouter, Navigate, RouterProvider} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import LoginForm from "./components/Auth/LoginForm.tsx";
import RegisterForm from "./components/Auth/RegisterForm.tsx";
import App from "./App.tsx";
import Homepage from "./components/Homepage.tsx";
import Navbar from "./components/Navbar.tsx";
import DisplayCars from "./components/Car/DisplayCars.tsx";
import DisplayUsers from "./components/Admin/DisplayUsers.tsx";
import CarPage from "./components/Car/CarPage.tsx";
import MyCars from "./components/Car/MyCars.tsx";

const router = createBrowserRouter([{
        path:'/',
        element:<Navigate to={"/login"} />,
        errorElement: <div>404 not found</div>
    },
    {
      path:'/login',
      element:<LoginForm/>
    },
    {
        path:"/register",
        element:<RegisterForm />
    },
    {
      path: "/cars",
      element:<Navigate to={"/login"} />
    },
    {
        path:"/dashboard",
        element: <Navbar />,
        children: [
            {
                path:"/dashboard/home",
                element:<Homepage/>
            },
            {
             path:"/dashboard/cars",
                element:<DisplayCars />
            },
            {
              path:"/dashboard/cars/:param",
              element:<CarPage />
            },
            {
                path:"/dashboard/myCars",
                element:<MyCars />
            },
            {
                path:"/dashboard/admin",
                element:<DisplayUsers />
            }
        ]

    }
    ]);

const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
          <App />
      </QueryClientProvider>
  </StrictMode>,
)
