import type {AuthFormFields} from "../../Types/FormTypes.ts";
import {type SubmitHandler, useForm} from 'react-hook-form';
import "../../styles/Auth/AuthForm.css";
import {Link, useNavigate} from "react-router-dom";
import {userRegister} from "../../service/AuthServices.ts";
import {useMutation} from "@tanstack/react-query";
import type {userAuthRequest} from "../../Types/User.ts";
import {userStore} from "../../Stores/UserStore.ts";
import {toast} from "react-toastify";


export default function RegisterForm(){
    const {register,handleSubmit,formState:{errors, isSubmitting}} = useForm<AuthFormFields>();

    const navigate = useNavigate();

    const mutation = useMutation({
        mutationFn:(data:userAuthRequest) => userRegister(data),
        onSuccess:(result,variables) =>{
            userStore.getState().stateLogin({
                username: variables.username,
                role:result.role,
                accessToken: result.accessToken,
            })
            navigate("/dashboard/home");
            toast.success("Successful registration!")
        },
        onError:(error) =>{
            if (error instanceof Error){
                toast.error(error.message);
            }else {
                toast.error("Something went wrong");
            }
        }
    })

    const onSubmit:SubmitHandler<AuthFormFields> = async (data) =>{
        await mutation.mutate(data);
    }

    return(
        <div className={"container"}>
            <h1 className={"HeaderText text-4xl font-semibold"}>Register</h1>
            <div className={"FormContainer"}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className={"formBody"}>
                        <input {...register("username",{
                            required : "Username is required",
                            minLength:{
                                value: 4,
                                message: "The username should be at least 4 characters long"
                            }
                        })} type={"text"} placeholder={"Username"} className={"textInput"}/>
                        {errors.username &&( <div className={"text-[#600000]"}>{errors.username.message}</div>)}
                        <input {...register("password",{
                            required :"Password is required",
                            minLength:{
                                value: 8,
                                message: "The given password is too weak"
                            }
                        })} type={"password"} placeholder={"Password"} className={"textInput"}/>
                        {errors.password &&( <div className={"text-[#600000]"}>{errors.password.message}</div>)}

                        <button type={"submit"} disabled={isSubmitting} className={"submitButton"}>
                            {isSubmitting ? "Loading..." : "Submit"}
                        </button>
                    </div>
                </form>
            </div>
            <Link className={"redirectAuthLink text-white"} to={"/"}>Already have an account? Sign in!</Link>
        </div>
    );
}