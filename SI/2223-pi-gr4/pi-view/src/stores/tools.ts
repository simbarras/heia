import {reactive, ref} from 'vue'
import {defineStore} from 'pinia'
import {getTools} from "@/service/myApi";

export class Tools {
    name: string = ""

}
const tools_list = reactive<String[]>([])

export const useToolsStore = defineStore('tools', () => {
    getTools().then(res => {

        res.forEach(e => {
            tools_list.push(e.name)
            console.log(e)
        })


    })
    return {tools_list}
})
