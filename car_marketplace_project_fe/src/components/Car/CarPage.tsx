import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { userStore } from "../../Stores/UserStore.ts";
import { useEffect } from "react";
import { toast } from "react-toastify";
import { getCarById } from "../../service/CarService.ts";
import type { carType } from "../../Types/Car.ts";

export default function CarPage() {
    const { param } = useParams();
    const id = param !== undefined ? parseInt(param) : undefined;

    const currentUser = userStore.getState().user;

    const {
        data,
        error,
        isError,
        isLoading,
    } = useQuery<carType>({
        queryKey: ["CarById", id],
        queryFn: async () => {
            if (!currentUser?.accessToken) {
                throw new Error("Authentication failed");
            }
            return await getCarById(currentUser.accessToken, id);
        },
        enabled: !!currentUser?.accessToken && id !== undefined,
    });

    useEffect(() => {
        if (isError && error instanceof Error) {
            toast.error(error.message);
        }
    }, [isError, error]);

    if (isLoading) {
        return <p>Loading...</p>;
    }

    if (!data) {
        return <p className={"text-white"}>No car found.</p>;
    }

    const title = `${data.make} ${data.model}`;
    const coverSrc =
        data.images_src && data.images_src.length > 0
            ? data.images_src[0]
            : "/placeholder-car.jpg";

    return (
        <div className={"flex flex-col"}>
            <h1 className={"text-4xl text-center text-white mb-8 mt-8"}>
                {title}
            </h1>

            <div className={"flex justify-center mx-40 mb-5"}>
                <img
                    className={
                        "transition delay-150 duration-300 ease-in-out hover:scale-130"
                    }
                    src={coverSrc}
                    alt={`${title} (${data.year})`}
                />
            </div>

            <div className={"text-center text-white flex flex-col gap-1"}>
                {/* Car-specific meta info */}
                <p>
                    Year: <strong className={"text-white font-bold"}>{data.year}</strong>
                </p>
                <p>
                    Fuel: <strong className={"text-white font-bold"}>{data.fuel}</strong>
                </p>
                <p>
                    Transmission:{" "}
                    <strong className={"text-white font-bold"}>{data.transmission}</strong>
                </p>
                {typeof data.price === "number" && (
                    <p>
                        Price:{" "}
                        <strong className={"text-white font-bold"}>
                            {data.price.toLocaleString()} EUR
                        </strong>
                    </p>
                )}
                {/* Username is optional on Car; show only if present */}
                {data.username && (
                    <p>
                        Added by:{" "}
                        <strong className={"text-white font-bold"}>{data.username}</strong>
                    </p>
                )}
            </div>

            <div className={"border-t border-white w-[60%] m-auto mt-10"}></div>

            <div className={"mt-5 ml-10 mr-10 mb-20"}>
                <h2 className={"text-2xl text-white mt-5"}>
                    <strong>Description:</strong>
                </h2>
                <p className={"text-white"}>{data.description}</p>
            </div>
        </div>
    );
}
