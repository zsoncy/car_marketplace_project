import {type SubmitHandler, useForm} from 'react-hook-form';
import type {AuthFormFields} from "../../Types/FormTypes.ts";
import {Link} from "react-router-dom";
import "../../styles/Auth/AuthForm.css";
import {userLogin} from "../../service/AuthServices.ts";
import {useMutation} from "@tanstack/react-query";
import type {userAuthRequest} from "../../Types/User.ts";
import {userStore} from "../../Stores/UserStore.ts";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";


export default function LoginForm(){
    const{register, handleSubmit, formState:{errors, isSubmitting}} = useForm<AuthFormFields>();
    const navigate = useNavigate();


    const mutation = useMutation({
        mutationFn:(data:userAuthRequest) =>
            userLogin(data),
        onSuccess:(result,variables) =>{
            userStore.getState().stateLogin({
                accessToken: result.accessToken,
                username:variables.username,
                role:result.role
            });
        navigate("/dashboard/home");
        toast.success("Login Successful")
    },
        onError:(error) =>{
            if (error instanceof Error){
                toast.error(error.message);
            }else {
                toast.error("Something went wrong");
            }
        }
    })
    const onSubmit: SubmitHandler<AuthFormFields> = async (data) =>{
        mutation.mutate(data);
    }
    return(
        <div className={"container"}>
                <h1 className={"HeaderText text-4xl font-semibold"}>Sign in</h1>
            <div className={"FormContainer"}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className={"formBody"}>
                        <input {...register("username",{
                            required : "Username is required",
                            minLength: 4
                        })} type={"text"} placeholder={"Username"} className={"textInput"}/>
                        {errors.username &&( <div className={"text-red-600"}>{errors.username.message}</div>)}
                        <input {...register("password",{
                            required :"Password is required"
                        })} type={"password"} placeholder={"Password"} className={"textInput"}/>
                        {errors.password &&( <div className={"text-red-600"}>{errors.password.message}</div>)}
                        <button type={"submit"} disabled={isSubmitting} className={"submitButton"}>
                            {isSubmitting ? "Loading..." : "Submit"}
                        </button>
                    </div>
                </form>
            </div>
            <Link className={"redirectAuthLink text-white"} to={"/register"}>New to the website? Register!</Link>
        </div>


    );

}