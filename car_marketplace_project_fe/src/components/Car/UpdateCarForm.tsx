
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type SubmitHandler, useForm } from "react-hook-form";
import type { carType } from "../../Types/Car.ts";
import { userStore } from "../../Stores/UserStore.ts";
import { updateCar } from "../../service/CarService.ts";
import { toast } from "react-toastify";

export default function UpdateCarForm(carToUpdate: {
    carInfo?: carType;
    manageEditing: () => void; // same as your UpdateRecipeForm API
}) {
    const {
        register,
        handleSubmit,
        formState: { errors },
        setValue,
        watch,
    } = useForm<carType>({
        defaultValues: {
            id: carToUpdate.carInfo?.id,
            vin: carToUpdate.carInfo?.vin,
            make: carToUpdate.carInfo?.make,
            model: carToUpdate.carInfo?.model,
            year: carToUpdate.carInfo?.year,
            fuel: carToUpdate.carInfo?.fuel,
            transmission: carToUpdate.carInfo?.transmission,
            engine_size: carToUpdate.carInfo?.engine_size,
            images_src: carToUpdate.carInfo?.images_src ?? [],
            price: carToUpdate.carInfo?.price,
            description: carToUpdate.carInfo?.description,
            user: carToUpdate.carInfo?.user,
            username: carToUpdate.carInfo?.username,
        },
    });

    const queryClient = useQueryClient();
    const currentUser = userStore.getState().user;

    const updateMutation = useMutation({
        mutationFn: (data: carType) => {
            if (!currentUser?.accessToken || data.id == null) {
                throw new Error("Missing authentication or car id");
            }
            // Your CarService.updateCar(accessToken, id, updateRequest)
            return updateCar(currentUser.accessToken, data.id, data);
        },
        onSuccess: () => {
            toast.success("The chosen car was updated!");
            queryClient.invalidateQueries({ queryKey: ["cars"] });
            carToUpdate.manageEditing(); // close the form after success (same behavior as recipe)
        },
        onError: (error) => {
            if (error instanceof Error) {
                toast.error(error.message);
            } else {
                toast.error("Something went wrong");
            }
        },
    });

    const onSubmit: SubmitHandler<carType> = async (data: carType) => {
        updateMutation.mutate(data);
    };

    // ----- Text <-> string[] bridge for images_src -----
    const imagesText = (watch("images_src") ?? []).join(", ");

    return (
        <div className={"flex flex-col justify-center m-auto mb-5"}>
            <div className={"border-t border-white w-[80%] m-auto mt-5 mb-10"}></div>
            <h1 className={"text-center text-white mr-10 text-3xl mb-10"}>Update a car</h1>

            <div className={"flex border-2 rounded-2xl p-5 mb-10 pt-10 w-250 self-center bg-[#AFBEE3]"}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className={"flex fle gap-3 flex-wrap"}>

                        {/* Make */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("make", {
                                    required: "Make is required",
                                    minLength: { value: 2, message: "Make must be at least 2 characters" },
                                })}
                                type="text"
                                placeholder="Make"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.make && (
                                <div className={"text-[#600000] w-auto"}>{errors.make.message}</div>
                            )}
                        </div>

                        {/* Model */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("model", {
                                    required: "Model is required",
                                    minLength: { value: 1, message: "Model must be at least 1 character" },
                                })}
                                type="text"
                                placeholder="Model"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.model && (
                                <div className={"text-[#600000]"}>{errors.model.message}</div>
                            )}
                        </div>

                        {/* VIN */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("vin", {
                                    required: "VIN is required",
                                    minLength: { value: 8, message: "VIN seems too short" },
                                })}
                                type="text"
                                placeholder="VIN"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.vin && (
                                <div className={"text-[#600000]"}>{errors.vin.message}</div>
                            )}
                        </div>

                        {/* Year */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("year", {
                                    required: "Year is required",
                                    min: { value: 1900, message: "Year must be ≥ 1900" },
                                    max: { value: new Date().getFullYear() + 1, message: "Year too large" },
                                    valueAsNumber: true,
                                })}
                                type="number"
                                placeholder="Year"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.year && (
                                <div className={"text-[#600000]"}>{errors.year.message}</div>
                            )}
                        </div>

                        {/* Price */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("price", {
                                    required: "Price is required",
                                    min: { value: 0, message: "Price cannot be negative" },
                                    valueAsNumber: true,
                                })}
                                type="number"
                                placeholder="Price"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.price && (
                                <div className={"text-[#600000]"}>{errors.price.message}</div>
                            )}
                        </div>

                        {/* Engine size */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("engine_size", {
                                    required: "Engine size is required",
                                    min: { value: 0, message: "Engine size cannot be negative" },
                                    valueAsNumber: true,
                                })}
                                type="number"
                                placeholder="Engine size (cc)"
                                className={"border-2 bg-white text-center w-75"}
                            />
                            {errors.engine_size && (
                                <div className={"text-[#600000]"}>{errors.engine_size.message}</div>
                            )}
                        </div>

                        {/* Description */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("description", {
                                    required: "Description is required",
                                    minLength: { value: 5, message: "Description must be at least 5 characters" },
                                })}
                                type="text"
                                placeholder="Description"
                                className={"border-2 bg-white text-center w-75 he overflow-hidden"}
                            />
                            {errors.description && (
                                <div className={"text-[#600000]"}>{errors.description.message}</div>
                            )}
                        </div>

                        {/* Fuel */}
                        <label className={"pt-1"}>Choose fuel: </label>
                        <select
                            {...register("fuel", { required: true })}
                            className={"bg-white border-2 h-8"}
                        >
                            <option value="PETROL">Petrol</option>
                            <option value="DIESEL">Diesel</option>
                            <option value="ELECTRIC">Electric</option>
                            <option value="HYBRID">Hybrid</option>
                        </select>

                        {/* Transmission */}
                        <label className={"pt-1"}>Choose transmission: </label>
                        <select
                            {...register("transmission", { required: true })}
                            className={"bg-white border-2 h-8"}
                        >
                            <option value="MANUAL">Manual</option>
                            <option value="AUTOMATIC">Automatic</option>
                        </select>

                        {/* Images (comma-separated) — controlled text input feeding string[] via setValue */}
                        <div className={"flex flex-col"}>
                            <input
                                type="text"
                                placeholder="Image URLs (comma separated)"
                                className={"border-2 bg-white text-center w-75"}
                                value={imagesText}
                                onChange={(e) => {
                                    const val: string = e.target.value;
                                    const arr: string[] = val
                                        .split(",")
                                        .map((s) => s.trim())
                                        .filter(Boolean);
                                    // Update the array field in RHF:
                                    setValue("images_src", arr, { shouldValidate: true, shouldDirty: true });
                                }}
                            />
                            {/* Add URL validation messages here if desired */}
                        </div>

                        {/* Actions */}
                        <button
                            type="submit"
                            className={
                                "border-2 border-[#142040] rounded-2xl h-12 w-30 text-white bg-[#2C4278] transition delay-75 ease-in-out hover:bg-[#142040]"
                            }
                        >
                            Update
                        </button>

                        <button
                            type="button"
                            className={
                                "border-2 border-[#400000] rounded-2xl h-12 w-30 text-white bg-[#800000] transition delay-75 ease-in-out hover:bg-[#400000]"
                            }
                            onClick={carToUpdate.manageEditing}
                        >
                            Close
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
