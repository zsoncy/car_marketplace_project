
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type SubmitHandler, useForm } from "react-hook-form";
import type { carRequest } from "../../Types/Car.ts";
import { userStore } from "../../Stores/UserStore.ts";
import { postCar } from "../../service/CarService.ts";
import { toast } from "react-toastify";
import { useMemo } from "react";

type CarFormFields = carRequest & {
    images_text?: string;
};

export default function PostCarForm() {
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
        watch,
        setValue,
    } = useForm<CarFormFields>({
        defaultValues: {
            vin: "",
            make: "",
            model: "",
            year: new Date().getFullYear(),
            fuel: "PETROL",
            transmission: "MANUAL",
            engine_size: 0,
            price: 0,
            description: "",
            images_text: "",
        },
    });

    const queryClient = useQueryClient();
    const currentUser = userStore.getState().user;

    const postMutation = useMutation({
        mutationFn: (payload: carRequest) => {
            if (!currentUser?.accessToken || !currentUser.username) {
                throw new Error("Not authenticated");
            }
            return postCar(currentUser.accessToken, currentUser.username, payload);
        },
        onSuccess: () => {
            toast.success("Car posted successfully");
            reset();
            queryClient.invalidateQueries({ queryKey: ["cars"] });
        },
        onError: (error) => {
            if (error instanceof Error) {
                toast.error(error.message);
            } else {
                toast.error("Something went wrong");
            }
        },
    });

    const onSubmit: SubmitHandler<CarFormFields> = async (form) => {
        // Convert images_text -> images_src[]
        const images_src = (form.images_text ?? "")
            .split(",")
            .map((s) => s.trim())
            .filter(Boolean);

        // Build request payload (omit images_text)
        const payload: carRequest = {
            vin: form.vin,
            make: form.make,
            model: form.model,
            year: Number(form.year),
            fuel: form.fuel,
            transmission: form.transmission,
            engine_size: Number(form.engine_size),
            images_src,
            price: Number(form.price),
            description: form.description,
        };

        postMutation.mutate(payload);
    };

    // (Optional) Derived helper for showing a friendly count of images
    const imagesCount = useMemo(
        () =>
            (watch("images_text") ?? "")
                .split(",")
                .map((s) => s.trim())
                .filter(Boolean).length,
        [watch("images_text")]
    );

    return (
        <div className={"flex flex-col justify-center m-auto mb-5"}>
            <div className={"border-t border-white w-[80%] m-auto mt-5 mb-10"}></div>
            <h1 className={"text-center text-white mr-10 text-3xl mb-10 "}>Add a new car</h1>

            <div className={"flex border-2 rounded-2xl p-5 mb-10 pt-10 w-250 self-center bg-[#AFBEE3]"}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className={"flex fle gap-3 flex-wrap"}>

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
                            {errors.vin && <div className={"text-[#600000] w-auto"}>{errors.vin.message}</div>}
                        </div>

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
                            {errors.make && <div className={"text-[#600000] w-auto"}>{errors.make.message}</div>}
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
                                <div className={"text-[#600000] w-auto"}>{errors.model.message}</div>
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
                                <div className={"text-[#600000] w-auto"}>{errors.year.message}</div>
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
                                <div className={"text-[#600000] w-auto"}>{errors.engine_size.message}</div>
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
                                <div className={"text-[#600000] w-auto"}>{errors.price.message}</div>
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
                                <div className={"text-[#600000] w-auto"}>{errors.description.message}</div>
                            )}
                        </div>

                        {/* Images as comma-separated text; converted to images_src[] on submit */}
                        <div className={"flex flex-col"}>
                            <input
                                {...register("images_text")}
                                type="text"
                                placeholder="Image URLs (comma separated)"
                                className={"border-2 bg-white text-center w-75"}
                                onBlur={(e) => {
                                    // Optional: normalize spaces on blur
                                    const normalized = e.target.value
                                        .split(",")
                                        .map((s) => s.trim())
                                        .filter(Boolean)
                                        .join(", ");
                                    setValue("images_text", normalized, { shouldDirty: true });
                                }}
                            />
                            {/* Optional helper text */}
                            <small className="text-[#142040]">
                                {imagesCount > 0 ? `${imagesCount} image(s) detected` : "No images yet"}
                            </small>
                        </div>

                        {/* Actions */}
                        <button
                            type="submit"
                            className={
                                "border-2 border-[#142040] rounded-2xl h-12 w-30 text-white bg-[#2C4278] transition delay-75 ease-in-out hover:bg-[#142040]"
                            }
                        >
                            Post
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
