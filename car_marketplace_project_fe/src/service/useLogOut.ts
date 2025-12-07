
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { userStore } from "../Stores/UserStore";
import { userLogout } from "./AuthServices.ts";

export function useLogout() {
    const navigate = useNavigate();

    const logoutMutation = useMutation<void, Error, void>({
        mutationFn: () => {
            const token = userStore.getState().user?.accessToken;
            if (!token) throw new Error("Not authenticated");
            return userLogout(token);
        },
        onSuccess: () => {
            userStore.getState().stateLogout();
            navigate("/login");
            toast.success("Successfully logged out!");
        },
        onError: (error) => {
            toast.error(error.message);
        }
    });

    return {
        handleLogout: () => logoutMutation.mutate(),
        isLoading: logoutMutation.isPending
    };
}
