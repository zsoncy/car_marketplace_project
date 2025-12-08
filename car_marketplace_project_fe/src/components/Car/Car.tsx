import type { carType } from "../../Types/Car.ts";
import { Link } from "react-router-dom";

export default function Car(carProp: { carInfo: carType }) {
    const {
        id,
        make,
        model,
        year,
        fuel,
        price,
        transmission,
        images_src,
        username,
    } = carProp.carInfo;

    const coverSrc =
        images_src && images_src.length > 0 ? images_src[0] : "/placeholder-car.jpg";
    const title = `${make} ${model}`;

    return (
        <div className={"flex flex-col border-2 border-[#2C4278] rounded-3xl p-5 w-60 min-h-96 mb-1 bg-[#2C4278]"}>
            <Link
                to={`/dashboard/cars/${id}`}
                className={
                    "text-2xl text-white mb-3 text-center transition delay-50 ease-in-out hover:text-[#3A5A9C]"
                }
            >
                {title}
            </Link>

            <img
                className={" my-2 transition delay-150 duration-300 ease-in-out hover:scale-110"}
                src={coverSrc}
                alt={`${title} (${year})`}
            />

            <div className={"text-left text-white ml-3"}>
            <p>Year: {year}</p>
            <p>Fuel: {fuel}</p>
            <p>Transmission: {transmission}</p>
            {username && <p className={" text-white"}>Added by: {username}</p>}
            <p className={"text-right font-bold mt-3"}>Price: {price}</p>
            </div>
        </div>
    );
}
