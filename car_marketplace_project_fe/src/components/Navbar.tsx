
import { Outlet, Link } from "react-router-dom";
import { userStore } from "../Stores/UserStore";
import "../styles/Homepage.css";
import { useState } from "react";
import { useLogout } from "./../service/useLogOut";

export default function Navbar() {
    const currentUser = userStore.getState().user;
    const [menuOpen, setMenuOpen] = useState(false);
    const { handleLogout } = useLogout();

    return(

        <div className="pt-0.5">

            <nav className="relative bg-gray-800 text-white border-b border-white/10 h-[64px] px-[20px]">
                <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 h-full">
                    <div className="flex h-full ml-10 items-center justify-between relative">
                        {/* Menu button */}
                        <div className="flex sm:hidden">
                            <button
                                onClick={() => setMenuOpen(!menuOpen)}
                                className="inline-flex items-center justify-center rounded-md p-2 text-gray-400 hover:bg-white/5 hover:text-white focus:outline-none"
                            >
                                {!menuOpen ? (
                                    <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth={1.5} viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"/>
                                    </svg>
                                ) : (
                                    <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth={1.5} viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/>
                                    </svg>
                                )}
                            </button>
                        </div>

                        {/* Logo + Links */}
                        <div className="flex flex-1 items-center">
                            {/* Centered title on small screens */}
                            <h1 className="font-bold text-[16px] mr-4 sm:static absolute left-1/2 transform -translate-x-1/2">
                                Car Marketplace
                            </h1>
                            <div className="hidden sm:flex sm:space-x-4">
                                {currentUser?.role === "ADMIN" && (
                                    <Link to="/dashboard/admin"
                                          className="px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5 hover:text-white">
                                        Users
                                    </Link>
                                )}
                                <Link to="/dashboard/home"
                                      className="px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5 hover:text-white">
                                    Home
                                </Link>
                                <Link to="/dashboard/myCars"
                                      className="px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5 hover:text-white">
                                    My Cars
                                </Link>
                                <Link to="/dashboard/cars"
                                      className="px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5 hover:text-white">
                                    All cars for sale
                                </Link>
                            </div>
                        </div>

                        {/* Username */}
                        <div className="hidden sm:block text-gray-300 text-sm px-3">
                            {currentUser?.username ?? "Guest"}
                        </div>
                        <div className="hidden sm:block text-gray-300 text-sm px-3">
                            <button className={"LogOutButton"}
                                    onClick={handleLogout}>Log out</button>
                        </div>

                    </div>
                </div>
            </nav>


            {/* Dropdown outside navbar */}
            {menuOpen && (
                <div className="sm:hidden px-2 pt-2 pb-3 space-y-1 bg-gray-800/90">
                    {currentUser?.role === "ADMIN" && (
                        <Link to="/dashboard/admin"
                              className="block px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5">
                            Users
                        </Link>
                    )}
                    <Link to="/dashboard/home"
                          className="block px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5">
                        Home
                    </Link>
                    <Link to="/dashboard/myCars"
                          className="block px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5">
                        My Cars
                    </Link>
                    <Link to="/dashboard/cars"
                          className="block px-[12px] py-[8px] text-[14px] rounded-md text-gray-300 hover:bg-white/5">
                        All cars for sale
                    </Link>
                    <div className="border-t border-white/10 mt-2 pt-2 text-gray-400 px-[12px] py-[8px] text-[14px] username-logout-container">
                        <span>{currentUser?.username ?? "Guest"}</span>
                        <button className="LogOutButton" onClick={handleLogout}>Log out</button>
                    </div>

                </div>
            )}

            <Outlet />
        </div>

    );
}