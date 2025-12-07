import type {UserResponse} from "../../Types/User.ts";
import {type SubmitHandler, useForm} from "react-hook-form";
import type {UserEditRequest} from "../../Types/FormTypes.ts";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {userStore} from "../../Stores/UserStore.ts";
import {userUpdate} from "../../service/AdminService.ts";
import {toast} from "react-toastify";

export default function UserEditForm(props:{userToUpdate:UserResponse|undefined,handleClose:(user:UserResponse) => void}){

    const {register,handleSubmit,formState:{errors}} = useForm<UserEditRequest>({
        defaultValues :{
            username:props.userToUpdate?.username,
            role:props.userToUpdate?.role
        }
    });
    const queryClient = useQueryClient();

    const updateMutation = useMutation({
        mutationFn:(data:UserEditRequest) => {
            const token = userStore.getState().user?.accessToken;
            const targetId = props.userToUpdate?.user_id;
            if (!token || targetId == null) {
                throw new Error("Missing authentication or user id");
            }
            return userUpdate(token, targetId, data);
        },
        onSuccess:() =>{
            toast.success("The chosen user was updated!");
            queryClient.invalidateQueries({queryKey:["users"]});
        },
        onError:(error)=>{
            if (error instanceof Error){
                toast.error(error.message);
            }else{
                toast.error("Something went wrong");
            }
        }

    })

    const onSubmit: SubmitHandler<UserEditRequest> = async (data:UserEditRequest) => {
        updateMutation.mutate(data)
    }


    return (
        <div className={"flex flex-col justify-center mb-10"}>
            <div className={"flex flex-column justify-between px-50 "}>
                <h1 className={" text-white text-[36px]"}>Update an existing User</h1>
            </div>
            <div className={"border-t border-white w-[80%] m-auto mt-5 mb-10"}></div>

            <div className={"flex justify-center w-auto mx-10 border-2 rounded-2xl self-center justify-items-center p-5 max-w-250 bg-[#AFBEE3]"}>
                <form onSubmit={handleSubmit(onSubmit)} className={"w-full"}>
                    <div className={"flex gap-3 flex-wrap justify-around w-full"}>
                        <div className={"flex flex-col"}>
                            <input{...register("username",{
                                required:"Username is required",
                                minLength:3,
                            })} type={"text"} placeholder={"title"} className={"border-2 bg-white text-center w-80 h-12"} />
                            {errors.username &&( <div className={"text-red-500 w-auto"}>{errors.username.message}</div>)}
                        </div>
                        <label className={"pt-2"}>Choose a role: </label>
                        <select{...register("role",{
                            required:true,
                        })} className={"bg-white border-2 h-12"}>
                            <option value="USER" >User</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                        <button type={"submit"} className={"border-2 border-[#142040] rounded-2xl h-12 w-30 text-white bg-[#2C4278] transition delay-75 ease-in-out hover:bg-[#142040] "}
                        >Update</button>
                        <button className={"border-2 border-[#400000] rounded-2xl h-12 w-30 text-white bg-[#800000] transition delay-75 ease-in-out hover:bg-[#400000]"}
                                onClick={()=>{ if (props.userToUpdate) { props.handleClose(props.userToUpdate) } }}>Close</button>
                    </div>
                </form>
            </div>
        </div>
    );
}