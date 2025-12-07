import type {User} from "../Types/User.ts";
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

interface UserState {
    user : User | null,
    stateLogin: (user:User) => void,
    stateLogout: () => void
}

export const userStore = create<UserState>()(
    persist(
        (set) => ({
            user: null,
            stateLogin: (user: User) => set({ user }),
            stateLogout: () => set({ user: null }),
        }),
        {
            name: "userStore",
            storage: createJSONStorage(() => localStorage),
        }
    )
);
