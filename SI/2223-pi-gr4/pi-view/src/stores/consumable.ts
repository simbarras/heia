import {reactive, ref} from 'vue'
import {defineStore} from 'pinia'
import {getConsumables} from "@/service/myApi";

export class Consumable {
    name: string = ""

}
const consumable_list = reactive<String[]>([])

export const useConsumableStore = defineStore('consumable', () => {
    getConsumables().then(res => {
        res.forEach(e => {
            consumable_list.push(e.name)
            console.log(e)
        })

    })
    return {consumable_list}
})
