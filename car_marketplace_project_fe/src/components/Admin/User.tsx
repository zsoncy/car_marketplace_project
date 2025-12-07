import type {userProp} from "../../Types/User.ts";
import {userStore} from "../../Stores/UserStore.ts";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {userDelete} from "../../service/AdminService.ts";
import {useEffect} from "react";

export default function User(prop:userProp){
    const currentUser = userStore.getState().user;
    const queryClient = useQueryClient();

    const deleteMutation = useMutation({
        mutationFn: (id:number) => {
            const token = userStore.getState().user?.accessToken;
            if (!token) {
                throw new Error("Not authenticated");
            }
            return userDelete(token, id);
        },
        onSuccess:() =>{
            toast.success("Delete successful");
            queryClient.invalidateQueries({queryKey:["users"]})
        },
        onError: (error) =>{
            if (error instanceof Error){
                toast.error(error.message);
            }else {
                toast.error("Could not complete request");
            }
        }
    })


    const handleDelete = (id:number) =>{
        deleteMutation.mutate(id);
    }

    const isOwnUser = currentUser?.username === prop.userInfo.username;
    const canDelete = !isOwnUser && prop.userInfo.user_id != null;

    useEffect(() => {
        console.log(canDelete);
        console.log(isOwnUser);
        console.log(userStore.getState().user);
        console.log(prop.userInfo.user_id);
    });

    return(
        <tr className="border-b-2 bg-blue-100">
            <td className="px-2 py-3 ">{prop.userInfo.user_id}</td>
            <td className="px-2 py-3 ">{prop.userInfo.username}</td>
            <td className="px-2 py-3 ">{prop.userInfo.role}</td>
            <td><button className={"rounded-2xl px-10 border-2 border-[#2C4278] " +
                "text-[#2C4278] transition delay-50 ease-in-out hover:text-white hover:bg-[#142040] hover:border-[#142040]"}
                        onClick={()=> prop.handleUpdating(prop.userInfo)}>Update</button></td>
           <td className={"px-2 py-3 pr-10 text-right"}>{canDelete ? <button className={"rounded-2xl px-10 border-2 border-[#800000] " +
                "text-[#800000] transition delay-50 ease-in-out hover:text-white hover:bg-[#400000] hover:border-[#400000]"} onClick={() => handleDelete(prop.userInfo.user_id!)}>Delete</button>
            :<></>}</td>
        </tr>
    );
}