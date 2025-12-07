import type { carType } from "../../Types/Car.ts";
import { Link } from "react-router-dom";

export default function Car(carProp: { carInfo: carType }) {
    const {
        id,
        make,
        model,
        year,
        fuel,
        transmission,
        images_src,
        username,
    } = carProp.carInfo;

    const coverSrc =
        images_src && images_src.length > 0 ? images_src[0] : "/placeholder-car.jpg";
    const title = `${make} ${model}`;

    return (
        <div className={"flex flex-col w-60"}>
            <Link
                to={`/dashboard/cars/${id}`}
                className={
                    "text-2xl text-white mb-3 transition delay-50 ease-in-out hover:text-[#3A5A9C]"
                }
            >
                {title}
            </Link>

            <img
                className={" mb-2 transition delay-150 duration-300 ease-in-out hover:scale-130"}
                src={coverSrc}
                alt={`${title} (${year})`}
            />

            <p className={"text-white"}>Year: {year}</p>
            <p className={"text-white"}>Fuel: {fuel}</p>
            <p className={"text-white"}>Transmission: {transmission}</p>
            {username && <p className={"mb-2 text-white"}>Added by: {username}</p>}
        </div>
    );
}
