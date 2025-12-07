import type {Car} from "./Car.ts";

export interface User{
    user_id?: number,
    username:string,
    password?: string,
    cars?:Car[],
    role?: "ADMIN" | "USER",
    accessToken?: string;

}

export type UserResponse= {
    user_id?: number,
    username:string,
    password?: string,
    cars?:Car[],
    role?: "ADMIN" | "USER",
    accessToken?: string;

}

export type userListProp = {
    list : User[]
}

export type userProp = {
    userInfo : User
    handleUpdating: (user:UserResponse) => void
}

export type userAuthRequest  = {
    username: string,
    password: string
}