import type {userProp} from "../../Types/User.ts";

export default function Users(prop:userProp){
    return(<>
        <p>{prop.userInfo.user_id}</p>
        <p>{prop.userInfo.username}</p>
        {prop.userInfo.cars!.map(car =>{
            return <p key={car.model}>{car.model}</p>
        })}
        <p>{prop.userInfo.password}</p>
        <p>{prop.userInfo.role}</p>
    </>);
}