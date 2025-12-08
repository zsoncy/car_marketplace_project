
import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { userStore } from "../../Stores/UserStore.ts";
import { useEffect, useMemo, useRef, useState, type MouseEvent } from "react";
import { toast } from "react-toastify";
import { getCarById } from "../../service/CarService.ts";
import type { carType } from "../../Types/Car.ts";

/** Main image with mouse-positioned transform origin (zoom on hover) */
function MainImage({ src, alt }: { src: string; alt: string }) {
    const wrapperRef = useRef<HTMLDivElement | null>(null);
    const [origin, setOrigin] = useState<string>("center center");

    const handleMouseMove = (e: MouseEvent<HTMLDivElement>) => {
        const el = wrapperRef.current;
        if (!el) return;

        const rect = el.getBoundingClientRect();
        const x = ((e.clientX - rect.left) / rect.width) * 100; // 0–100%
        const y = ((e.clientY - rect.top) / rect.height) * 100; // 0–100%
        setOrigin(`${x}% ${y}%`);
    };

    const handleMouseLeave = () => {
        setOrigin("center center");
    };

    return (
        <div
            ref={wrapperRef}
            onMouseMove={handleMouseMove}
            onMouseLeave={handleMouseLeave}
            className="inline-block overflow-hidden rounded shadow"
        >
            <img
                src={src}
                alt={alt}
                style={{ transformOrigin: origin }}
                className="
          block
          transition delay-150 duration-300 ease-in-out
          hover:scale-200
          transform-gpu
          select-none
        "
                draggable={false}
            />
        </div>
    );
}

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

    // --- Gallery state ---
    const [activeIdx, setActiveIdx] = useState(0);

    // Safe image list
    const images = useMemo(
        () =>
            Array.isArray(data?.images_src)
                ? data!.images_src.filter((s): s is string => Boolean(s))
                : [],
        [data?.images_src]
    );

    // Reset active index when images change
    useEffect(() => {
        setActiveIdx(0);
    }, [images.length]);

    if (isLoading) {
        return <p>Loading...</p>;
    }

    if (!data) {
        return <p className={"text-white"}>No car found.</p>;
    }

    const title = `${data.make} ${data.model}`;
    const fallback = "/placeholder-car.jpg";
    const coverSrc =
        images.length > 0 ? images[Math.min(activeIdx, images.length - 1)] : fallback;

    return (
        <div className={"flex flex-col text-3xl"}>
            <h1 className={"text-6xl text-center text-white mb-8 mt-8"}>{title}</h1>

            {/* Main image — zoom follows cursor, clipped by wrapper */}
            <div className={"flex justify-center mx-40 mb-5"}>
                <MainImage src={coverSrc} alt={`${title} (${data.year})`} />
            </div>

            {/* Thumbnails — clickable, NO zoom */}
            {images.length > 1 && (
                <div className="flex flex-wrap gap-3 justify-center mx-40 mb-8">
                    {images.map((src, idx) => {
                        const isActive = idx === activeIdx;
                        return (
                            <button
                                key={`${src}-${idx}`}
                                type="button"
                                onClick={() => setActiveIdx(idx)}
                                className={`border rounded overflow-hidden ${
                                    isActive ? "ring-2 ring-blue-500" : "opacity-80 hover:opacity-100"
                                }`}
                                aria-label={`Show image ${idx + 1}`}
                                title={`Image ${idx + 1}`}
                            >
                                <img
                                    src={src}
                                    alt={`${title} (${data.year}) - ${idx + 1}`}
                                    className="block w-24 h-16 object-cover select-none"
                                    loading="lazy"
                                    draggable={false}
                                />
                            </button>
                        );
                    })}
                </div>
            )}

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
                    <p className={"text-right mr-40 mt-5"}>
                        Price:{" "}
                        <strong className={"text-white font-bold text-5xl"}>
                            {data.price.toLocaleString()} EUR
                        </strong>
                    </p>
                )}
                {data.username && (
                    <p>
                        Added by:{" "}
                        <strong className={"text-white font-bold"}>{data.username}</strong>
                    </p>
                )}
            </div>

            <div className={"border-t border-white w-[60%] m-auto mt-10"}></div>

            <div className={"mt-5 mx-40 mb-20"}>
                <h2 className={"text-2xl text-white mt-5"}>
                    <strong>Description:</strong>
                </h2>
                <p className={"text-white"}>{data.description}</p>
            </div>
        </div>
    );
}
