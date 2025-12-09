import { userStore } from "../../Stores/UserStore.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { carType } from "../../Types/Car.ts";
import Car from "./Car.tsx";
import { deleteCar } from "../../service/CarService.ts";
import { toast } from "react-toastify";

export default function CarElement(carProp: {
    car: carType;
    setUpdating: (car: carType) => void;
}) {
    const currentUser = userStore.getState().user;
    const queryClient = useQueryClient();

    const canEdit =
        !!currentUser &&
        (currentUser.username === carProp.car.username || currentUser.role === "ADMIN");


    const deleteMutation = useMutation({
        mutationFn: (id: number) => {
            const token = userStore.getState().user?.accessToken;
            if (!token) {
                throw new Error("Not authenticated");
            }
            return deleteCar(token, id);
        },
        onSuccess: () => {
            toast.success("The chosen car has been deleted");
            queryClient.invalidateQueries({ queryKey: ["cars"] });
        },
        onError: (error) => {
            if (error instanceof Error) {
                console.log(error.message);
                toast.error(
                    "The delete was not successful, you are not the owner of the car, or don't have permission to delete the car"
                );
            } else {
                toast.error("Something went wrong");
            }
        },
    });

    const handleDelete = (id: number) => {
        console.log(id);
        deleteMutation.mutate(id);
    };

    return (
        <div className={" text-center border-2 border-gray-800 rounded-4xl p-1 bg-gray-800"}>
            <Car carInfo={carProp.car} />
            <div>
            {canEdit ? (
                <button
                    className={
                        "text-l border-2 rounded-2xl px-1 my-1 w-20 bg-red-800 " +
                        "text-white transition delay-50 ease-in-out hover:bg-red-500"
                    }
                    onClick={() => handleDelete(carProp.car.id!)}
                >
                    Delete
                </button>
            ) : (<></>)}
            {canEdit ? (
                <button
                    className={
                        "text-l border-2 rounded-2xl ml-7 my-1 px-1 w-20 bg-blue-800 " +
                        "text-white transition delay-50 ease-in-out hover:bg-blue-500"
                    }
                    onClick={() => carProp.setUpdating(carProp.car)}
                >
                    Edit
                </button>
            ) : (<></>)}
            </div>
        </div>
    );
}
