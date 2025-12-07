// ------------- GetAll -------------

import type {UserResponse} from "../Types/User.ts";

export const usersGetAll = async (accessToken:string) => {
    const res = await fetch("/api/recipe/user",{
        headers:{
            Authorization:`Bearer ${accessToken}`
        }
    });
    if (res.ok){
        const response = await res.json();
        console.log(response);
        return response;
    }else{
        const message = await res.text();
        throw new Error(message || "Could not complete request");
    }
}

// ------------- Delete -------------
export const userDelete = async (accessToken:string,id:number) =>{
    const res = await fetch(`/api/recipe/user/${id}`,{
        method:"DELETE",
        headers:{
            Authorization:`Bearer ${accessToken}`,
        }
    });
    if (res.ok){
        return await res.text();
    }else {
        const message = await res.text();
        throw new Error(message || "Request could not be completed");
    }
}

// ------------- Update -------------

export const userUpdate = async (accessToken:string, id:number, data:UserResponse)=> {
    const res = await fetch(`/api/recipe/user/${id}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    });
    if (res.ok) {
        const response = await res.json();
        console.log(response);
        return response;
    } else {
        const message = await res.text();
        throw new Error(message || "Could not complete request");
    }
}