import {userStore} from "../../Stores/UserStore.ts";
import {useQuery} from "@tanstack/react-query";
import {usersGetAll} from "../../service/AdminService.ts";
import {useEffect, useState} from "react";
import {toast} from "react-toastify";
import User from "./User.tsx";
import type {UserResponse} from "../../Types/User.ts";
import UserEditForm from "./UserEditForm.tsx";

export default function DisplayUsers(){

    const [isUpdating, setIsUpdating] = useState<boolean>(false);
    const [userToUpdate, setUserToUpdate] = useState<UserResponse>();

    const currentUser = userStore.getState().user;
    const {data,error,isError,isLoading} = useQuery<UserResponse[]>({
        queryKey:["users"],
        queryFn: async () =>{
            if (!currentUser?.accessToken){
                throw new Error("Could not authenticate");
            }
            return await usersGetAll(currentUser.accessToken);
        },
        enabled: !!currentUser?.accessToken,
    });
    useEffect(()=>{
        if (isError && error instanceof Error){
            toast.error(error.message);
        }
    },[isError,error])

    useEffect(()=>{
        console.log(data);
    })
    const handleUpdating = (user:UserResponse)=>{
        if (!isUpdating){
            setIsUpdating(true);
            setUserToUpdate(user);
        }else {
            setIsUpdating(false);
        }
    }

    return isLoading ? (
        <p>Loading...</p>
    ):(<div className={"flex flex-col mt-10"}>
        {isUpdating ? <UserEditForm userToUpdate = {userToUpdate}  handleClose = {handleUpdating}/> : <></>}
        <div className="flex justify-center w-auto mx-3">
            <table className="table-auto w-250 border-collapse text-black">
                <thead className={"border-b-2"}>
                <tr>
                    <th scope="col" className="text-left text-white p-2 border-b">ID</th>
                    <th scope="col" className="text-left text-white p-2 border-b">Name</th>
                    <th scope="col" className="text-left text-white p-2 border-b">Role</th>
                </tr>
                </thead>
                <tbody className={"border-b-2"}>
                {(data ?? []).map(r => (
                    <User key={r.user_id} userInfo={r} handleUpdating={handleUpdating}/>
                ))}
                </tbody>
            </table>
        </div>
    </div>);
}