export type AuthFormFields = {
    username: string,
    password: string
}

export type UserEditRequest = {
    username:string,
    role: "ADMIN" | "USER"
}

export type RecipeFormFields = {
    recipeTitle:string,
    description?: string,
    ingredients: string,
    instructions: string,
    category?: "DESSERT" | "SOUP" | "MAIN" | "DRINK",
    pictureSrc?: string,
}