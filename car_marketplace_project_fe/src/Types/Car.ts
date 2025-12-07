
import type { User } from "./User.ts";

export type Fuel = "PETROL" | "DIESEL" | "HYBRID" | "ELECTRIC";
export type Transmission = "MANUAL" | "AUTOMATIC";

export interface Car {
    id: number;
    vin: string;
    make: string;
    model: string;
    year: number;
    fuel: Fuel;
    transmission: Transmission;
    engine_size: number;
    images_src?: string[];
    price: number;
    description?: string;
    user?: User;
    username?: string;
}

export type carType = {
    id: number;
    vin: string;
    make: string;
    model: string;
    year: number;
    fuel: Fuel;
    transmission: Transmission;
    engine_size: number;
    images_src?: string[];
    price: number;
    description?: string;
    user?: User;
    username?: string;
};


export type carRequest = {
    vin: string;
    make: string;
    model: string;
    year: number;
    fuel: Fuel;
    transmission: Transmission;
    engine_size: number;
    images_src?: string[];
    price: number;
    description?: string;
};
