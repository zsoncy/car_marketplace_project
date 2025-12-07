
// src/services/CarService.ts

import type { carRequest, carType } from "../Types/Car.ts";

// ------------- GetAll -------------
export const getAllCars = async (accessToken: string) => {
    const res = await fetch("/api/cars", {
        headers: { Authorization: `Bearer ${accessToken}` },
    });
    if (res.ok) {
        const response = await res.json();
        console.log(response);
        return response as carType[];
    } else {
        const message = await res.text();
        throw new Error(message || "Something went wrong");
    }
};

// ------------- GetById -------------
export const getCarById = async (accessToken: string, carId: number | undefined) => {
    const res = await fetch(`/api/cars/${carId}`, {
        headers: { Authorization: `Bearer ${accessToken}` },
    });
    if (res.ok) {
        const response = await res.json();
        console.log(response);
        return response as carType;
    } else {
        const message = await res.text();
        throw new Error(message || "Request could not be completed");
    }
};

// ------------- Post -------------
// NOTE: username is a query parameter per CarController.createCar(...)
export const postCar = async (
    accessToken: string,
    username: string,
    car: carRequest
) => {
    const res = await fetch(`/api/cars?username=${encodeURIComponent(username)}`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(car),
    });
    if (res.ok) {
        const response = await res.json();
        console.log(response);
        return response as carType;
    } else {
        const message = await res.text();
        throw new Error(message || "Request could not be completed");
    }
};

// ------------- Update -------------
export const updateCar = async (
    accessToken: string,
    id: number,
    updateRequest: carType
) => {
    const res = await fetch(`/api/cars/${id}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(updateRequest),
    });
    if (res.ok) {
        const response = await res.json();
        console.log(response);
        return response as carType;
    } else {
        const message = await res.text();
        throw new Error(
            message || "You are not the owner of the car, or don't have permission to edit the car"
        );
    }
};

// ------------- Delete -------------
export const deleteCar = async (accessToken: string, id: number) => {
    const res = await fetch(`/api/cars/${id}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    });
    if (res.ok) {
        return await res.text(); // "Car with id:<id>has been deleted" per your controller
    } else {
        const error = await res.text();
        throw new Error(
            error || "You are not the owner of the car, or don't have permission to delete the car"
        );
    }
};
