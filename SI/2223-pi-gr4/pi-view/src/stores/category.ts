import {reactive, ref} from 'vue'
import {defineStore} from 'pinia'
import {getCategories} from "@/service/myApi";

export class Category {
    name: string = ""

}
const category_list = reactive<String[]>([])

export const useCategoryStore = defineStore('category', () => {
    getCategories().then(res => {
        res.forEach(e => {
            category_list.push(e.name)
            console.log(e)
        })

    })
    return {category_list}
})

// add activity

